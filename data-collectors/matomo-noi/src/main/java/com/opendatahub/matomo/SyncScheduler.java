package com.opendatahub.matomo;

import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import com.opendatahub.matomo.api.CustomReportDto;
import com.opendatahub.matomo.api.MatomoClient;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;

@Service
public class SyncScheduler {

	private static final Logger LOG = LoggerFactory.getLogger(SyncScheduler.class);

	private static final String DATATYPE_NAME = "dailyVisits";

	@Value("${odh_client.period}")
	private Integer period;

	@Lazy
	@Autowired
	private OdhClient odhClient;

	@Autowired
	private MatomoClient matomoClient;

	@PostConstruct
	public void syncDataTypes() {

		List<DataTypeDto> odhDataTypeList = new ArrayList<>();
		odhDataTypeList.add(
				new DataTypeDto(
						DATATYPE_NAME,
						"amount",
						"Daily visits on a website",
						null // explaining the metric type (min, max, mean, etc.)
				));

		odhClient.syncDataTypes(odhDataTypeList);
	}

	@Scheduled(cron = "${scheduler.data}")
	public void syncData() {
		LOG.info("Cron job started: Sync Stations and pushing data with type {} and data types",
				odhClient.getIntegreenTypology());

		StationList odhStationList = new StationList();
		DataMapDto<RecordDtoImpl> dataMap = new DataMapDto<>();

		CustomReportDto[] reportData = matomoClient.getReportData();

		for (CustomReportDto report : reportData) {

			String stationId = report.getActionsPageUrl();

			// Stations
			StationDto station = new StationDto(
					stationId,
					report.getLabel(),
					0d,
					0d);
			station.setOrigin(odhClient.getProvenance().getLineage());
			odhStationList.add(station);

			// Data
			DataMapDto<RecordDtoImpl> stationMap = dataMap.upsertBranch(stationId);
			DataMapDto<RecordDtoImpl> metricMap = stationMap.upsertBranch(DATATYPE_NAME);
			SimpleRecordDto measurement = new SimpleRecordDto(Instant.now().toEpochMilli(), report.getVisits(),
					period);

			LOG.info("visits {}", report.getVisits());

			List<RecordDtoImpl> values = metricMap.getData();
			values.add(measurement);
		}

		// Sync stations
		try {
			odhClient.syncStations(odhStationList);
			LOG.info("Syncing stations successful");
		} catch (WebClientRequestException e) {
			LOG.error("Syncing stations failed: Request exception: {}", e.getMessage());
		}

		// Push data
		try {
			odhClient.pushData(dataMap);
			LOG.info("Pushing data successful");
		} catch (WebClientRequestException e) {
			LOG.error("Pushing data failed: Request exception: {}", e.getMessage());
		}

		LOG.info("Cron job done");
	}

}
