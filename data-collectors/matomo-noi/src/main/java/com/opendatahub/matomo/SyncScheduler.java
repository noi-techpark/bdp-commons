package com.opendatahub.matomo;

import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties.Stream;
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

	private record Datatype(String odhType, String odhDescription, String matomoPeriod) {
	};

	private final Set<Datatype> types = new HashSet<>();

	@Value("${odh_client.period}")
	private Integer period;

	@Lazy
	@Autowired
	private OdhClient odhClient;

	@Autowired
	private MatomoClient matomoClient;

	@PostConstruct
	public void syncDataTypes() {
		types.add(new Datatype("dailyVisity", "Daily visits on a website", "day"));
		types.add(new Datatype("weeklyVisity", "Weekly visits on a website", "week"));
		types.add(new Datatype("monthlyVisity", "Monthly visits on a website", "month"));
		types.add(new Datatype("yearlyVisity", "Yearly visits on a website", "year"));

		List<DataTypeDto> odhDataTypeList = types.stream()
				.map(e -> new DataTypeDto(e.odhType, "amount", e.odhDescription, null))
				.toList();

		odhClient.syncDataTypes(odhDataTypeList);
	}

	@Scheduled(cron = "${scheduler.data}")
	public void syncData() {
		LOG.info("Cron job started: Sync Stations and pushing data with type {} and data types",
				odhClient.getIntegreenTypology());

		StationList odhStationList = new StationList();
		DataMapDto<RecordDtoImpl> dataMap = new DataMapDto<>();

		for (Datatype type : types) {
			CustomReportDto[] reportData = matomoClient.getReportData(type.matomoPeriod);

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
				DataMapDto<RecordDtoImpl> metricMap = stationMap.upsertBranch(type.odhType());
				SimpleRecordDto measurement = new SimpleRecordDto(Instant.now().toEpochMilli(), report.getVisits(),
						period);

				LOG.info("{} visits {}", type.odhType, report.getVisits());

				List<RecordDtoImpl> values = metricMap.getData();
				values.add(measurement);
			}
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
