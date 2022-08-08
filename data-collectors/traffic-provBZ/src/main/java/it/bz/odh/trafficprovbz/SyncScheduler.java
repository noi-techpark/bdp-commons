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

	private Date startPeriodTraffic;

	private Date startPeriodBluetooth;

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
			ArrayList<LinkedHashMap<String, String>> odhClassesList = new ArrayList<>();
			for (ClassificationSchemaDto c : classificationDtos) {
				ArrayList<LinkedHashMap<String, String>> classes = JsonPath.read(c.getOtherFields(), "$.Classi");
				odhClassesList.addAll(classes);
			}
			MetadataDto[] metadataDtos = famasClient.getStationsData();
			StationList odhTrafficStationList = new StationList();

			// Insert traffic sensors in station list to insert them in ODH
			for (MetadataDto metadataDto : metadataDtos) {
				JSONObject otherFields = new JSONObject(metadataDto.getOtherFields());
				ArrayList<LinkedHashMap<String, String>> lanes = JsonPath.read(otherFields, "$.CorsieInfo");
				LinkedHashMap<String, String> classificationSchema = getClassificationSchema(odhClassesList, metadataDto);
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
				LinkedHashMap<String, String> classificationSchema = getClassificationSchema(odhClassesList, metadataDto);
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
			if (startPeriodTraffic == null) {
				startPeriodTraffic = new Date(new Date().getTime() - period * 1000);
			}
			MetadataDto[] metadataDtos = famasClient.getStationsData();
			for (MetadataDto metadataDto : metadataDtos) {
				DataMapDto<RecordDtoImpl> rootMap = new DataMapDto<>();
				DataMapDto<RecordDtoImpl> stationMap = rootMap.upsertBranch(metadataDto.getId());
				AggregatedDataDto[] aggregatedDataDtos = famasClient.getAggregatedDataOnStations(metadataDto.getId(), sdf.format(startPeriodTraffic), sdf.format(new Date()));
				Parser.insertDataIntoStationMap(aggregatedDataDtos, period, stationMap);
				odhClient.pushData(rootMap);
				LOG.info("Cron job traffic for station {} successful", metadataDto.getId());
			}
			startPeriodTraffic = new Date();
		} catch (Exception e) {
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
			if (startPeriodBluetooth == null) {
				startPeriodBluetooth = new Date(new Date().getTime() - period * 1000);
			}
			MetadataDto[] metadataDtos = famasClient.getStationsData();

			for (MetadataDto metadataDto : metadataDtos) {
				DataMapDto<RecordDtoImpl> rootMap = new DataMapDto<>();
				DataMapDto<RecordDtoImpl> stationMap = rootMap.upsertBranch(metadataDto.getId());
				DataMapDto<RecordDtoImpl> bluetoothMetricMap = stationMap.upsertBranch("vehicle detection");
				PassagesDataDto[] passagesDataDtos = famasClient.getPassagesDataOnStations(metadataDto.getId(), sdf.format(startPeriodBluetooth), sdf.format(new Date()));
				Parser.insertDataIntoBluetoothmap(passagesDataDtos, period, bluetoothMetricMap);
				try {
					// Push data for every station separately to avoid out of memory errors
					odhClient.pushData(rootMap);
					LOG.info("Push data for station {} bluetooth measurement successful", metadataDto.getName());
				} catch (WebClientRequestException e) {
					LOG.error("Push data for station {} bluetooth measurement failed: Request exception: {}", metadataDto.getName(),
						e.getMessage());
				}
			}
			LOG.info("Cron job for bluetooth measurements successful");
			startPeriodBluetooth = new Date();
		} catch (Exception e) {
			LOG.error("Push data for bluetooth measurements failed: Request exception: {}", e.getMessage());
		}
	}

	private void initDataTypes() {
		LOG.info("Syncing data types");
		List<DataTypeDto> odhDataTypeList = new ArrayList<>();

		odhDataTypeList.add(new DataTypeDto(DATATYPE_ID_HEADWAY_VARIANCE, "double", DATATYPE_ID_HEADWAY_VARIANCE, "Average"));
		odhDataTypeList.add(new DataTypeDto(DATATYPE_ID_GAP_VARIANCE, "double", DATATYPE_ID_GAP_VARIANCE, "Average"));

		try {
			odhClient.syncDataTypes(odhDataTypeList);
		} catch (WebClientRequestException e) {
			LOG.error("Sync data types failed: Request exception: {}", e.getMessage());
		}
	}

	private LinkedHashMap<String, String> getClassificationSchema(ArrayList<LinkedHashMap<String, String>> odhClassesList, MetadataDto s) {
		for (LinkedHashMap<String, String> odhClass : odhClassesList) {
			int code = JsonPath.read(odhClass, "$.Codice");
			if (code == s.getClassificationSchema()) {
				return odhClass;
			}
		}
		return null;
	}

}
