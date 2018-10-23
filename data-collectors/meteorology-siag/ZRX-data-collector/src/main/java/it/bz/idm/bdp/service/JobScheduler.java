package it.bz.idm.bdp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationList;

@Component
public class JobScheduler {
	@Autowired
	private MeteoPusher pusher;
	
	@Autowired
	private MeteoUtil util;
	
	public void pushDataToCollector() {
		String json = util.getMeteoDataAsJsonString();
		if (json != null){
			DataMapDto<RecordDtoImpl> segments = util.getRecordsFromJson(json);
			pusher.pushData(segments);
		}
	}
	public void syncDataTypes(){
		String json = util.getMeteoDataAsJsonString();
		if (json != null){
			List<DataTypeDto> dtos = util.getDataTypesFromJson(json);
			pusher.syncDataTypes(dtos);
		}
	}
	public void syncStations(){
		String json = util.getMeteoDataAsJsonString();
		if (json != null){
			StationList stations = util.getStationsFromJson(json);
			pusher.syncStations(stations);
		}
	}

}