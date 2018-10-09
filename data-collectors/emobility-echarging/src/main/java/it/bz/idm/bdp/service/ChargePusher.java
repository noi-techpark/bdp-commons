package it.bz.idm.bdp.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationList;
import it.bz.idm.bdp.dto.emobility.ChargingPointsDtoV2;
import it.bz.idm.bdp.dto.emobility.EchargingPlugDto;
import it.bz.idm.bdp.dto.emobility.EchargingStationDto;
import it.bz.idm.bdp.json.JSONPusher;
import it.bz.idm.bdp.service.dto.ChargerDtoV2;

@Service
public class ChargePusher extends JSONPusher {

	private static final String ORIGIN_KEY = "app.dataOrigin";

	@Autowired
	private Environment env;
	@Override
	public String initIntegreenTypology() {
		return "EChargingStation";
	}

	@Override
	public <T> DataMapDto<RecordDtoImpl> mapData(T rawData) {
		@SuppressWarnings("unchecked")
		List<ChargerDtoV2> data = (List<ChargerDtoV2>) rawData;
		if (data == null)
			return null;
		DataMapDto<RecordDtoImpl> map = new DataMapDto<>();
		Date now = new Date();
		for(ChargerDtoV2 dto: data ){
			DataMapDto<RecordDtoImpl> recordsByType = new DataMapDto<RecordDtoImpl>();
			Integer availableStations=0;
			for (ChargingPointsDtoV2 point:dto.getChargingPoints()){
				List<RecordDtoImpl> records = new ArrayList<RecordDtoImpl>();
				if (env.getRequiredProperty("plug.status.available").equals(point.getState()))
					availableStations++;
				SimpleRecordDto record = new SimpleRecordDto(now.getTime(),availableStations.doubleValue());
				record.setPeriod(env.getProperty("app.period", Integer.class));
				records.add(record);
				DataMapDto<RecordDtoImpl> dataSet = new DataMapDto<>(records);
				recordsByType.getBranch().put(DataTypeDto.NUMBER_AVAILABE, dataSet);
			}
			map.getBranch().put(dto.getId(), recordsByType);
		}
		return map;
	}

	public StationList map2bdp(List<ChargerDtoV2> fetchedSations) {
		StationList stations = new StationList();
		for (ChargerDtoV2 dto : fetchedSations){
			EchargingStationDto s = new EchargingStationDto();
			s.setId(dto.getId());
			s.setLongitude(dto.getLongitude());
			s.setLatitude(dto.getLatitude());
			s.setName(dto.getName());
			s.setCity(dto.getPosition().getCity());
			s.setProvider(dto.getProvider());
			s.setCapacity(dto.getChargingPoints().size());
			s.setState(dto.getState());
			s.setAccessInfo(dto.getAccessInfo());
			s.setFlashInfo(dto.getFlashInfo());
			s.setLocationServiceInfo(dto.getLocationServiceInfo());
			s.setPaymentInfo(dto.getPaymentInfo());
			s.setAddress(dto.getAddress());
			s.setReservable(dto.getIsReservable());
			s.setAccessType(dto.getAccessType());
			s.setCategories(dto.getCategories());
			s.setOrigin(env.getProperty(ORIGIN_KEY));
			stations.add(s);
		}
		return stations;
	}

	public StationList mapPlugs2Bdp(List<ChargerDtoV2> fetchedStations) {
		if (fetchedStations == null)
			return null;
		StationList stations = new StationList();
		for (ChargerDtoV2 dto : fetchedStations){
			for(ChargingPointsDtoV2 point:dto.getChargingPoints()){
				EchargingPlugDto s = new EchargingPlugDto();
				s.setId(dto.getId()+ "-" + point.getOutlets().get(0).getId());
				s.setLongitude(dto.getLongitude());;
				s.setLatitude(dto.getLatitude());
				s.setName(dto.getName()+"-"+point.getId());
				s.setParentStation(dto.getCode());
				s.setOutlets(point.getOutlets());
				s.setOrigin(env.getProperty(ORIGIN_KEY));
				stations.add(s);
			}
		}
		return stations;
	}

	public DataMapDto<RecordDtoImpl> mapPlugData2Bdp(List<ChargerDtoV2> data) {
		if (data == null)
			return null;
		DataMapDto<RecordDtoImpl> map = new DataMapDto<>();
		Date now = new Date();
		for(ChargerDtoV2 dto: data ){
			for (ChargingPointsDtoV2 point:dto.getChargingPoints()){
				DataMapDto<RecordDtoImpl> recordsByType = new DataMapDto<RecordDtoImpl>();
				List<RecordDtoImpl> records = new ArrayList<RecordDtoImpl>();
				SimpleRecordDto record = new SimpleRecordDto();
				record.setTimestamp(now.getTime());
				record.setValue(point.getState().equals("AVAILABLE") ? 1. : 0);
				record.setPeriod(env.getProperty("app.period", Integer.class));
				records.add(record);
				recordsByType.getBranch().put("echarging-plug-status", new DataMapDto<RecordDtoImpl>(records));
				map.getBranch().put(dto.getId()+"-"+point.getOutlets().get(0).getId(), recordsByType);
			}
		}
		return map;
	}
}
