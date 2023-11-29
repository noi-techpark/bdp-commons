// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

/*
 *  Parking Forecast Data Collector - Main Application
 *
 *  (C) 2021 NOI Techpark Südtirol / Alto Adige
 *
 *  changelog:
 *  2022-02-23  1.0 - thomas.nocker@catch-solve.tech
 */

package it.bz.noi.sta.parkingforecast;

import it.bz.idm.bdp.dto.*;
import it.bz.noi.sta.parkingforecast.configuration.DatatypeConfiguration;
import it.bz.noi.sta.parkingforecast.configuration.DatatypesConfiguration;
import it.bz.noi.sta.parkingforecast.dto.ParkingForecastDataPoint;
import it.bz.noi.sta.parkingforecast.dto.ParkingForecastResult;
import it.bz.noi.sta.parkingforecast.pusher.AbstractParkingForecastJSONPusher;
import it.bz.noi.sta.parkingforecast.pusher.ParkingSensorJSONPusher;
import it.bz.noi.sta.parkingforecast.pusher.ParkingStationJSONPusher;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.*;

@Component
public class JobScheduler {

	@Autowired
	private ParkingSensorJSONPusher parkingSensorJSONPusher;
	@Autowired
	private ParkingStationJSONPusher parkingStationJSONPusher;
	@Autowired
	private DatatypesConfiguration datatypesConfiguration;

	@Autowired
	private ParkingForecastConnector staParkingForecastConnector;

	private static Logger LOG = LoggerFactory.getLogger(JobScheduler.class);

	@Scheduled(cron = "${scheduler.job}")
	public void execute() {
		LOG.info("MainParkingForecast execute");
		try {
			ParkingForecastResult parkingForecastResult = staParkingForecastConnector.getParkingForecastResult();
			LOG.debug(
					"got timeseries about {} stations with publish_timestamp {}, forecast_start_timestamp {}, forecast_period_seconds {}, forecast_duration_hours {}",
					parkingForecastResult.getStationTimeseriesMap().size(),
					parkingForecastResult.getPublishTimestamp(),
					parkingForecastResult.getForecastStartTimestamp(),
					parkingForecastResult.getForecastPeriodSeconds(),
					parkingForecastResult.getForecastDurationHours());

			for (AbstractParkingForecastJSONPusher jsonPusher : getAllJsonPusher()) {
				setupDataType(jsonPusher);
				exportData(jsonPusher, parkingForecastResult);
			}
		} catch (Exception e) {
			LOG.error("reading parking station timeseries failed ", e);
		}
	}

	private void exportData(AbstractParkingForecastJSONPusher jsonPusher, ParkingForecastResult parkingForecastResult) {
		List<StationDto> stationList = jsonPusher.fetchStations(jsonPusher.initIntegreenTypology(), null);
		LOG.debug("got {} station with the type of {}", stationList.size(), jsonPusher.initIntegreenTypology());

		DataMapDto<RecordDtoImpl> dataMap = new DataMapDto<>();

		int pushDataCount = 0;
		int skipDataCount = 0;
		int stationNoUpdateCount = 0;
		int stationNoDataCount = 0;

		for (StationDto stationDto : stationList) {
			String stationCode = stationDto.getId();
			List<ParkingForecastDataPoint> forecastTimeseries = parkingForecastResult.getStationTimeseriesMap()
					.get(stationCode);
			if (forecastTimeseries != null) {
				long timestampMillis = parkingForecastResult.getPublishTimestamp().toEpochSecond() * 1000;
				Date dateOfLastRecord = getDateOfLastRecord(jsonPusher,
						stationCode,
						datatypesConfiguration.getAllDataTypes().stream()
								.filter(datatypeConfiguration -> datatypeConfiguration.getProperty().equals("mean"))
								.sorted((o1, o2) -> o1.getPeriod().compareTo(o2.getPeriod())).findFirst().get());
				if (dateOfLastRecord == null || timestampMillis != dateOfLastRecord.getTime()) {
					for (DatatypeConfiguration datatypeConfiguration : datatypesConfiguration.getAllDataTypes()) {
						ZonedDateTime forecastDatatypeTimestamp = parkingForecastResult.getForecastStartTimestamp()
								.plusSeconds(datatypeConfiguration.getPeriod());
						Optional<ParkingForecastDataPoint> forecastDataPoint = forecastTimeseries.stream()
								.filter(f -> f.getTs().equals(forecastDatatypeTimestamp)).findFirst();
						Object propertyValue = forecastDataPoint.isPresent()
								? forecastDataPoint.get().getProperty(datatypeConfiguration.getProperty())
								: null;
						if (propertyValue != null) {
							dataMap.addRecord(stationCode, datatypeConfiguration.getKey(),
									new SimpleRecordDto(forecastDatatypeTimestamp.toEpochSecond() * 1000, propertyValue,
											datatypeConfiguration.getPeriod()));
							pushDataCount++;
						} else
							skipDataCount++;
					}
				} else
					stationNoUpdateCount++;
			} else
				stationNoDataCount++;
		}

		if (pushDataCount > 0)
			jsonPusher.pushData(dataMap);

		LOG.debug(
				"{} forecast records pushed, {} records skipped, {} stations with no update, {} stations with no forecast data",
				pushDataCount, skipDataCount, stationNoUpdateCount, stationNoDataCount);
	}

	private void setupDataType(AbstractParkingForecastJSONPusher jsonPusher) {
		List<DataTypeDto> dataTypeDtoList = new ArrayList<>();
		for (DatatypeConfiguration datatypeConfiguration : datatypesConfiguration.getAllDataTypes()) {
			dataTypeDtoList.add(
					new DataTypeDto(datatypeConfiguration.getKey(),
							datatypeConfiguration.getUnit(),
							datatypeConfiguration.getDescription(),
							datatypeConfiguration.getRtype()));
		}
		jsonPusher.syncDataTypes(dataTypeDtoList);
	}

	private List<AbstractParkingForecastJSONPusher> getAllJsonPusher() {
		return Arrays.asList(parkingSensorJSONPusher, parkingStationJSONPusher);
	}

	private Date getDateOfLastRecord(AbstractParkingForecastJSONPusher jsonPusher, String stationCode,
			DatatypeConfiguration datatypeConfiguration) {
		Object dateOfLastRecord = jsonPusher.getDateOfLastRecord(stationCode, datatypeConfiguration.getKey(),
				datatypeConfiguration.getPeriod());
		if (dateOfLastRecord == null)
			return null;
		return (Date) dateOfLastRecord;
	}
}
