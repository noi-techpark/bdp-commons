package info.datatellers.appatn.tenminutes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	 * Takes care of historic data pushing, called once when launched (see
	 * applicationContext.xml)
	 */
	public void init() {
		LOG.info("Starting APPATN ten minutes history collection");
		LOG.debug("Started at {}", new Date());

		DataMapDto<RecordDtoImpl> rootMap;
		DataPusher pusher = new DataPusher();
		ResourceBundle rb = ResourceBundle.getBundle("config");

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date from;
		try {
			// set initial date from configuration file
			from = format.parse(rb.getString("odp.data.history.from.tenminutes"));

			Date now = new Date();
			// construct base structure
			rootMap = constructRootMap();

			while (from.compareTo(now) < 0) {

				// set final date
				Calendar c = Calendar.getInstance();
				c.setTime(from);
				c.add(Calendar.DATE, 31);
				Date to = c.getTime();

				// clone base data structure
				DataMapDto<RecordDtoImpl> historicMap = new DataMapDto<RecordDtoImpl>();
				historicMap.setName(rootMap.getName());
				historicMap.setData(rootMap.getData());
				for (String stationKey : rootMap.getBranch().keySet()) {
					DataMapDto<RecordDtoImpl> station = historicMap.upsertBranch(stationKey);
					station.setName(rootMap.upsertBranch(stationKey).getName());
					for (String sensorKey : rootMap.upsertBranch(stationKey).getBranch().keySet()) {
						DataMapDto<RecordDtoImpl> sensor = station.upsertBranch(sensorKey);
						sensor.setName(rootMap.upsertBranch(stationKey).upsertBranch(sensorKey).getName());
					}
				}

				for (Map.Entry<String, DataMapDto<RecordDtoImpl>> entry : historicMap.getBranch().entrySet()) {
					LOG.info("historicMap Station: " + entry.getKey());
					for (Map.Entry<String, DataMapDto<RecordDtoImpl>> typeEntry : entry.getValue().getBranch()
							.entrySet()) {
						LOG.info("\tDatatype: " + typeEntry.getKey() + " records:"
								+ typeEntry.getValue().getData().size());
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
			LOG.error("Unknown value in odp.data.history.from.tenminutes. Format should be yyyy-MM-dd, {}",
					e.getMessage());
		}
	}

	/**
	 * Takes care of repetitive data pushing and filling possible data gaps
	 * 
	 * @throws Exception
	 */
	public void pushData() throws Exception {

		DataMapDto<RecordDtoImpl> rootMap;
		DataPusher pusher = new DataPusher();

		LOG.info("Scheduled APPATN ten minutes execution started");
		LOG.debug("Started at {}", new Date());

		rootMap = constructRootMap();
		
		// get date of last record for most outdated sensor
		
		Date from = null;

		for (String stationIndex : rootMap.getBranch().keySet()) {
			for (String sensorIndex : rootMap.upsertBranch(stationIndex).getBranch().keySet()) {
				Date lastRecord = (Date) pusher.getDateOfLastRecord(stationIndex, sensorIndex, 600);
				if (from == null || from.compareTo(lastRecord) > 0)
					from = lastRecord;
			}
		}

		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 1);
		Date now = c.getTime();
		
		c.setTime(from);
		// start from next 10 minutes (skips a day if the last record is at 23:50, saves computation)
		c.add(Calendar.MINUTE, 10);
		from = c.getTime();

		// only one station is available so it is not possible to predict the format for
		// multiple stations
		// in case of more stations the next part may need adaptation

		// if the interval of the last record is more than one hour, we need to gather
		// the interval of data missing.
		if ((now.getTime() - from.getTime()) > 3600000) {
			LOG.info("There seems to be missing data from {}, recollecting", from);
			
			c.add(Calendar.DATE, 31);
			Date to = c.getTime().compareTo(now) >= 0 ? now : c.getTime();

			while (from.compareTo(now) < 0) {
				LOG.debug("Collecting from {} to {}", from, to);
				rootMap = pusher.mapDataWithDates(rootMap, from, to);
				pusher.pushData(rb.getString("odh.station.type"), rootMap);
				rootMap = constructRootMap();
				from = to;
				c.add(Calendar.DATE, 31);
				to = c.getTime().compareTo(now) >= 0 ? now : c.getTime();
			}

		} else {
			LOG.debug("All records are up to date, executing as usual");
			rootMap = pusher.mapData(rootMap);
			pusher.pushData(rb.getString("odh.station.type"), rootMap);
		}

		LOG.info("Scheduled APPATN ten minutes execution completed");
		LOG.debug("Completed at {}", new Date());
	}

	/**
	 * Constructs the DataMapDto with the basic structure root -> station -> sensor
	 * 
	 * @return The basic structure for data pushing
	 */
	private DataMapDto<RecordDtoImpl> constructRootMap() {
		JsonElement station;

		// only one station is available so it is not possible to predict the format for
		// multiple stations
		// in case of more stations the next part will need adaptation (up until END
		// ADAPTATION)
		try {
			station = new JsonParser().parse(fetcher.fetchStations());
			LOG.debug("Parsed JsonElement (station) {}", station);
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
			JsonArray sensors = ((JsonArray) new JsonParser().parse(fetcher
					.fetchSensors(singleStation.getId().substring(rb.getString("odh.station.origin").length() + 1))));

			LOG.debug("Parsed JsonArray (sensors) {}", sensors);

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

		for (Map.Entry<String, DataMapDto<RecordDtoImpl>> entry : map.getBranch().entrySet()) {
			LOG.info("rootMap (constructRootMap()) Station: " + entry.getKey());
			for (Map.Entry<String, DataMapDto<RecordDtoImpl>> typeEntry : entry.getValue().getBranch().entrySet()) {
				LOG.info("\tDatatype: " + typeEntry.getKey() + " records:" + typeEntry.getValue().getData().size());
			}
		}

		return map;
	}
}
