package it.fos.noibz.skyalps.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;
import it.fos.noibz.skyalps.dto.json.AeroCRSFlight;
import it.fos.noibz.skyalps.dto.json.AeroCRSGetScheduleSuccessResponse;
import it.fos.noibz.skyalps.rest.AeroCRSRest;

//This class is responsible to schedule retrieve and push operations for Stations.
//It has been structured in the same way the meteorology eurac data collector does. 
@Lazy
@Service
public class SyncScheduler {
	/**
	 * Cronjob configuration can be found under
	 * src/main/resources/META-INF/spring/applicationContext.xml XXX Do not forget
	 * to configure it!
	 */
	private static final Logger LOG = LoggerFactory.getLogger(SyncScheduler.class);

	@Value("${odh_client.STATION_ID_PREFIX}")
	private String STATION_ID_PREFIX;

	@Value("${odh_client.LA}")
	private Double LA;

	@Value("${odh_client.LON}")
	private Double LON;

	@Value("${sched_days_before}")
	private int days_before;

	@Value("${sched_days_after}")
	private int days_after;

	@Autowired
	AeroCRSConst aeroconst;

	@Autowired
	RestTemplate template;

	@Lazy
	@Autowired
	private AeroCRSRest acrsclient;

	@Lazy
	@Autowired
	private ODHClient odhclient;

	@Autowired
	Environment env;

	/**
	 * Scheduled job A: sync stations and data types
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */

