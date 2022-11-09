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

	private static final String TYPE_DEST = "flight-destination";
	private static final String TYPE_NR = "flight-number";

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

		//////////////////////////
		// Sync data types
		/////////////////////////
		List<DataTypeDto> dataTypeList = new ArrayList<DataTypeDto>();
		DataTypeDto typeDst = new DataTypeDto(TYPE_DEST, null, "Destination of a flight",
				null);
		DataTypeDto typeNr = new DataTypeDto(TYPE_NR, null, "ID number of a flight",
				null);
		dataTypeList.add(typeDst);
		dataTypeList.add(typeNr);

		LOG.info("Trying to sync data types...");
		odhclient.syncDataTypes(odhclient.getIntegreenTypology(), dataTypeList);
		LOG.info("Syncing data types successful");



		//////////////////////////
		// Sync stations
		/////////////////////////
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

		StationList odhStationlist = new StationList();

		for (AeroCRSFlight dto : aero.getAerocrs().getFlight()) {
			StationDto stationDto = new StationDto();
			stationDto.setId(dto.getFltnumber());
			stationDto.setName(dto.getFromdestination() + "-" + dto.getTodestination());
			stationDto.setOrigin(odhclient.getProvenanceOrigin());
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
		}

		LOG.info("Trying to sync the stations: ");
		odhclient.syncDataTypes(odhclient.getIntegreenTypology(), dataTypeList);
		odhclient.syncStations(odhclient.getIntegreenTypology(), odhStationlist);
		LOG.info("Syncing data types successful");


		//////////////////////////
		// Push data
		/////////////////////////

		DataMapDto<RecordDtoImpl> rootMap = new DataMapDto<RecordDtoImpl>();
		Long currentTimestamp = System.currentTimeMillis();

		for (AeroCRSFlight dto : aero.getAerocrs().getFlight()) {
			DataMapDto<RecordDtoImpl> stationMap = rootMap.upsertBranch(dto.getFltnumber());
			// metricMap.setProvenance(rootMap.getProvenance());
			// Map for type1 DataType
			DataMapDto<RecordDtoImpl> destinationMap = stationMap.upsertBranch(TYPE_DEST);
			// Map for type2 DataType
			DataMapDto<RecordDtoImpl> numberMap = stationMap.upsertBranch(TYPE_NR);
			// Creating new branch for maps

			// Creating lists of data transfer object
			List<RecordDtoImpl> destinationValues = destinationMap.getData();
			destinationValues.add(new SimpleRecordDto(currentTimestamp, dto.getFromdestination() + "-" + dto.getTodestination(), env.getProperty(aeroconst.getEnvperiod(), Integer.class)));
			
			List<RecordDtoImpl> numberValues = numberMap.getData();
			numberValues.add(new SimpleRecordDto(currentTimestamp, dto.getFltnumber(), env.getProperty(aeroconst.getEnvperiod(), Integer.class)));

		}

		// Stations Lists push
		LOG.info("Trying to push the data: ");
		odhclient.pushData(rootMap);
		LOG.info("Pushing data successful");

	}

}