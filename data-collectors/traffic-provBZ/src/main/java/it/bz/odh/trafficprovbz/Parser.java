package it.bz.odh.trafficprovbz;

import com.jayway.jsonpath.JsonPath;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.odh.trafficprovbz.dto.AggregatedDataDto;
import it.bz.odh.trafficprovbz.dto.MetadataDto;
import it.bz.odh.trafficprovbz.dto.PassagesDataDto;
import net.minidev.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Parser {

	private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

	/**
	 * This is a function to create a station, either of type traffic or bluetooth
	 *
	 * @param metadataDto          where the station is stored
	 * @param otherFields          is a jsonobject where all other fields that
	 *                             Jackson did not match are stored
	 * @param lane                 is a param set if it´s a traffic sensor and
	 *                             viceversa for bluetooth sensor containing the
	 *                             info of the lane
	 * @param classificationSchema is a hash map where the schema is stored
	 * @param stationType          is a string either of type traffic or bluetooth
	 * @return station containing the data from the params
	 */
	public static StationDto createStation(MetadataDto metadataDto, JSONObject otherFields,
			LinkedHashMap<String, String> lane, LinkedHashMap<String, String> classificationSchema,
			String stationType) {
		Double lat = JsonPath.read(otherFields, "$.GeoInfo.Latitudine");
		Double lon = JsonPath.read(otherFields, "$.GeoInfo.Longitudine");

		// metadataDto.setOtherField("SchemaDiClassificazione",
		// classificationSchema != null ? classificationSchema.toString() : null);
		String stationId = metadataDto.getName();
		String stationName = metadataDto.getName();
		int laneId = -1;
		if (lane != null) {
			String description = JsonPath.read(lane, "$.Descrizione");
			laneId = JsonPath.read(lane, "$.Id");

			stationId = metadataDto.getName() + ":" + description;
			stationName = metadataDto.getName() + ":" + description;
			// save odhId to otherFields to use in syncJobTrafficMeasurements()
			// -1 because in aggregated data lanes start from 0
			metadataDto.addLane(laneId - 1, stationId);
		}
		StationDto station = new StationDto(stationId, stationName, lat, lon);
		station.setStationType(stationType);
		station.setMetaData(createMetadata(otherFields, laneId));
		return station;
	}

	/**
	 * This is a function where the traffic data of the stations is inserted into
	 * the stationmap
	 *
	 * @param aggregatedDataDtos is an array where the traffic data is stored
	 * @param period             is an integer where the period in seconds is stored
	 * @param stationMap         is a map where the traffic data is stored
	 * @throws ParseException is thrown if parsing causes an error
	 */
	public static void insertDataIntoStationMap(AggregatedDataDto[] aggregatedDataDtos, Integer period,
			DataMapDto<RecordDtoImpl> stationMap, Integer laneId) throws ParseException {

		List<String> trafficDataTypes = Parser.getTrafficDataTypes();

		@SuppressWarnings("unchecked")
		DataMapDto<RecordDtoImpl>[] metricMaps = new DataMapDto[trafficDataTypes.size()];

		for (int i = 0; i < trafficDataTypes.size(); i++) {
			DataMapDto<RecordDtoImpl> metricMap = stationMap.upsertBranch(trafficDataTypes.get(i));
			metricMaps[i] = metricMap;
		}

		for (AggregatedDataDto aggregatedDataDto : aggregatedDataDtos) {
			if (aggregatedDataDto.getLane().equals((laneId) + "")) {
				Long timestamp = formatter.parse(aggregatedDataDto.getDate()).getTime();
				JSONObject otherFields = new JSONObject(aggregatedDataDto.getOtherFields());

				if (otherFields.containsKey("TotaliPerClasseVeicolare")) {
					addMeasurementToMap(metricMaps[0],
							new SimpleRecordDto(timestamp, aggregatedDataDto.getTotalTransits(), period));

					LinkedHashMap<String, Integer> classes = JsonPath.read(otherFields, "$.TotaliPerClasseVeicolare");
					Set<String> keys = classes.keySet();
					for (String key : keys) {
						addMeasurementToMap(metricMaps[Integer.parseInt(key)],
								new SimpleRecordDto(timestamp, classes.get(key), period));
					}
					addMeasurementToMap(metricMaps[11],
							new SimpleRecordDto(timestamp, aggregatedDataDto.getAverageVehicleSpeed(), period));
					addMeasurementToMap(metricMaps[12],
							new SimpleRecordDto(timestamp, aggregatedDataDto.getHeadway(), period));
					addMeasurementToMap(metricMaps[13],
							new SimpleRecordDto(timestamp, aggregatedDataDto.getHeadwayVariance(), period));
					addMeasurementToMap(metricMaps[14],
							new SimpleRecordDto(timestamp, aggregatedDataDto.getGap(), period));
					addMeasurementToMap(metricMaps[15],
							new SimpleRecordDto(timestamp, aggregatedDataDto.getGapVariance(), period));
				}
			}

		}
	}

	/**
	 * This is a helper method where all the data types for traffic data is returned
	 *
	 * @return a list with all the data types for traffic data
	 */
	private static List<String> getTrafficDataTypes() {
		return Arrays.asList("total-transits", "number-of-motorcycles", "number-of-cars",
				"number-of-cars-and-minivans-with-trailer", "number-of-small-trucks-and-vans ",
				"number-of-medium-sized-trucks", "number-of-big-trucks", "number-of-articulated-trucks",
				"number-of-articulated-lorries", "number-of-busses",
				"number-of-unclassified-vehicles", "average-vehicle-speed", "headway", "headway-variance", "gap",
				"gap-variance");
	}

	/**
	 * This is a function where the bluetooth data of the stations is inserted to
	 * the bluetoothMetricMap
	 *
	 * @param passagesDataDtos   is an array where the bluetooth data is stored
	 * @param period             is an integer where the period in seconds is stored
	 * @param bluetoothMetricMap is a map where the bluetooth data is stored
	 * @throws ParseException is thrown if parsing causes an error
	 */
	public static void insertDataIntoBluetoothmap(PassagesDataDto[] passagesDataDtos, Integer period,
			DataMapDto<RecordDtoImpl> bluetoothMetricMap) throws ParseException {
		for (PassagesDataDto passagesDataDto : passagesDataDtos) {
			String value = passagesDataDto.getIdVehicle() != null ? passagesDataDto.getIdVehicle().toLowerCase() : null;
			Long timestamp = formatter.parse(passagesDataDto.getDate()).getTime();
			addMeasurementToMap(bluetoothMetricMap,
					new SimpleRecordDto(timestamp, value, period));
		}
	}

	/**
	 * This is a function where the measurement is added to a map
	 *
	 * @param map         where the measurement is stored
	 * @param measurement who gets stored in the map
	 */
	private static void addMeasurementToMap(DataMapDto<RecordDtoImpl> map, SimpleRecordDto measurement) {
		if (map != null) {
			measurement.setCreated_on(new Date().getTime());
			map.getData().add(measurement);
		}
	}

	private static Map<String, Object> createMetadata(JSONObject otherFields, int laneId) {
		Map<String, Object> metadata = new HashMap<>();

		metadata.put("municipality", JsonPath.read(otherFields, "$.GeoInfo.Comune"));
		metadata.put("region", JsonPath.read(otherFields, "$.GeoInfo.Regione"));

		if (laneId > -1)
			metadata.put("direction",
					JsonPath.read(otherFields, "$.CorsieInfo[?(@.Id == " + laneId + ")].Descrizione"));

		metadata.put("street_name", JsonPath.read(otherFields, "$.StradaInfo.Nome"));
		metadata.put("kilometric", JsonPath.read(otherFields, "$.StradaInfo.Chilometrica"));
		metadata.put("total_lanes", JsonPath.read(otherFields, "$.NumeroCorsie"));

		return metadata;
	}
}
