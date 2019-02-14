package info.datatellers.appatn.tenminutes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import info.datatellers.appatn.helpers.APPADataFormatException;
// import info.datatellers.appatn.helpers.CoordinateHelper;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.json.JSONPusher;

public class DataPusher extends JSONPusher {

	private ResourceBundle rb = ResourceBundle.getBundle("config");

	private static final Logger LOG = LogManager.getLogger(DataPusher.class.getName());

	private DataMapDto<RecordDtoImpl> rootMap = null;

	/**
	 * Creates the necessary structure to push data via JSONPusher pushData(String,
	 * DataMapDto<? extends RecordDtoImpl>) method. This implementation is used for
	 * historic data
	 *
	 * @param data The already constructed DataMapDto to the levels of stations and
	 *             sensors.
	 * @param from The date to recover data from
	 * @param to   The date limit to recover data
	 * @return The DataMapDto with data filled into sensors as values, ready to be
	 *         pushed
	 */
	@SuppressWarnings("unchecked") // checked with try/catch
	public <T> DataMapDto<RecordDtoImpl> mapDataWithDates(T data, Date from, Date to) {

		boolean historic = false;

		if (from != null && to != null) {
			historic = true;
		}

		try {
			this.rootMap = (DataMapDto<RecordDtoImpl>) data;
		} catch (ClassCastException e) {
			LOG.error("Unexpected input format while mapping data, {}", e.getMessage());
			LOG.debug("Input format class is {}", data.getClass());
			return null;
		}

		for (int i = 0; i < this.rootMap.getBranch().keySet().size(); i++) {
			String stationKey = (String) this.rootMap.getBranch().keySet().toArray()[i];
			DataMapDto<RecordDtoImpl> station = this.rootMap.getBranch().get(stationKey);
			DataFetcher fetcher = new DataFetcher();
			for (int a = 0; a < this.rootMap.getBranch().get(this.rootMap.getBranch().keySet().toArray()[i]).getBranch()
					.keySet().size(); a = a + 2) {

				List<RecordDtoImpl> records = new ArrayList<RecordDtoImpl>();

				List<RecordDtoImpl> recordsInv = new ArrayList<RecordDtoImpl>();

				DataMapDto<RecordDtoImpl> measureMap = station.getBranch()
						.get(station.getBranch().keySet().toArray()[a]);

				DataMapDto<RecordDtoImpl> measureMapInv = station.getBranch()
						.get(station.getBranch().keySet().toArray()[a + 1]);

				// SET VALUE FOR A

				SimpleRecordDto measure;

				// SET VALUE FOR A + 1

				SimpleRecordDto measureInv;

				JsonObject observations;
				JsonObject sensorValues;
				try {
					String measurementId = measureMap.getName();

					String stationId = stationKey.substring(rb.getString("odh.station.origin").length() + 1);

					if (historic) {
						observations = ((JsonObject) new JsonParser()
								.parse(fetcher.fetchHistoricData(stationId, measurementId, from, to)));
					} else {
						observations = ((JsonObject) new JsonParser()
								.parse(fetcher.fetchData(stationId, measurementId)));
					}

					sensorValues = ((JsonObject) ((JsonObject) ((JsonObject) observations.get("response"))
							.get("stations")).get(stationId));
					try {
						sensorValues = ((JsonObject) ((JsonObject) sensorValues.get("sensors")).get(measurementId));

						for (int b = 0; b < sensorValues.keySet().size(); b++) {

							try {
								String key = (String) sensorValues.keySet().toArray()[b];

								// timestamp in unix doesn't need milliseconds, but sql does
								Long timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").parse(key).getTime();

								Double measureValue = Double
										.parseDouble(((JsonObject) sensorValues.get(key)).get("value").getAsString());

								Double measureAvailability = Double.parseDouble(
										((JsonObject) sensorValues.get(key)).get("availability").getAsString());

								if (measureValue >= 0 && measureAvailability >= 0) {
									measure = new SimpleRecordDto(timestamp, measureValue, 600);

									measureInv = new SimpleRecordDto(timestamp, measureAvailability, 600);
									records.add(measure);
									recordsInv.add(measureInv);
								} else {
									throw new APPADataFormatException("Expected non-negative data, received "
											+ measureValue + " and " + measureAvailability + " from sensor " + measureMap.getName() + " at timestamp " + key);
								}

							} catch (ParseException e) {
								LOG.error("Unexpected date format, {}", e.getMessage());
							} catch (NumberFormatException | APPADataFormatException e) {
								LOG.warn("Unexpected sensor value. {}", e.getMessage());
							}
						}

					} catch (ClassCastException e) {
						LOG.info("Station has no value for such sensor");
						LOG.debug("Station id: " + stationId + ", sensor id: " + measurementId);
					} catch (NullPointerException e) {
						LOG.debug("station id: {}", stationId);
						LOG.error("sensorValues is {}", e.getMessage());
					}

				} catch (ClassCastException e) {
					LOG.error("Unknown or unexpected observation format, {}", e.getMessage());
				}

				if (records.isEmpty() || recordsInv.isEmpty()) {
					station.getBranch().remove(station.getBranch().keySet().toArray()[a]);
					station.getBranch().remove(station.getBranch().keySet().toArray()[a + 1]);
					LOG.debug("Current record is empty, {}", records);
				} else {
					measureMap.setData(records);
					measureMapInv.setData(recordsInv);
				}

			}
		}

		return this.rootMap;

	}

