// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.odh.carpooling;

import it.bz.idm.bdp.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SyncScheduler {

	private static final Logger LOG = LoggerFactory.getLogger(SyncScheduler.class);
	private static final String DATATYPE_ITINERARY_ID = "itinerary_details";

	@Value("${odh_client.period}")
	private Integer period;

	@Value("${odh_client.stationNamePrefix}")
	private String stationNamePrefix;

	@Lazy
	@Autowired
	private OdhClient odhClient;

	@Autowired
	private GoogleDriveConnector googleDriveConnector;

	@Scheduled(cron = "${scheduler.job_car_pooling}")
	public void carPoolingMain() {
		LOG.info("Cron car pooling man started");
		initDataTypes();

		long currentTimeInMillis = System.currentTimeMillis();

		List<CarPoolingTripDto> carPoolingTripList = CsvFileUtilities
				.parseCarPoolingCsvData(googleDriveConnector.readRidesCsvContent());

		StationList stationList = new StationList();
		DataMapDto<RecordDtoImpl> dataMap = new DataMapDto<>();

		LOG.info("got {} car pooling trips", carPoolingTripList.size());

		for (int i = 0; i < carPoolingTripList.size(); i++) {
			CarPoolingTripDto carPoolingTrip = carPoolingTripList.get(i);
			StationDto stationDto = new StationDto(carPoolingTrip.getHashedId(),
					stationNamePrefix + i,
					carPoolingTrip.getStartLatApprox(),
					carPoolingTrip.getStartLonApprox());

			stationDto.setOrigin(odhClient.getProvenance().getLineage());

			stationDto.getMetaData().put("start_lat_approx", carPoolingTrip.getStartLatApprox());
			stationDto.getMetaData().put("start_lon_approx", carPoolingTrip.getStartLonApprox());
			stationDto.getMetaData().put("end_lat_approx", carPoolingTrip.getEndLatApprox());
			stationDto.getMetaData().put("end_lon_approx", carPoolingTrip.getEndLonApprox());

			stationList.add(stationDto);

			dataMap.addRecord(stationDto.getId(), DATATYPE_ITINERARY_ID,
					new SimpleRecordDto(currentTimeInMillis, carPoolingTrip.toJson(), period));
		}

		LOG.info("sync {} car pooling stations", stationList.size());
		try {
			if (stationList.isEmpty()) {
				// deactivate all active stations, if none are present
				odhClient.syncStationStates(stationNamePrefix, odhClient.getProvenance().getLineage(),
						Arrays.asList("deactivate"), false);
			} else {
				odhClient.syncStations(stationList);
			}
		} catch (WebClientRequestException e) {
			LOG.error("Sync stations failed: Request exception: {}", e.getMessage());
		}

		LOG.info("push trip data about {} car pooling stations", stationList.size());
		try {
			odhClient.pushData(dataMap);
		} catch (WebClientRequestException e) {
			LOG.error("Sync stations failed: Request exception: {}", e.getMessage());
		}
	}

	private void initDataTypes() {
		LOG.info("init car pooling data types");
		Map<String, String> externalDataType = new HashMap<>();
		externalDataType.put("name", DATATYPE_ITINERARY_ID);
		externalDataType.put("unit", "json");

		List<DataTypeDto> odhDataTypeList = new ArrayList<>();
		odhDataTypeList.add(
				new DataTypeDto(
						externalDataType.get("name"),
						externalDataType.get("unit"),
						externalDataType.get("name"),
						"Instantaneous"));

		try {
			odhClient.syncDataTypes(odhDataTypeList);
		} catch (WebClientRequestException e) {
			LOG.error("Sync data types failed: Request exception: {}", e.getMessage());
		}
	}

}
