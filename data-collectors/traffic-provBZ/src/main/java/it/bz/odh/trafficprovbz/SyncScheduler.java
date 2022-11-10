package it.bz.odh.trafficprovbz;

import com.jayway.jsonpath.JsonPath;
import it.bz.idm.bdp.dto.*;
import it.bz.odh.trafficprovbz.dto.AggregatedDataDto;
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

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import javax.annotation.PostConstruct;

@Service
public class SyncScheduler {

	private static final Logger LOG = LoggerFactory.getLogger(SyncScheduler.class);
	private static final String STATION_TYPE_TRAFFIC_SENSOR = "TrafficSensor";
	private static final String STATION_TYPE_BLUETOOTH_STATION = "BluetoothStation";
	private static final String DATATYPE_ID_HEADWAY_VARIANCE = "headway-variance";
	private static final String DATATYPE_ID_GAP_VARIANCE = "gap-variance";
	@Value("${odh_client.period}")
	private Integer period;
	private final OdhClientTrafficSensor odhClientTrafficSensor;
	private final OdhClientBluetoothStation odhClientBluetoothStation;
	private final FamasClient famasClient;
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	private Map<String, Date> startPeriodTrafficList;
	private Map<String, Date> endPeriodTrafficList;
	private Map<String, Date> startPeriodBluetoothList;
	private Map<String, Date> endPeriodBluetoothList;

	// Frame of data requested from Famas API
	// api gives actually error if period is bigger than 12 hours
	private int timeframe = 39600 * 1000;

	@Value("${historyimport.enabled}")
	private Boolean historyEnabled;

	@Value("#{new java.text.SimpleDateFormat('${historyimport.dateformat}').parse('${historyimport.startdate}')}")
	private Date historyStartDate;

	public SyncScheduler(@Lazy OdhClientTrafficSensor odhClientTrafficSensor,
			@Lazy OdhClientBluetoothStation odhClientBluetoothStation, @Lazy FamasClient famasClient)
			throws IOException, ParseException {
		this.odhClientTrafficSensor = odhClientTrafficSensor;
		this.odhClientBluetoothStation = odhClientBluetoothStation;
		this.famasClient = famasClient;
		initDataTypes();
	}