	@SuppressWarnings("static-access")
	@Scheduled(cron = "${scheduler.job_stations}")
	public void syncJobStations() throws IOException, ParseException {
		// Get current date
		// Subtracting certain amount of days defined in the application.properties
		// Adding certain amount of days defined in the application.properties
		// Synch stations in between the date
		Calendar from = Calendar.getInstance();
		from.add(Calendar.DATE, -days_before);
		Date fltsFROMPeriod = from.getTime();
		Calendar to = Calendar.getInstance();
		to.add(Calendar.DATE, +days_after);
		Date fltsTOPeriod = to.getTime();
		LOG.info("Cron job A started: Sync Stations from {} to {}", fltsFROMPeriod, fltsTOPeriod);
		// {} refers to odh.stationtype which is Flights
		LOG.info("Cron job A started: Sync Stations with type {} and data types", odhclient.getIntegreenTypology());

		AeroCRSGetScheduleSuccessResponse aero = acrsclient.getSchedule(template, fltsFROMPeriod, fltsTOPeriod,
				aeroconst.getIatacode(), aeroconst.getCompanycode(), false, false);
		List<DataTypeDto> dataTypeList = new ArrayList<DataTypeDto>();

		StationList odhStationlist = new StationList();

		for (AeroCRSFlight dto : aero.getAerocrs().getFlight()) {
			StationDto stationDto = new StationDto();
			stationDto.setOrigin(odhclient.getProvenanceOrigin());
			stationDto.setName(odhclient.getProvenanceName());
			stationDto.setLatitude(LA);
			stationDto.setLongitude(LON);
			Map<String, Object> metaData = new HashMap<>();
			metaData.put(aeroconst.getAirlineId(), dto.getAirlineid());
			metaData.put(aeroconst.getAirlinename(), dto.getAirlinename());
			metaData.put(aeroconst.getAccode(), dto.getAccode());
			metaData.put(aeroconst.getFltnumber(), dto.getFltnumber());
			metaData.put(aeroconst.getFltstoperiod(), dto.getFltstoperiod());
			metaData.put(aeroconst.getFltsfromperiod(), dto.getFltsfromperiod());
			metaData.put(aeroconst.getSta(), dto.getSta());
			metaData.put(aeroconst.getStd(), dto.getStd());
			metaData.put(aeroconst.getWeekdaymon(), dto.getWeekdaymon());
			metaData.put(aeroconst.getWeekdaytue(), dto.getWeekdaytue());
			metaData.put(aeroconst.getWeekdaywed(), dto.getWeekdaywed());
			metaData.put(aeroconst.getWeekdaywed(), dto.getWeekdaythu());
			metaData.put(aeroconst.getWeekdayfri(), dto.getWeekdayfri());
			metaData.put(aeroconst.getWeekdaysat(), dto.getWeekdaysat());
			metaData.put(aeroconst.getWeekdaysun(), dto.getWeekdaysun());
			metaData.put(aeroconst.getFromdestination(), dto.getFromdestination());
			metaData.put(aeroconst.getTodestination(), dto.getTodestination());
			stationDto.setMetaData(metaData);
			odhStationlist.add(stationDto);
			// DataType object representing a specific data type, values are:
			// unique name identifier: SkyAlps
			// Unit of the data type - /
			// Descriptions of the data: Flights
			// Metric of specific measurements: Fromtodestination
			// Data for fltnumber will be added in the type3Map below
			DataTypeDto type = new DataTypeDto(STATION_ID_PREFIX, null, odhclient.getIntegreenTypology(),
					aeroconst.getFromtodestination());

			dataTypeList.add(type);

			// DataType object representing a specific data type, values are:
			// unique name identifier: SkyAlps
			// Unit of the data type - /
			// Descriptions of the data: Flights
			// Metric of specific measurements: Fltnumber
			// Data for fltnumber will be added in the type2Map below
			DataTypeDto type2 = new DataTypeDto(STATION_ID_PREFIX, null, odhclient.getIntegreenTypology(),
					aeroconst.getFltnumber());

			dataTypeList.add(type2);
		}
		LOG.info("Stations print: ");
		for (int i = 0; i < odhStationlist.size(); i++) {
			System.out.println(odhStationlist.get(i).getMetaData());
		}
		// {} refers to odh_stationtype which is Flights
		LOG.info("Cron job C started: Pushing {}", odhclient.getIntegreenTypology());

		DataMapDto<RecordDtoImpl> rootMap = new DataMapDto<RecordDtoImpl>();
		rootMap.setProvenance(odhclient.getProvenanceOrigin());
		// Map for metadata
		DataMapDto<RecordDtoImpl> metricMap = new DataMapDto<RecordDtoImpl>();
		metricMap.setProvenance(rootMap.getProvenance());
		// Map for type1 DataType
		DataMapDto<RecordDtoImpl> type1Map = new DataMapDto<RecordDtoImpl>();
		// Map for type2 DataType
		DataMapDto<RecordDtoImpl> type2Map = new DataMapDto<RecordDtoImpl>();
		for (AeroCRSFlight dto : aero.getAerocrs().getFlight()) {
			// Creating new branch for maps
			metricMap = rootMap.upsertBranch(STATION_ID_PREFIX);
			type1Map = metricMap.upsertBranch(aeroconst.getFromtodestination());
			type2Map = metricMap.upsertBranch(aeroconst.getFltnumber());
			// Creating lists of data transfer object
			List<RecordDtoImpl> values = metricMap.getData();
			List<RecordDtoImpl> valuesType1 = type1Map.getData();
			List<RecordDtoImpl> valuesType2 = type2Map.getData();
			// creating simple records to be added in the Metadata list map
			SimpleRecordDto simpleRecordDto = new SimpleRecordDto();
			simpleRecordDto.setTimestamp(null);
			simpleRecordDto.setPeriod(env.getProperty(aeroconst.getEnvperiod(), Integer.class));
			simpleRecordDto.setValue(dto);
			values.add(simpleRecordDto);
			// creating simple records to be added in the Type1 DataType map
			SimpleRecordDto simpleRecordDtoType1 = new SimpleRecordDto();
			simpleRecordDtoType1.setValue(dto.getFromdestination() + " " + dto.getTodestination());
			valuesType1.add(simpleRecordDtoType1);
			// creating simple records to be added in the Type2 DataType map
			SimpleRecordDto simpleRecordDtoType2 = new SimpleRecordDto();
			simpleRecordDtoType2.setValue(dto.getFltsfromperiod() + " " + dto.getFltstoperiod());
			valuesType2.add(simpleRecordDtoType2);
		}

		// Stations Lists sync
		try {
			LOG.info("Trying to sync the stations: ");
			odhclient.syncStations(odhclient.getIntegreenTypology(), odhStationlist);
			odhclient.syncDataTypes(odhclient.getIntegreenTypology(), dataTypeList);
			LOG.info("Cron job for stations successful");
		} catch (WebClientRequestException e) {
			LOG.error("Cron job for stations failed: Request exception: {}", e.getMessage());
		}
		// Stations Lists push
		try {
			/*
			 * LOG.info("Map Provenance is: "); System.out.println(rootMap.getProvenance());
			 * LOG.info("Map Data are: "); for (int i = 0; i < metricMap.getData().size();
			 * i++) { System.out.println(metricMap.getData().get(i)); } for (int i = 0; i <
			 * type1Map.getData().size(); i++) {
			 * System.out.println(type1Map.getData().get(i)); } for (int i = 0; i <
			 * type2Map.getData().size(); i++) {
			 * System.out.println(type2Map.getData().get(i)); }
			 */
			LOG.info("Trying to push the data: ");
			odhclient.pushData(odhclient.getIntegreenTypology(), rootMap);
			LOG.info("Pushing job for stations successful");

		} catch (Exception e) {
			LOG.error("Cron job Push for stations failed: Request exception: {}", e.getMessage());

		}

	}
}