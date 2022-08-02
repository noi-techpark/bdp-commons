package it.bz.odh.trafficprovbz;

import com.jayway.jsonpath.JsonPath;
import it.bz.idm.bdp.dto.*;
import it.bz.odh.trafficprovbz.dto.AggregatedDataDto;
import it.bz.odh.trafficprovbz.dto.ClassificationSchemaDto;
import it.bz.odh.trafficprovbz.dto.MetadataDto;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.io.IOException;
import java.util.*;

@Service
public class SyncScheduler {

	private static final Logger LOG = LoggerFactory.getLogger(SyncScheduler.class);

	private static final String STATION_TYPE = "TrafficSensor";
	private static final String DATATYPE_ID = "TrafficSensor";

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
	 * Scheduled job measurements: Example on how to send measurements
	 */
	@Scheduled(cron = "${scheduler.job_measurements}")
	public void syncJobMeasurements() throws IOException {
		LOG.info("Cron job measurements started: Pushing measurements for {}", odhClient.getIntegreenTypology());

		DataMapDto<RecordDtoImpl> rootMap = new DataMapDto<>();

		AggregatedDataDto[] aggregatedDataDtos = famasClient.getAggregatedDataOnStations();

		for (AggregatedDataDto aggregatedDataDto : aggregatedDataDtos) {

			DataMapDto<RecordDtoImpl> stationMap = rootMap.upsertBranch(String.valueOf(aggregatedDataDto.getId()));
			DataMapDto<RecordDtoImpl> metricMap = stationMap.upsertBranch(DATATYPE_ID);
			Map<String, Object> aggregatedDataMap = new HashMap<>();
			aggregatedDataMap.put("total-transits", aggregatedDataDto.getTotalTransits());
			//TODO: LOGIC FOR BIKES AND SO ON
			JSONObject otherFields = new JSONObject(aggregatedDataDto.getOtherFields());
			if (otherFields.containsKey("TotaliPerClasseVeicolare")) {
				System.out.println("HERER");
			} else {
				System.out.println("NOT ALWAYS HERER");

			}
			aggregatedDataMap.put("average-vehicle-speed", aggregatedDataDto.getAverageVehicleSpeed());
			aggregatedDataMap.put("headway", aggregatedDataDto.getHeadway());
			aggregatedDataMap.put("headway-variance", aggregatedDataDto.getHeadwayVariance());
			aggregatedDataMap.put("gap", aggregatedDataDto.getGap());
			aggregatedDataMap.put("gap-variance", aggregatedDataDto.getGapVariance());


			SimpleRecordDto measurement = new SimpleRecordDto(22323L, aggregatedDataMap, period);
			List<RecordDtoImpl> values = metricMap.getData();
			values.add(measurement);
		}
		try {
			odhClient.pushData(rootMap);
			LOG.info("Cron job measurements successful");
		} catch (WebClientRequestException e) {
			LOG.error("Cron job measurements failed: Request exception: {}", e.getMessage());
		}
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
