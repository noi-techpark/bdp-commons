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
import it.bz.noi.sta.parkingforecast.pusher.AbstractParkingForecastJSONPusher;
import it.bz.noi.sta.parkingforecast.pusher.ParkingSensorJSONPusher;
import it.bz.noi.sta.parkingforecast.pusher.ParkingStationJSONPusher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.*;

@Component
public class MainParkingForecast {

	@Autowired
	private ParkingSensorJSONPusher parkingSensorJSONPusher;
	@Autowired
	private ParkingStationJSONPusher parkingStationJSONPusher;
	@Autowired
	private DatatypesConfiguration datatypesConfiguration;

	@Autowired
	private ParkingForecastConnector staParkingForecastConnector;

	private static Logger LOG = LogManager.getLogger(MainParkingForecast.class);

	public void execute() {
		LOG.info("MainParkingForecast execute");
		try {
			Map<String, List<ParkingForecastDataPoint>> forecastDataMap = staParkingForecastConnector.getParkingStationTimeseries();
			LOG.debug("got timeseries about {} stations", forecastDataMap.size());

			ZonedDateTime now = ZonedDateTime.now();

			for (AbstractParkingForecastJSONPusher jsonPusher : getAllJsonPusher()) {
				setupDataType(jsonPusher);
				exportData(jsonPusher, forecastDataMap, now);
			}
		} catch (Exception e) {
			LOG.error("reading parking station timeseries failed ", e);
		}
	}

	private void exportData(AbstractParkingForecastJSONPusher jsonPusher, Map<String, List<ParkingForecastDataPoint>> forecastDataMap, ZonedDateTime now) {
		List<StationDto> stationList = jsonPusher.fetchStations(jsonPusher.initIntegreenTypology(), null);
		LOG.debug("got {} station with the type of {}", stationList.size(), jsonPusher.initIntegreenTypology());

		DataMapDto<RecordDtoImpl> dataMap = new DataMapDto<>();

		int pushDataCount = 0;
		int skipDataCount = 0;
		int noDataCount = 0;

		for (StationDto stationDto : stationList) {
			String stationCode = stationDto.getId();
			List<ParkingForecastDataPoint> forecastTimeseries = forecastDataMap.get(stationCode);
			if (forecastTimeseries != null) {
				for (DatatypeConfiguration datatypeConfiguration : datatypesConfiguration.getAllDataTypes()) {
					ZonedDateTime forecastTstamp = now.plusSeconds(datatypeConfiguration.getPeriod());
					Optional<ParkingForecastDataPoint> forecastDataPoint = forecastTimeseries.stream().filter(f -> f.getTs().isAfter(forecastTstamp)).sorted((o1, o2) -> o1.getTs().compareTo(o2.getTs())).findFirst();
					if (forecastDataPoint.isPresent()) {
						dataMap.addRecord(stationCode, datatypeConfiguration.getKey(),
							new SimpleRecordDto(now.toEpochSecond() * 1000, forecastDataPoint.get().getMean(), datatypeConfiguration.getPeriod()));
						pushDataCount++;
					} else
						skipDataCount++;
				}
			} else
				noDataCount++;
		}

		if (pushDataCount > 0)
			jsonPusher.pushData(dataMap);

		LOG.debug("{} forecast records pushed, {}  records skipped, {} stations with no forecast data", pushDataCount, skipDataCount, noDataCount);
	}

	private void setupDataType(AbstractParkingForecastJSONPusher jsonPusher) {
		List<DataTypeDto> dataTypeDtoList = new ArrayList<>();
		for (DatatypeConfiguration datatypeConfiguration : datatypesConfiguration.getAllDataTypes()) {
			dataTypeDtoList.add(
				new DataTypeDto(datatypeConfiguration.getKey(),
					datatypeConfiguration.getUnit(),
					datatypeConfiguration.getDescription(),
					datatypeConfiguration.getRtype())
			);
		}
		jsonPusher.syncDataTypes(dataTypeDtoList);
	}

	public List<AbstractParkingForecastJSONPusher> getAllJsonPusher() {
		return Arrays.asList(parkingSensorJSONPusher, parkingStationJSONPusher);
	}
}
