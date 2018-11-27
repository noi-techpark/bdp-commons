package it.bz.idm.bdp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationList;
import it.bz.idm.bdp.service.dto.ChargerDtoV2;

@Service
public class PushScheduler{
	
	@Autowired
	private DataRetrieverAPIV2 retrieverV2;

	@Autowired
	private ChargePusher pusher;
	public void syncStationsV2(){
		List<ChargerDtoV2> fetchedSations = retrieverV2.fetchStations();
		StationList data = pusher.map2bdp(fetchedSations);
		StationList plugs = pusher.mapPlugs2Bdp(fetchedSations);
		if (data != null && plugs != null) {
			pusher.syncStations(data);
			pusher.syncStations("EChargingPlug", plugs);
		}
	}
	public void pushChargerDataV2(){
		List<ChargerDtoV2> stations = retrieverV2.fetchStations();
		DataMapDto<RecordDtoImpl> map = pusher.mapData(stations);
		DataMapDto<RecordDtoImpl> plugRec = pusher.mapPlugData2Bdp(stations);
		if (map != null && plugRec != null){
			pusher.pushData(map);
			pusher.pushData("EChargingPlug",plugRec);
		}
	}
	public void syncDataTypes(){
		List<DataTypeDto> types = pusher.getDataTypes();
		if (types != null && types != null){
			pusher.syncDataTypes("EChargingPlug",types);
		}
	}
}