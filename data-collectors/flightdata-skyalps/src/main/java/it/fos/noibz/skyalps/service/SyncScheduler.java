package it.fos.noibz.skyalps.service;

import java.io.IOException;
import java.text.ParseException;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;
import it.fos.noibz.skyalps.dto.json.fares.AeroCRSFare;
import it.fos.noibz.skyalps.dto.json.fares.AeroCRSFares;
import it.fos.noibz.skyalps.dto.json.fares.AeroCRSFaresSuccessResponse;
import it.fos.noibz.skyalps.dto.json.fares.ODHFare;
import it.fos.noibz.skyalps.dto.json.realtime.RealtimeDto;
import it.fos.noibz.skyalps.dto.json.schedule.AeroCRSFlight;
import it.fos.noibz.skyalps.dto.json.schedule.AeroCRSGetScheduleSuccessResponse;
import it.fos.noibz.skyalps.dto.string.AereoCRSConstants;
import it.fos.noibz.skyalps.rest.AeroCRSRest;
import it.fos.noibz.skyalps.rest.RealTimeClient;

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

	@Value("${ssim_enabled}")
	private boolean ssimEnabled;

	@Autowired
	AeroCRSConst aeroconst;

	@Lazy
	@Autowired
	private AeroCRSRest acrsclient;

	@Lazy
	@Autowired
	private RealTimeClient realTimeClient;

	@Lazy
	@Autowired
	private ODHClient odhclient;


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
		// Sync stations
		/////////////////////////
		// Get current date
		// Subtracting certain amount of days defined in the application.properties
		// Adding certain amount of days defined in the application.properties
		// Synch stations in between the date

		StationList allStationList = new StationList();

		// fares requests give fares for a time range, so we can reuse fares and don't
		// have to request the same every time
		Map<String, Map<String, ODHFare>> faresByFlightNumber = new HashMap<>();

		LOG.info("Cron job started: Sync Stations with type {} and data types", odhclient.getIntegreenTypology());

		LOG.info("Get schedules and fares...");
		for (int i = 0 - days_before; i < days_after; i++) {

			Calendar from = Calendar.getInstance();
			from.add(Calendar.DATE, i);
			Date fltsFROMPeriod = from.getTime();
			Calendar to = Calendar.getInstance();
			to.add(Calendar.DATE, i + 1);
			Date fltsTOPeriod = to.getTime();

			StationList stationList = new StationList();

			// SCHEDULES

			AeroCRSGetScheduleSuccessResponse aero = acrsclient.getSchedule(fltsFROMPeriod, fltsTOPeriod,
					aeroconst.getIatacode(), aeroconst.getCompanycode(), false, ssimEnabled);

			for (AeroCRSFlight dto : aero.getAerocrs().getFlight()) {

				StationDto stationDto = new StationDto();
				stationDto.setId(dto.getFltnumber() + "_" + dto.getDate());
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
				metaData.put(aeroconst.getWeekdaythu(), dto.getWeekdaythu());
				metaData.put(aeroconst.getWeekdayfri(), dto.getWeekdayfri());
				metaData.put(aeroconst.getWeekdaysat(), dto.getWeekdaysat());
				metaData.put(aeroconst.getWeekdaysun(), dto.getWeekdaysun());
				metaData.put(aeroconst.getFromdestination(), dto.getFromdestination());
				metaData.put(aeroconst.getTodestination(), dto.getTodestination());
				// unix timestamp
				metaData.put("departure_timestamp", dto.getDepartureTimestamp());
				metaData.put("arrival_timestamp", dto.getArrivalTimestamp());
				metaData.put("ssim", dto.getSsimMessage());
				stationDto.setMetaData(metaData);

				stationList.add(stationDto);

			}
			LOG.debug("Get schedules done.");

			// FARES
			LOG.debug("Get fares...");
			for (StationDto stationDto : stationList) {

				// use flight number and to period as key for mapping
				// because a flight with same number can be requested for 2 different fare
				// periods
				String flightNumber = String.valueOf(stationDto.getMetaData().get(aeroconst.getFltnumber()));
				String toPeriod = String.valueOf(stationDto.getMetaData().get(aeroconst.getFltstoperiod()));

				Date flightToPeriodDate = AereoCRSConstants.DATE_FORMAT.parse(toPeriod);

				Map<String, ODHFare> cachedFares = faresByFlightNumber.getOrDefault(flightNumber, null);

				// check if not already fetched
				if (cachedFares == null || cachedFares.values().isEmpty() || !AereoCRSConstants.DATE_FORMAT
						.parse(cachedFares.values().iterator().next().getFare().getToDate())
						.after(flightToPeriodDate)) {
					LOG.debug("Getting fares for: {}", flightNumber);
					AeroCRSFaresSuccessResponse faresResponse = acrsclient.getFares(fltsFROMPeriod, fltsTOPeriod,
							(String) stationDto.getMetaData().get(aeroconst.getFromdestination()),
							(String) stationDto.getMetaData().get(aeroconst.getTodestination()));

					if (faresResponse != null && faresResponse.getAerocrs().isSuccess()
							&& faresResponse.getAerocrs().getFares() != null) {
						AeroCRSFares fares = faresResponse.getAerocrs().getFares();

						List<AeroCRSFare> decodeFare = fares.decodeFare();

						// map fares by type like SKY LIGHT, SKY BASIC, SKY GO, SKY PLUS
						Map<String, ODHFare> faresByType = new HashMap<>();
						for (AeroCRSFare fare : decodeFare) {
							String key = fare.getType().replace(" ", "_");
							Date farePeriodToDate = AereoCRSConstants.DATE_FORMAT.parse(fare.getToDate());
							if (!faresByType.containsKey(key) && !fltsTOPeriod.after(farePeriodToDate)) {
								faresByType.put(key, new ODHFare(fare));
							}
						}
						faresByFlightNumber.put(flightNumber, faresByType);
						LOG.debug("Getting fares for {} done.", flightNumber);

					} else {
						LOG.info("fares request not successful");
					}
				} else
					LOG.debug("From cache getting for {}.", flightNumber);

				stationDto.getMetaData().put("fares", faresByFlightNumber.get(flightNumber));
			}

			allStationList.addAll(stationList);

			LOG.debug("Get fares done.");
		}

		LOG.info("Get schedules and fares done.");

		LOG.info("Trying to sync the stations...");

		odhclient.syncStations(odhclient.getIntegreenTypology(), allStationList);
		LOG.info("Syncing stations done.");

	}


	@Scheduled(cron = "${scheduler.push_data}")
	public void realtimeData() throws IOException, ParseException {
        DataMapDto<RecordDtoImpl> dataMap = new DataMapDto<>();

		RealtimeDto realTimeData = realTimeClient.getRealTimeData();


		// SimpleRecordDto dto = new SimpleRecordDto(recordTimeLong, "", 600);
		// dataMap.addRecord(stationDto.getId(), datatypesConfiguration.getItineraryDetails().getKey())
		// );

		odhclient.pushData(dataMap);
	}
}