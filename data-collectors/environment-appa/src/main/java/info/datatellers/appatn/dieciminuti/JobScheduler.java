package info.datatellers.appatn.dieciminuti;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;

@Component
public class JobScheduler {
	
	private static final Logger LOG = LogManager.getLogger(JobScheduler.class.getName());
	
	public void pushData() throws Exception {
		LOG.info("Scheduled APPATN 10 minuti execution started");
		
		DataFetcher fetcher = new DataFetcher();
		JsonElement station;
		try {
			station = new JsonParser().parse(fetcher.fetchStations());
			LOG.debug("Parsed JsonElement {}", station);
		} catch (JsonParseException e) {
			LOG.error("Failed to parse JSON, {}", e.getMessage());
			e.printStackTrace();
			throw e;
		}
		
		DataPusher pusher = new DataPusher();
		
		StationList stations = new StationList();
		stations.add(pusher.mapStation((JsonObject) station));
		pusher.syncStations(stations);
				
		
		
		DataMapDto<RecordDtoImpl> rootMap = new DataMapDto<RecordDtoImpl>();
		
		for (StationDto singleStation : stations) {
			JsonArray sensors = ((JsonArray) new JsonParser().parse(fetcher.fetchSensors(singleStation.getId())));
			
			DataMapDto<RecordDtoImpl> stationMap = rootMap.upsertBranch(singleStation.getId());
			
			List<DataTypeDto> syncSensorList = new ArrayList<DataTypeDto>();
			
			for (JsonElement sensor : sensors) {
				
				HashMap<String, DataTypeDto> sensorHashMap = pusher.mapDataType((JsonObject) sensor);
				
				JsonElement phenomenon = ((JsonObject) sensor).get("phenomenon");
				
				DataMapDto<RecordDtoImpl> measurementBranch = stationMap.upsertBranch(sensorHashMap.get(phenomenon.getAsString()).getName());
				DataMapDto<RecordDtoImpl> invBranch = stationMap.upsertBranch(sensorHashMap.get(phenomenon.getAsString() + "_I").getName());
				
				measurementBranch.setName(phenomenon.getAsString());
				invBranch.setName(phenomenon.getAsString());
				
				syncSensorList.add(sensorHashMap.get(phenomenon.getAsString()));
				syncSensorList.add(sensorHashMap.get(phenomenon.getAsString() + "_I"));
			}
			pusher.syncDataTypes(syncSensorList);
		}
		
		rootMap = pusher.mapData(rootMap);
				
		pusher.pushData("Environmentstation", rootMap);
		
		LOG.info("Scheduled APPATN 10 minuti execution completed");
	}
}
