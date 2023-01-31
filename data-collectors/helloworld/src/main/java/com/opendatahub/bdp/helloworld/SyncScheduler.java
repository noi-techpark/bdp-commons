package com.opendatahub.bdp.helloworld;

import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;

@Service
public class SyncScheduler {

	private static final Logger LOG = LoggerFactory.getLogger(SyncScheduler.class);

    private static final String STATION_ID = "some-unique-id-1234";
	private static final String DATATYPE_ID = "Temperature";

	@Value("${odh_client.period}")
    private Integer period;

    @Lazy
    @Autowired
    private OdhClient odhClient;

    /**
     * Scheduled job A: Example to sync stations and data types
     */
    @Scheduled(cron = "${scheduler.job_a}")
    public void syncJobA() {
        LOG.info("Cron job A started: Sync Stations with type {} and data types", odhClient.getIntegreenTypology());

		// 1) Retrieve your station data from some endpoint (this is a mock)
		Map<String, String> externalStation = new HashMap<>();
		externalStation.put("name", "Cool Measurement Station");
		externalStation.put("lat", "46.4861067");
		externalStation.put("lon", "11.3275207");

		Map<String, String> externalDataType = new HashMap<>();
		externalDataType.put("name", DATATYPE_ID);
		externalDataType.put("unit", "°C");

		// 2) Parse results and fill a list of StationDto and DataTypeDto objects
		StationDto station = new StationDto(
			STATION_ID,
			externalStation.get("name"),
			Double.parseDouble(externalStation.get("lat")),
			Double.parseDouble(externalStation.get("lon"))
		);
		// best practice: set also an origin, so that we understand where this station came from
		station.setOrigin(odhClient.getProvenance().getLineage());
		// Use station.getMetaData().put(.....) if you want to add additional information
		StationList odhStationList = new StationList();
		odhStationList.add(station);

		List<DataTypeDto> odhDataTypeList = new ArrayList<>();
		odhDataTypeList.add(
			new DataTypeDto(
				externalDataType.get("name"),
				externalDataType.get("unit"),
				"temperature in celsius from blablabla...",
				"max"  // explaining the metric type (min, max, mean, etc.)
			)
			// Use .getMetaData().put(.....) if you want to add additional information
		);

		// 3) Send it to the Open Data Hub INBOUND API (writer)
		try {
			odhClient.syncStations(odhStationList);
			odhClient.syncDataTypes(odhDataTypeList);
			LOG.info("Cron job A successful");
		} catch (WebClientRequestException e) {
			LOG.error("Cron job A failed: Request exception: {}", e.getMessage());
		}
    }

	/**
     * Scheduled job B: Example on how to send measurements
     */
    @Scheduled(cron = "${scheduler.job_b}")
    public void syncJobB() {
        LOG.info("Cron job B started: Pushing measurements for {}", odhClient.getIntegreenTypology());

		// 1) Retrieve your measurement data from some endpoint (this is a mock)
		//   a) The measurement itself (including a measurement timestamp)
		long externalTimestamp = 1651741173000L;  // we use unix epoch timestamps (in millis) here
		Double externalMeasurement = -12.3;
		//   b) Associate the measurement somehow to an existing station and data type
		String odhStationName = STATION_ID;
		String odhDataTypeName = DATATYPE_ID;

		// 2) Build a data map which has the following form:
		//		(root) >> Station >> DataType >> Measurements
		//
		//    The data map is generic, therefore we need to create single
		//    branches starting from a root pointer.
		//
		// We show each step separately for faster understanding:
		DataMapDto<RecordDtoImpl> rootMap = new DataMapDto<>();
		DataMapDto<RecordDtoImpl> stationMap = rootMap.upsertBranch(odhStationName);
		DataMapDto<RecordDtoImpl> metricMap = stationMap.upsertBranch(odhDataTypeName);
		SimpleRecordDto measurement = new SimpleRecordDto(externalTimestamp, externalMeasurement);
		// The period is an interval between one measurement and the consecutive one (seconds)
		// It tells us, how often something gets measured.
		// For example, every 10 minutes = every 600 seconds
		// Since this is the same for all measurements, and should be configurable we use a property value
		measurement.setPeriod(period);
		List<RecordDtoImpl> values = metricMap.getData();
		values.add(measurement);

		// 3) Send the measurements to the Open Data Hub INBOUND API (writer)
		//    WARNING: stations and datatypes must already exist, otherwise this call will fail
		//             It does not throw any exception, it will just not insert that data (this is a known issue)
		//             Exception will only be thrown on connection errors here! Please refer to the
		//             writer log output or the database itself to see if data has been inserted
		try {
			odhClient.pushData(rootMap);
			LOG.info("Cron job B successful");
		} catch (WebClientRequestException e) {
			LOG.error("Cron job B failed: Request exception: {}", e.getMessage());
		}
    }

}