	/**
	 * To import historical data
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	@PostConstruct
	private void historyImport() throws IOException, ParseException {
		if (historyEnabled) {
			LOG.info("Start historical import from {}...", historyStartDate.toString());
			MetadataDto[] metadataDtos = famasClient.getStationsData();

			LOG.info("Syncing stations...");
			syncTrafficStations(metadataDtos);
			syncBluetoothStations(metadataDtos);
			LOG.info("Syncing stations done");

			Instant now = Instant.now();
			Instant currentStartDate = historyStartDate.toInstant();
			Instant currentEndDate = historyStartDate.toInstant().plus(timeframe, ChronoUnit.MILLIS);

			int timeFrameCounter = 0;

			while (currentEndDate.isBefore(now)) {
				// bluetooth
				for (MetadataDto station : metadataDtos) {
					String stationId = station.getId();

					DataMapDto<RecordDtoImpl> rootMap = new DataMapDto<>();
					DataMapDto<RecordDtoImpl> stationMap = rootMap.upsertBranch(station.getId());
					DataMapDto<RecordDtoImpl> bluetoothMetricMap = stationMap.upsertBranch("vehicle detection");
					PassagesDataDto[] passagesDataDtos = famasClient.getPassagesDataOnStations(stationId,
							sdf.format(currentStartDate),
							sdf.format(currentEndDate));
					Parser.insertDataIntoBluetoothmap(passagesDataDtos, period, bluetoothMetricMap);
					// Push data for every station separately to avoid out of memory errors
					odhClientBluetoothStation.pushData(rootMap);
				}

				// traffic
				for (MetadataDto station : metadataDtos) {
					String requestStationId = station.getId();
					for (String key : station.getLanes().keySet()) {
						DataMapDto<RecordDtoImpl> rootMap = new DataMapDto<>();
						// use id that has been written to odh by station sync
						DataMapDto<RecordDtoImpl> stationMap = rootMap.upsertBranch(key);
						AggregatedDataDto[] aggregatedDataDtos = famasClient.getAggregatedDataOnStations(
								requestStationId,
								sdf.format(currentStartDate),
								sdf.format(currentEndDate));
						Parser.insertDataIntoStationMap(aggregatedDataDtos, period, stationMap,
								station.getLanes().get(key));
						odhClientTrafficSensor.pushData(rootMap);
					}
				}

				// increase by time frame
				currentStartDate.plus(timeframe, ChronoUnit.MILLIS);
				currentEndDate.plus(timeframe, ChronoUnit.MILLIS);
				timeFrameCounter++;
			}
			LOG.info("Historical done. Imported {} times 11 hours of data.", timeFrameCounter);
		} else
			LOG.info("Historical import not enabled, skipping it...");

	}

	@Scheduled(cron = "${scheduler.sync}")
	public void sync() throws IOException, ParseException {
		MetadataDto[] metadataDtos = famasClient.getStationsData();

		// ClassificationSchemaDto[] classificationDtos =
		// famasClient.getClassificationSchemas();
		// ArrayList<LinkedHashMap<String, String>> classificationSchemaList = new
		// ArrayList<>();
		// for (ClassificationSchemaDto c : classificationDtos) {
		// ArrayList<LinkedHashMap<String, String>> classes =
		// JsonPath.read(c.getOtherFields(), "$.Classi");
		// classificationSchemaList.addAll(classes);
		// }

		syncTrafficStations(metadataDtos);
		syncBluetoothStations(metadataDtos);

		syncJobTrafficMeasurements(metadataDtos);
		syncJobBluetoothMeasurements(metadataDtos);
	}

	/**
	 * Scheduled job stations: Sync stations and data types
	 *
	 * @throws IOException
	 */
	public void syncTrafficStations(MetadataDto[] metadataDtos) throws IOException {
		LOG.info("Cron job stations started: Sync Stations with type {} and data types",
				odhClientTrafficSensor.getIntegreenTypology());
		LOG.info("Syncing traffic stations");

		StationList odhTrafficStationList = new StationList();

		// Insert traffic sensors in station list to insert them in ODH
		for (MetadataDto metadataDto : metadataDtos) {
			JSONObject otherFields = new JSONObject(metadataDto.getOtherFields());
			ArrayList<LinkedHashMap<String, String>> lanes = JsonPath.read(otherFields, "$.CorsieInfo");
			for (LinkedHashMap<String, String> lane : lanes) {
				StationDto station = Parser.createStation(metadataDto, otherFields, lane,
						STATION_TYPE_TRAFFIC_SENSOR);
				station.setOrigin(odhClientTrafficSensor.getProvenance().getLineage());
				odhTrafficStationList.add(station);
			}
		}
		odhClientTrafficSensor.syncStations(odhTrafficStationList);
		LOG.info("Cron job traffic stations successful");
	}

	public void syncBluetoothStations(MetadataDto[] metadataDtos) {
		LOG.info("Syncing bluetooth stations");
		StationList odhBluetoothStationList = new StationList();

		// Insert bluetooth sensors in station list to insert them in ODH
		for (MetadataDto metadataDto : metadataDtos) {
			JSONObject otherFields = new JSONObject(metadataDto.getOtherFields());
			StationDto station = Parser.createStation(metadataDto, otherFields, null,
					STATION_TYPE_BLUETOOTH_STATION);
			station.setOrigin(odhClientBluetoothStation.getProvenance().getLineage());
			// station.setMetaData(metadataDto.getOtherFields());
			odhBluetoothStationList.add(station);
		}
		odhClientBluetoothStation.syncStations(odhBluetoothStationList);
		LOG.info("Cron job bluetooth stations successful");
	}

	/**
	 * Scheduled job traffic measurements: Example on how to send measurements
	 *
	 * @throws IOException
	 * @throws ParseException
	 */
	public void syncJobTrafficMeasurements(MetadataDto[] stationDtos) throws IOException, ParseException {
		LOG.info("Cron job measurements started: Pushing measurements for {}",
				odhClientTrafficSensor.getIntegreenTypology());
		for (MetadataDto station : stationDtos) {
			String stationId = station.getId();
			String requestStationId = station.getId();
			endPeriodTrafficList = updateEndPeriod(stationId, endPeriodTrafficList);
			startPeriodTrafficList = updateStartPeriod(stationId, startPeriodTrafficList,
					endPeriodTrafficList.get(stationId));
			LOG.info("After Initialisation for {}", station.getId());

			for (String key : station.getLanes().keySet()) {
				DataMapDto<RecordDtoImpl> rootMap = new DataMapDto<>();
				// use id that has been written to odh by station sync
				DataMapDto<RecordDtoImpl> stationMap = rootMap.upsertBranch(key);
				AggregatedDataDto[] aggregatedDataDtos = famasClient.getAggregatedDataOnStations(requestStationId,
						sdf.format(startPeriodTrafficList.get(stationId)),
						sdf.format(endPeriodTrafficList.get(stationId)));
				Parser.insertDataIntoStationMap(aggregatedDataDtos, period, stationMap,
						station.getLanes().get(key));
				try {
					odhClientTrafficSensor.pushData(rootMap);
				} catch (Exception e) {
					LOG.info("Cron job traffic for station {} failed because of {}", station.getId(),
							e.getMessage());
				}
			}

			// If everything was successful we set the start of the next period equal to the
			// end of the period queried right now
			startPeriodTrafficList.put(stationId, endPeriodTrafficList.get(stationId));
			LOG.info("After inserting to DB for {}", station.getId());
			LOG.info("Cron job traffic for station {} successful", station.getId());
		}
	}

