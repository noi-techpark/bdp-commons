package it.bz.idm.bdp.rwis;

import static it.bz.idm.bdp.rwis.RWISFetch.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.tempuri.ArrayOfInt;

import cleanroadsdatatype.cleanroadswebservices.GetDataResult;
import cleanroadsdatatype.cleanroadswebservices.GetDataTypesResult;
import cleanroadsdatatype.cleanroadswebservices.GetDataTypesResult.XmlDataType;
import cleanroadsdatatype.cleanroadswebservices.GetMetadataStationResult;
import it.bz.idm.bdp.dto.BDPAdapter;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;

@Component
@PropertySource("classpath:/META-INF/spring/types.properties")
public class JobScheduler {
	
	private static final int PERIOD = 600;

	@Autowired
	private CleanroadsPusher pusher;
	
	@Autowired
	private BDPAdapter adapter;
	
	@Autowired
	private Environment env;
	
	public void syncStations(){
		StationList dtos = new StationList();
        for (Integer stid : getStationID().getInt()) {
    		GetMetadataStationResult mres = getMetadataStation((int) stid);
        	StationDto dto = adapter.convert2StationDto(mres);
        	dtos.add(dto);
        }
        pusher.syncStations(dtos);
	}
	public void syncDataTypes(){
        List<GetDataTypesResult.XmlDataType> types = getDataTypes().getXmlDataType();
        List<DataTypeDto> dtos = adapter.convert2DatatypeDtos(types);
        pusher.syncDataTypes(dtos);
	}
	public void sendData(){
        List<Integer> stations = getStationID().getInt();       
        List<GetDataTypesResult.XmlDataType> types = getDataTypes().getXmlDataType();
        ArrayOfInt what = new ArrayOfInt();
        for (int i = 0; i < types.size(); i++) {
            XmlDataType t = types.get(i);
            what.getInt().add(t.getId()); // note hack documented in org.tempuri.ArrayOfInt (!)
        }
        DataMapDto<RecordDtoImpl> dto = new DataMapDto<>();
        for (int stid : stations) {
            GetDataResult res = getData(stid, what);
            List<GetDataResult.XmlRwData> datalist = res.getXmlRwData();
			DataMapDto<RecordDtoImpl> stationData = new DataMapDto<>();
	        for (int i = 0; i < datalist.size(); i++) {
	            GetDataResult.XmlRwData d = datalist.get(i);
	            long localTime = d.getTs().toGregorianCalendar().getTimeInMillis();
				String typeId = env.getProperty(String.valueOf(d.getId()));
	            SimpleRecordDto record = new SimpleRecordDto(localTime,d.getValore());
	            record.setPeriod(PERIOD);
	            if (stationData.getBranch().containsKey(typeId))
	            	stationData.getBranch().get(typeId).getData().add(record);
				else {
					List<RecordDtoImpl> newSet = new ArrayList<RecordDtoImpl>();
					newSet.add(record);
					stationData.getBranch().put(typeId, new DataMapDto<>(newSet));
				}
	        }
			dto.getBranch().put("rwis"+stid, stationData);
			
        }
		pusher.pushData(dto);
	}
}
