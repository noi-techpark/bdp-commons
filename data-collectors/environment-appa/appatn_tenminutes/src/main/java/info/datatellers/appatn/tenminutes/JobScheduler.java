package info.datatellers.appatn.tenminutes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Date;
import java.util.Calendar;
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

@Component("jobScheduler")
public class JobScheduler {

	private static final Logger LOG = LogManager.getLogger(JobScheduler.class.getName());

	public void collectData() {
		try {
			pushData();
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
	}

	@SuppressWarnings("Duplicates")
	public static void pushData() throws Exception {

		DataMapDto<RecordDtoImpl> rootMap;
		DataPusher pusher = new DataPusher();

		ResourceBundle rb = ResourceBundle.getBundle("config");

		LOG.info("Scheduled APPATN ten minutes execution started");
		LOG.debug("Started at {}", new Date());

		rootMap = constructRootMap();

		String startDate = rb.getString("odp.data.history.from.tenminutes");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date from = format.parse(startDate);
		Date lastDatabaseEntryDate = format.parse("1970-01-01");

		for (String stationIndex : rootMap.getBranch().keySet()) {
			for (String sensorIndex : rootMap.upsertBranch(stationIndex).getBranch().keySet()) {
				Date lastRecord = (Date) pusher.getDateOfLastRecord(stationIndex, sensorIndex, 600);
				lastDatabaseEntryDate = lastRecord;
				if (!lastRecord.toString().contains("1970"))
				{
					from = lastRecord;
				}
			}
		}

		Calendar c = Calendar.getInstance();
		Date today = c.getTime();

		c.setTime(from);
		// start from next 10 minutes (skips a day if the last record is at 23:50, saves computation)
		c.add(Calendar.MINUTE, 10);
		from = c.getTime();

		// only one station is available so it is not possible to predict the format for
		// multiple stations
		// in case of more stations the next part may need adaptation

		// if the interval of the last record is more than one hour, we need to gather
		// the interval of data missing.
		if ((today.getTime() - from.getTime()) > 3600000) {
			LOG.info("There seems to be missing data from {}, recollecting", from);

			c.add(Calendar.DATE, 30);
			Date to = c.getTime().compareTo(today) >= 0 ? today : c.getTime();

			while (from.compareTo(today) < 0) {
				LOG.debug("Collecting from {} to {}", from, to);

				c.setTime(to);
				c.add(Calendar.DATE, 1);
				to = c.getTime();
				rootMap = pusher.mapDataWithDates(rootMap, from, to);

				pusher.pushData(rb.getString("odh.station.type"), rootMap);
				rootMap = constructRootMap();
				from = to;
				c.add(Calendar.DATE, 29);
				to = c.getTime();
			}

		} else {
			LOG.debug("All records are up to date, executing as usual");
			c.setTime(today);
			c.add(Calendar.DATE, 1);
			Date tomorrow = c.getTime();
			rootMap = pusher.mapDataWithDates(rootMap, lastDatabaseEntryDate, tomorrow);
			pusher.pushData(rb.getString("odh.station.type"), rootMap);
		}

		LOG.info("Scheduled APPATN ten minutes execution completed");
		LOG.debug("Finished at {}", new Date());
	}

	@SuppressWarnings("Duplicates")
	private static DataMapDto<RecordDtoImpl> constructRootMap() {
		ResourceBundle rb = ResourceBundle.getBundle("config");
		DataPusher pusher = new DataPusher();
		DataFetcher fetcher = new DataFetcher();
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
			LOG.debug("The following rootMap (constructRootMap()) should contain no observations. Station: " + entry.getKey());
			for (Map.Entry<String, DataMapDto<RecordDtoImpl>> typeEntry : entry.getValue().getBranch().entrySet()) {
				LOG.debug("\tDatatype: " + typeEntry.getKey() + ". Records:" + typeEntry.getValue().getData().size());
			}
		}

		return map;
	}

	private static boolean dateComparison(Date from, Date to, Date target)
	{
		return (from.before(target) && to.after(target));
	}
}
