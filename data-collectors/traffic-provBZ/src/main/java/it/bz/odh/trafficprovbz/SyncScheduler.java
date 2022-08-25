package it.bz.odh.trafficprovbz;

import com.jayway.jsonpath.JsonPath;
import it.bz.idm.bdp.dto.*;
import it.bz.odh.trafficprovbz.dto.AggregatedDataDto;
import it.bz.odh.trafficprovbz.dto.ClassificationSchemaDto;
import it.bz.odh.trafficprovbz.dto.MetadataDto;
import it.bz.odh.trafficprovbz.dto.PassagesDataDto;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class SyncScheduler {

	private static final Logger LOG = LoggerFactory.getLogger(SyncScheduler.class);
	private static final String STATION_TYPE_TRAFFIC_SENSOR = "TrafficSensor";
	private static final String STATION_TYPE_BLUETOOTH_SENSOR = "BluetoothSensor";
	private static final String DATATYPE_ID_HEADWAY_VARIANCE = "headway-variance";
	private static final String DATATYPE_ID_GAP_VARIANCE = "gap-variance";
	@Value("${odh_client.period}")
	private Integer period;
	private final OdhClient odhClient;
	private final FamasClient famasClient;
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	private Map<String, Date> startPeriodTrafficList;
	private Map<String, Date> endPeriodTrafficList;
	private Map<String, Date> startPeriodBluetoothList;
	private Map<String, Date> endPeriodBluetoothList;

	public SyncScheduler(@Lazy OdhClient odhClient, @Lazy FamasClient famasClient) {
		this.odhClient = odhClient;
		this.famasClient = famasClient;
	}

	/**
	 * Scheduled job stations: Sync stations and data types
	 */
	@Scheduled(cron = "${scheduler.job_stations}")
	public void syncJobStations() {
		LOG.info("Cron job stations started: Sync Stations with type {} and data types", odhClient.getIntegreenTypology());
		try {
			initDataTypes();

			LOG.info("Syncing traffic stations");
			ClassificationSchemaDto[] classificationDtos;
			classificationDtos = famasClient.getClassificationSchemas();
			ArrayList<LinkedHashMap<String, String>> classificationSchemaList = new ArrayList<>();
			for (ClassificationSchemaDto c : classificationDtos) {
				ArrayList<LinkedHashMap<String, String>> classes = JsonPath.read(c.getOtherFields(), "$.Classi");
				classificationSchemaList.addAll(classes);
			}
			MetadataDto[] metadataDtos = famasClient.getStationsData();
			StationList odhTrafficStationList = new StationList();

			// Insert traffic sensors in station list to insert them in ODH
			for (MetadataDto metadataDto : metadataDtos) {
				JSONObject otherFields = new JSONObject(metadataDto.getOtherFields());
				ArrayList<LinkedHashMap<String, String>> lanes = JsonPath.read(otherFields, "$.CorsieInfo");
				LinkedHashMap<String, String> classificationSchema = getClassificationSchema(classificationSchemaList, metadataDto);
				for (LinkedHashMap<String, String> lane : lanes) {
					StationDto station = Parser.createStation(metadataDto, otherFields, lane, classificationSchema, STATION_TYPE_TRAFFIC_SENSOR);
					station.setOrigin(odhClient.getProvenance().getLineage());
					station.setMetaData(metadataDto.getOtherFields());
					odhTrafficStationList.add(station);
				}
			}
			odhClient.syncStations(odhTrafficStationList);
			LOG.info("Cron job traffic stations successful");

			LOG.info("Syncing bluetooth stations");
			StationList odhBluetoothStationList = new StationList();

			// Insert bluetooth sensors in station list to insert them in ODH
			for (MetadataDto metadataDto : metadataDtos) {
				JSONObject otherFields = new JSONObject(metadataDto.getOtherFields());
				LinkedHashMap<String, String> classificationSchema = getClassificationSchema(classificationSchemaList, metadataDto);
				StationDto station = Parser.createStation(metadataDto, otherFields, null, classificationSchema, STATION_TYPE_BLUETOOTH_SENSOR);
				station.setOrigin(odhClient.getProvenance().getLineage());
				station.setMetaData(metadataDto.getOtherFields());
				odhBluetoothStationList.add(station);
			}
			odhClient.syncStations(odhBluetoothStationList);
			LOG.info("Cron job bluetooth stations successful");
		} catch (Exception e) {
			LOG.error("Cron job stations failed: Request exception: {}", e.getMessage());
		}
	}

	/**
	 * Scheduled job traffic measurements: Example on how to send measurements
	 */
	@Scheduled(cron = "${scheduler.job_measurements}")
	public void syncJobTrafficMeasurements() {
		LOG.info("Cron job measurements started: Pushing measurements for {}", odhClient.getIntegreenTypology());
		try {
			MetadataDto[] metadataDtos = famasClient.getStationsData();
			for (MetadataDto metadataDto : metadataDtos) {
				String stationId = metadataDto.getId();
				endPeriodTrafficList = updateEndPeriod(stationId, endPeriodTrafficList);
				startPeriodTrafficList = updateStartPeriod(stationId, startPeriodTrafficList, endPeriodTrafficList.get(stationId));
				LOG.info("After Initialisation for {}", stationId);
				DataMapDto<RecordDtoImpl> rootMap = new DataMapDto<>();
				DataMapDto<RecordDtoImpl> stationMap = rootMap.upsertBranch(stationId);
				AggregatedDataDto[] aggregatedDataDtos = famasClient.getAggregatedDataOnStations(stationId, sdf.format(startPeriodTrafficList.get(stationId)), sdf.format(endPeriodTrafficList.get(stationId)));
				Parser.insertDataIntoStationMap(aggregatedDataDtos, period, stationMap);
				try {
					odhClient.pushData(rootMap);
				} catch (Exception e) {
					LOG.info("Cron job traffic for station {} failed because of {}", metadataDto.getId(), e.getMessage());
				}
				// If everything was successful we set the start of the next period equal to the end of the period queried right now
				startPeriodTrafficList.put(stationId, endPeriodTrafficList.get(stationId));
				LOG.info("After inserting to DB for {}", stationId);
				LOG.info("Cron job traffic for station {} successful", metadataDto.getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Cron job traffic measurements failed: Request exception: {}", e.getMessage());
		}
	}

	/**
	 * Scheduled job bluetooth measurements: sync climate daily
	 */
	@Scheduled(cron = "${scheduler.job_measurements}")
	public void syncJobBluetoothMeasurements() {
		LOG.info("Cron job measurements started: Pushing bluetooth measurements for {}", odhClient.getIntegreenTypology());
		try {
			MetadataDto[] metadataDtos = famasClient.getStationsData();

			for (MetadataDto metadataDto : metadataDtos) {
				String stationId = metadataDto.getId();
				endPeriodBluetoothList = updateEndPeriod(metadataDto.getId(), endPeriodBluetoothList);
				startPeriodBluetoothList = updateStartPeriod(metadataDto.getId(), startPeriodBluetoothList, endPeriodBluetoothList.get(stationId));
				DataMapDto<RecordDtoImpl> rootMap = new DataMapDto<>();
				DataMapDto<RecordDtoImpl> stationMap = rootMap.upsertBranch(metadataDto.getId());
				DataMapDto<RecordDtoImpl> bluetoothMetricMap = stationMap.upsertBranch("vehicle detection");
				PassagesDataDto[] passagesDataDtos = famasClient.getPassagesDataOnStations(metadataDto.getId(), sdf.format(startPeriodBluetoothList.get(stationId)), sdf.format(endPeriodBluetoothList.get(stationId)));
				Parser.insertDataIntoBluetoothmap(passagesDataDtos, period, bluetoothMetricMap);
				try {
					// Push data for every station separately to avoid out of memory errors
					odhClient.pushData(rootMap);
				} catch (WebClientRequestException e) {
					LOG.error("Push data for station {} bluetooth measurement failed: Request exception: {}", metadataDto.getName(),
						e.getMessage());
				}
				// If everything was successful we set the start of the next period equal to the end of the period queried right now
				startPeriodBluetoothList.put(stationId, endPeriodBluetoothList.get(stationId));
				LOG.info("Push data for station {} bluetooth measurement successful", metadataDto.getName());
			}
			LOG.info("Cron job for bluetooth measurements successful");
		} catch (Exception e) {
			LOG.error("Push data for bluetooth measurements failed: Request exception: {}", e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Helper method to initialize and analyze the start period
	 *
	 * @param id              string containing the station di
	 * @param startPeriodList list of dates containing the start of the period
	 * @param endPeriod       date containing the end of the period
	 * @return hash map with (eventually updated) list with start dates
	 */
	private Map<String, Date> updateStartPeriod(String id, Map<String, Date> startPeriodList, Date endPeriod) {
		if (startPeriodList == null) {
			startPeriodList = new HashMap<>();
		}
		// Set date of start period to now minus seven days if not existing or if range
		// of start and end period is bigger than seven days (otherwise 400 error from api)

		// api gives actually error if period is bigger than 12 hours (previus value 604800)
		if (!startPeriodList.containsKey(id) || startPeriodList.get(id).getTime() - endPeriod.getTime() > 39600 * 1000) {
			startPeriodList.put(id, new Date(endPeriod.getTime() - 39600 * 1000));
		}
		return startPeriodList;
	}

	/**
	 * Helper method to initialize and analyze the start period
	 *
	 * @param id              string containing the station di
	 * @param endPeriodList list of dates containing the end of the period
	 * @return hash map with (eventually updated) list with end dates
	 */
	private Map<String, Date> updateEndPeriod(String id, Map<String, Date> endPeriodList) {
		if (endPeriodList == null) {
			endPeriodList = new HashMap<>();
		}
		// Set date of end period always to now
		endPeriodList.put(id, new Date());
		return endPeriodList;
	}

	/**
	 * Helper method to initialize the data types
	 */
	private void initDataTypes() {
		LOG.info("Syncing data types");
		List<DataTypeDto> odhDataTypeList = new ArrayList<>();

		// TODO: What to insert for unit and rtype?
		odhDataTypeList.add(new DataTypeDto(DATATYPE_ID_HEADWAY_VARIANCE, "double", DATATYPE_ID_HEADWAY_VARIANCE, "Average"));
		odhDataTypeList.add(new DataTypeDto(DATATYPE_ID_GAP_VARIANCE, "double", DATATYPE_ID_GAP_VARIANCE, "Average"));

		try {
			odhClient.syncDataTypes(odhDataTypeList);
		} catch (WebClientRequestException e) {
			LOG.error("Sync data types failed: Request exception: {}", e.getMessage());
		}
	}

	/**
	 * This helper function finds the classification schema for a given station
	 *
	 * @param classificationSchemaList is a list containing all the classification schemas
	 * @param metadataDto              is a variable where the station is stored
	 * @return the appropriate classification schema for the station
	 */
	private LinkedHashMap<String, String> getClassificationSchema(ArrayList<LinkedHashMap<String, String>> classificationSchemaList, MetadataDto metadataDto) {
		for (LinkedHashMap<String, String> classificationSchema : classificationSchemaList) {
			int code = JsonPath.read(classificationSchema, "$.Codice");
			if (code == metadataDto.getClassificationSchema()) {
				return classificationSchema;
			}
		}
		return null;
	}

}