	/**
	 * Scheduled job bluetooth measurements: sync climate daily
	 *
	 * @throws IOException
	 * @throws ParseException
	 */
	public void syncJobBluetoothMeasurements(MetadataDto[] stationDtos) throws IOException, ParseException {
		LOG.info("Cron job measurements started: Pushing bluetooth measurements for {}",
				odhClientBluetoothStation.getIntegreenTypology());

		for (MetadataDto station : stationDtos) {
			String stationId = station.getId();
			endPeriodBluetoothList = updateEndPeriod(stationId, endPeriodBluetoothList);
			startPeriodBluetoothList = updateStartPeriod(stationId, startPeriodBluetoothList,
					endPeriodBluetoothList.get(stationId));
			DataMapDto<RecordDtoImpl> rootMap = new DataMapDto<>();
			DataMapDto<RecordDtoImpl> stationMap = rootMap.upsertBranch(station.getId());
			DataMapDto<RecordDtoImpl> bluetoothMetricMap = stationMap.upsertBranch("vehicle detection");
			PassagesDataDto[] passagesDataDtos = famasClient.getPassagesDataOnStations(stationId,
					sdf.format(startPeriodBluetoothList.get(stationId)),
					sdf.format(endPeriodBluetoothList.get(stationId)));

			Parser.insertDataIntoBluetoothmap(passagesDataDtos, period, bluetoothMetricMap);
			try {
				// Push data for every station separately to avoid out of memory errors
				odhClientBluetoothStation.pushData(rootMap);
			} catch (WebClientRequestException e) {
				LOG.error("Push data for station {} bluetooth measurement failed: Request exception: {}",
						station.getId(),
						e.getMessage());
			}
			// If everything was successful we set the start of the next period equal to the
			// end of the period queried right now
			startPeriodBluetoothList.put(stationId, endPeriodBluetoothList.get(stationId));
			LOG.info("Push data for station {} bluetooth measurement successful", station.getId());
		}
		LOG.info("Cron job for bluetooth measurements successful");
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
		// of start and end period is bigger than seven days (otherwise 400 error from
		// api)
		if (!startPeriodList.containsKey(id)
				|| startPeriodList.get(id).getTime() - endPeriod.getTime() > timeframe) {
			startPeriodList.put(id, new Date(endPeriod.getTime() - timeframe));
		}
		return startPeriodList;
	}

	/**
	 * Helper method to initialize and analyze the start period
	 *
	 * @param id            string containing the station di
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
		odhDataTypeList
				.add(new DataTypeDto(DATATYPE_ID_HEADWAY_VARIANCE, null, DATATYPE_ID_HEADWAY_VARIANCE, "Average"));
		odhDataTypeList.add(new DataTypeDto(DATATYPE_ID_GAP_VARIANCE, null, DATATYPE_ID_GAP_VARIANCE, "Average"));

		try {
			odhClientBluetoothStation.syncDataTypes(odhDataTypeList);
			odhClientTrafficSensor.syncDataTypes(odhDataTypeList);
		} catch (WebClientRequestException e) {
			LOG.error("Sync data types failed: Request exception: {}", e.getMessage());
		}
	}

	/**
	 * This helper function finds the classification schema for a given station
	 *
	 * @param classificationSchemaList is a list containing all the classification
	 *                                 schemas
	 * @param metadataDto              is a variable where the station is stored
	 * @return the appropriate classification schema for the station
	 */
	private LinkedHashMap<String, String> getClassificationSchema(
			ArrayList<LinkedHashMap<String, String>> classificationSchemaList, MetadataDto metadataDto) {
		for (LinkedHashMap<String, String> classificationSchema : classificationSchemaList) {
			int code = JsonPath.read(classificationSchema, "$.Codice");
			if (code == metadataDto.getClassificationSchema()) {
				return classificationSchema;
			}
		}
		return null;
	}

}