	/**
	 * Creates the necessary structure to push data via JSONPusher pushData(String,
	 * DataMapDto<? extends RecordDtoImpl>) method.
	 *
	 * @param data The already constructed DataMapDto to the levels of stations and
	 *             sensors.
	 * @return The DataMapDto with data filled into sensors as values, ready to be
	 *         pushed
	 */
	@Override
	public <T> DataMapDto<RecordDtoImpl> mapData(T data) {
		return mapDataWithDates(data, null, null);
	}

	/**
	 * Maps a JsonObject into a HashMap that contains as key the original id of the
	 * data type that can be used for the API calls but is not needed while pushing
	 * data to the writer
	 *
	 * @param rawDataType Gson result of the API call to /sensors
	 * @return data types mapped as DataTypeDto with their source id as key
	 */
	public HashMap<String, DataTypeDto> mapDataType(JsonObject rawDataType) {
		rb = ResourceBundle.getBundle("config");
		DataTypeDto dataType = new DataTypeDto();
		DataTypeDto dataTypeAvailability = new DataTypeDto();
		try {
			dataType.setName(rawDataType.get("description").getAsString());
			dataType.setUnit(rawDataType.get("uom").getAsString());
			dataType.setDescription(rb.getString("odp.unit.description.tenminutes"));
			dataType.setRtype(rb.getString("odp.unit.rtype.tenminutes"));

			dataTypeAvailability.setName(
					rawDataType.get("description").getAsString() + rb.getString("odp.unit.availability.tenminutes"));
			dataTypeAvailability.setUnit("%");
			dataTypeAvailability.setDescription(rb.getString("odp.unit.description.tenminutes.availability"));
			dataTypeAvailability.setRtype(rb.getString("odp.unit.rtype.tenminutes.availability"));
		} catch (JsonParseException e) {
			LOG.error("ERROR: Data type parsing error, {}", e.getMessage());
			e.printStackTrace();
		}

		LinkedHashMap<String, DataTypeDto> result = new LinkedHashMap<String, DataTypeDto>();
		result.put(rawDataType.get("phenomenon").getAsString(), dataType);
		result.put(rawDataType.get("phenomenon").getAsString() + "_I", dataTypeAvailability);

		return result;
	}

	/**
	 * Maps a JsonObject into a StationDto that contains the station data
	 *
	 * @param fetchedStation Gson result of the API call to /stations
	 * @return StationDto filled with station info
	 */
	public StationDto mapStation(JsonObject fetchedStation) {
		rb = ResourceBundle.getBundle("config");
		try {
			String latitude = ((JsonObject) ((JsonObject) ((JsonArray) fetchedStation.get("features")).get(0))
					.get("properties")).get("lat").getAsString();

			String longitude = ((JsonObject) ((JsonObject) ((JsonArray) fetchedStation.get("features")).get(0))
					.get("properties")).get("lon").getAsString();

			StationDto station = new StationDto(
					rb.getString("odh.station.origin") + "_"
							+ ((JsonObject) ((JsonObject) ((JsonArray) fetchedStation.get("features")).get(0))
							.get("properties")).get("id").getAsString(),
					((JsonObject) ((JsonObject) ((JsonArray) fetchedStation.get("features")).get(0)).get("properties"))
							.get("name").getAsString(),
					Double.parseDouble(latitude), Double.parseDouble(longitude));

			station.setStationType(rb.getString("odh.station.type"));
			station.setOrigin(rb.getString("odh.station.origin"));
			station.setCoordinateReferenceSystem((rb.getString("odh.station.projection")));

			return station;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			LOG.error("ERROR: Station LAT/LON parsing error, {}", e.getMessage());
			return null;
		} catch (JsonParseException e) {
			e.printStackTrace();
			LOG.error("ERROR: Station format parsing error, {}", e.getMessage());
			return null;
		}

	}

	@Override
	public String initIntegreenTypology() {
		rb = ResourceBundle.getBundle("config");
		return rb.getString("odh.station.type");
	}

}
