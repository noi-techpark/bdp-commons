package info.datatellers.appatn.dieciminuti;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Date;
import java.util.Calendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;

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
	private DataPusher pusher = new DataPusher();
	private DataFetcher fetcher = new DataFetcher();
	private ResourceBundle rb = ResourceBundle.getBundle("config");

	/**
	 * Takes care of historic data pushing, called once when launched (see applicationContext.xml)
	 */
	public void init() {
		LOG.info("Starting APPATN 10 minuti history collection");

		DataMapDto<RecordDtoImpl> rootMap;
		DataPusher pusher = new DataPusher();
		ResourceBundle rb = ResourceBundle.getBundle("config");

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date from;
		try {
			// set initial date from configuration file
			from = format.parse(rb.getString("odp.data.history.from.10minuti"));

			Date now = new Date();
			//construct base structure
			rootMap = constructRootMap();
			
			while (from.compareTo(now) < 0) {
				
				// set final date
				Calendar c = Calendar.getInstance();
				c.setTime(from);
				c.add(Calendar.DATE, 31);
				Date to = c.getTime();
				
				//clone base data structure
				DataMapDto<RecordDtoImpl> historicMap = new DataMapDto<RecordDtoImpl>();
				historicMap.setName(rootMap.getName());
				historicMap.setData(rootMap.getData());
				for (String stationKey : rootMap.getBranch().keySet()) {
					DataMapDto<RecordDtoImpl> station = historicMap.upsertBranch(stationKey);
					station.setName(rootMap.upsertBranch(stationKey).getName());
					station.setData(rootMap.upsertBranch(stationKey).getData());
					for (String sensorKey : rootMap.upsertBranch(stationKey).getBranch().keySet()) {
						DataMapDto<RecordDtoImpl> sensor = station.upsertBranch(sensorKey);
						sensor.setName(rootMap.upsertBranch(stationKey).upsertBranch(sensorKey).getName());
						sensor.setData(rootMap.upsertBranch(stationKey).upsertBranch(sensorKey).getData());
					}
				}

				LOG.debug("Parsing data from {} to {}", from, to);
				// map data
				historicMap = pusher.mapDataWithDates(historicMap, from, to);
				// push data
				pusher.pushData(rb.getString("odh.station.type"), historicMap);
				// increment interval
				from = to;
			}
		} catch (ParseException e) {
			LOG.error("Unknown value in odp.data.history.from.10minuti. Format should be yyyy-MM-dd, {}",
					e.getMessage());
		}
	}

	/**
	 * Takes care of repetitive data pushing
	 * @throws Exception
	 */
	public void pushData() throws Exception {

		DataMapDto<RecordDtoImpl> rootMap;
		DataPusher pusher = new DataPusher();

		LOG.info("Scheduled APPATN 10 minuti execution started");

		rootMap = constructRootMap();

		rootMap = pusher.mapData(rootMap);

		pusher.pushData(rb.getString("odh.station.type"), rootMap);

		LOG.info("Scheduled APPATN 10 minuti execution completed");
	}

	/**
	 * Constructs the DataMapDto with the basic structure root -> station -> sensor
	 * @return	The basic structure for data pushing
	 */
	private DataMapDto<RecordDtoImpl> constructRootMap() {
		JsonElement station;

		// only one station is available so it is not possible to predict the format for
		// multiple stations
		// in case of more stations the next part will need adaptation (up until END
		// ADAPTATION)
		try {
			station = new JsonParser().parse(fetcher.fetchStations());
			LOG.debug("Parsed JsonElement {}", station);
		} catch (JsonParseException e) {
			LOG.error("Failed to parse JSON, {}", e.getMessage());
			e.printStackTrace();
			throw e;
		}

		StationList stations = new StationList();
		stations.add(pusher.mapStation((JsonObject) station));

		// END ADAPTATION

		pusher.syncStations(stations);

		DataMapDto<RecordDtoImpl> map = new DataMapDto<RecordDtoImpl>();

		for (StationDto singleStation : stations) {
			JsonArray sensors = ((JsonArray) new JsonParser().parse(fetcher.fetchSensors(singleStation.getId())));

			DataMapDto<RecordDtoImpl> stationMap = map.upsertBranch(singleStation.getId());

			List<DataTypeDto> syncSensorList = new ArrayList<DataTypeDto>();

			for (JsonElement sensor : sensors) {

				HashMap<String, DataTypeDto> sensorHashMap = pusher.mapDataType((JsonObject) sensor);

				JsonElement phenomenon = ((JsonObject) sensor).get("phenomenon");

				DataMapDto<RecordDtoImpl> measurementBranch = stationMap
						.upsertBranch(sensorHashMap.get(phenomenon.getAsString()).getName());
				DataMapDto<RecordDtoImpl> invBranch = stationMap
						.upsertBranch(sensorHashMap.get(phenomenon.getAsString() + "_I").getName());

				measurementBranch.setName(phenomenon.getAsString());
				invBranch.setName(phenomenon.getAsString());

				syncSensorList.add(sensorHashMap.get(phenomenon.getAsString()));
				syncSensorList.add(sensorHashMap.get(phenomenon.getAsString() + "_I"));
			}
			pusher.syncDataTypes(syncSensorList);
		}

		return map;
	}
}
