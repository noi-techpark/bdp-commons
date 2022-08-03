package it.bz.odh.trafficprovbz;

import com.jayway.jsonpath.JsonPath;
import it.bz.idm.bdp.dto.*;
import it.bz.odh.trafficprovbz.dto.AggregatedDataDto;
import it.bz.odh.trafficprovbz.dto.ClassificationSchemaDto;
import it.bz.odh.trafficprovbz.dto.MetadataDto;
import it.bz.odh.trafficprovbz.dto.PassagesDataDto;
import net.bytebuddy.build.Plugin;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import javax.swing.text.DateFormatter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class SyncScheduler {

	private static final Logger LOG = LoggerFactory.getLogger(SyncScheduler.class);

	private static final String STATION_TYPE = "TrafficSensor";
	private static final String DATATYPE_ID = "TrafficSensor";
	private static final String DATATYPE_ID_BlUETOOTH = "vehicle detection";

	@Value("${odh_client.period}")
	private Integer period;

	@Lazy
	@Autowired
	private OdhClient odhClient;

	@Lazy
	@Autowired
	private FamasClient famasClient;

	/**
	 * Scheduled job stations: Sync stations and data types
	 */
	@Scheduled(cron = "${scheduler.job_stations}")
	public void syncJobStations() throws IOException {
		LOG.info("Cron job stations started: Sync Stations with type {} and data types", odhClient.getIntegreenTypology());

		List<DataTypeDto> odhDataTypeList = new ArrayList<>();

		ClassificationSchemaDto[] classificationDtos = famasClient.getClassificationSchemas();
		ArrayList<LinkedHashMap<String, String>> odhClassesList = new ArrayList<>();

		for (ClassificationSchemaDto c : classificationDtos) {
			ArrayList<LinkedHashMap<String, String>> classes = JsonPath.read(c.getOtherFields(), "$.Classi");
			odhClassesList.addAll(classes);
		}


		MetadataDto[] metadataDtos = famasClient.getStationsData();
		StationList odhStationList = new StationList();

		for (MetadataDto metadataDto : metadataDtos) {
			JSONObject otherFields = new JSONObject(metadataDto.getOtherFields());
			Double lat = JsonPath.read(otherFields, "$.GeoInfo.Latitudine");
			Double lon = JsonPath.read(otherFields, "$.GeoInfo.Longitudine");

			LinkedHashMap<String, String> classificationSchema = getClassificationSchema(odhClassesList, metadataDto);
			metadataDto.setOtherField("SchemaDiClassificazione", classificationSchema);

			ArrayList<LinkedHashMap<String, String>> lanes = JsonPath.read(otherFields, "$.CorsieInfo");
			LOG.info(String.valueOf(lanes.get(0)));

			for (LinkedHashMap<String, String> lane : lanes) {
				String description = JsonPath.read(lane, "$.Descrizione");
				String stationName = metadataDto.getName() + ":" + description;
				StationDto station = new StationDto(String.valueOf(metadataDto.getId()), stationName, lat, lon);
				station.setOrigin(odhClient.getProvenance().getLineage());
				station.setStationType(STATION_TYPE);
				station.setMetaData(metadataDto.getOtherFields());

				odhStationList.add(station);
			}
			LOG.info(odhStationList.toString());
		}

		try {
			odhClient.syncStations(odhStationList);
			odhClient.syncDataTypes(odhDataTypeList);
			LOG.info("Cron job stations successful");
		} catch (WebClientRequestException e) {
			LOG.error("Cron job stations failed: Request exception: {}", e.getMessage());
		}
	}

	/**
	 * Scheduled job traffic measurements: Example on how to send measurements
	 */
	@Scheduled(cron = "${scheduler.job_measurements}")
	public void syncJobTrafficMeasurements() throws IOException, ParseException {
		LOG.info("Cron job measurements started: Pushing measurements for {}", odhClient.getIntegreenTypology());

		DataMapDto<RecordDtoImpl> rootMap = new DataMapDto<>();

		AggregatedDataDto[] aggregatedDataDtos = famasClient.getAggregatedDataOnStations();

		for (AggregatedDataDto aggregatedDataDto : aggregatedDataDtos) {

			DataMapDto<RecordDtoImpl> stationMap = rootMap.upsertBranch(String.valueOf(aggregatedDataDto.getId()));
			DataMapDto<RecordDtoImpl> metricMap = stationMap.upsertBranch(DATATYPE_ID);
			Map<String, Object> aggregatedDataMap = new HashMap<>();
			System.out.println(new Date().getTime());
			aggregatedDataMap.put("created_on", new Date().getTime());
			aggregatedDataMap.put("total-transits", aggregatedDataDto.getTotalTransits());
			JSONObject otherFields = new JSONObject(aggregatedDataDto.getOtherFields());
			if (otherFields.containsKey("TotaliPerClasseVeicolare")) {
				LinkedHashMap<String, Integer> classes = JsonPath.read(otherFields, "$.TotaliPerClasseVeicolare");
				Set<String> keys = classes.keySet();
				for (String key : keys) {
					switch (key) {
						case "1":
							aggregatedDataMap.put("number-of-motorcycles", classes.get(key));
							break;
						case "2":
							aggregatedDataMap.put("number-of-cars", classes.get(key));
							break;
						case "3":
							aggregatedDataMap.put("number-of-cars-and-minivans-with-trailer", classes.get(key));
							break;
						case "4":
							aggregatedDataMap.put("number-of-small-trucks-and-vans", classes.get(key));
							break;
						case "5":
							aggregatedDataMap.put("number-of-medium-sized-trucks", classes.get(key));
							break;
						case "6":
							aggregatedDataMap.put("number-of-big-trucks", classes.get(key));
							break;
						case "7":
							aggregatedDataMap.put("number-of-articulated-trucks", classes.get(key));
							break;
						case "8":
							aggregatedDataMap.put("number-of-articulated-lorries", classes.get(key));
							break;
						case "9":
							aggregatedDataMap.put("number-of-busses", classes.get(key));
							break;
						case "10":
							aggregatedDataMap.put("number-of-unclassified-vehicles", classes.get(key));
							break;
					}
				}
				aggregatedDataMap.put("average-vehicle-speed", aggregatedDataDto.getAverageVehicleSpeed());
				aggregatedDataMap.put("headway", aggregatedDataDto.getHeadway());
				aggregatedDataMap.put("headway-variance", aggregatedDataDto.getHeadwayVariance());
				aggregatedDataMap.put("gap", aggregatedDataDto.getGap());
				aggregatedDataMap.put("gap-variance", aggregatedDataDto.getGapVariance());
			}

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			Long timestamp = formatter.parse(aggregatedDataDto.getDate()).getTime();
			SimpleRecordDto measurement = new SimpleRecordDto(timestamp, aggregatedDataMap, period);
			List<RecordDtoImpl> values = metricMap.getData();
			values.add(measurement);
		}
		try {
			odhClient.pushData(rootMap);
			LOG.info("Cron job traffic measurements successful");
		} catch (WebClientRequestException e) {
			LOG.error("Cron job traffic measurements failed: Request exception: {}", e.getMessage());
		}
	}

	/**
	 * Scheduled job bluetooth measurements: sync climate daily
	 */
	@Scheduled(cron = "${scheduler.job_measurements}")
	public void syncJobBluetoothMeasurements() throws IOException, ParseException {
		LOG.info("Cron job measurements started: Pushing bluetooth measurements for {}", odhClient.getIntegreenTypology());

		MetadataDto[] metadataDtos = famasClient.getStationsData();

		for (MetadataDto metadataDto : metadataDtos) {
			System.out.println("HERE");
			DataMapDto<RecordDtoImpl> rootMap = new DataMapDto<>();

			DataMapDto<RecordDtoImpl> stationMap = rootMap.upsertBranch(String.valueOf(metadataDto.getId()));

			DataMapDto<RecordDtoImpl> bluetoothMetricMap = stationMap.upsertBranch(DATATYPE_ID_BlUETOOTH);

			PassagesDataDto[] passagesDataDtos = famasClient.getPassagesDataOnStations(metadataDto.getId());

			for (PassagesDataDto passagesDataDto : passagesDataDtos) {
				System.out.println("HERE2");
				System.out.println(passagesDataDto.getId());
				System.out.println(passagesDataDto.getDate());
				System.out.println(passagesDataDto.getIdVehicle());
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
				Long timestamp = formatter.parse(passagesDataDto.getDate()).getTime();
				SimpleRecordDto measurement = new SimpleRecordDto(timestamp, passagesDataDto.getIdVehicle(), period);
				bluetoothMetricMap.getData().add(measurement);
			}

			try {
				// Push data for every station separately to avoid out of memory errors
				odhClient.pushData(rootMap);
				LOG.info("Push data for station {} bluetooth measurement successful", metadataDto.getName());
			} catch (WebClientRequestException e) {
				LOG.error("Push data for station {} bluetooth measurement failed: Request exception: {}", metadataDto.getName(),
					e.getMessage());
			}
		}

		LOG.info("Cron job for climate daily successful");
	}

	public LinkedHashMap<String, String> getClassificationSchema(ArrayList<LinkedHashMap<String, String>> odhClassesList, MetadataDto s) {
		for (LinkedHashMap<String, String> odhClass : odhClassesList) {
			int code = JsonPath.read(odhClass, "$.Codice");
			if (code == s.getClassificationSchema()) {
				return odhClass;
			}
		}
		return null;
	}

}
