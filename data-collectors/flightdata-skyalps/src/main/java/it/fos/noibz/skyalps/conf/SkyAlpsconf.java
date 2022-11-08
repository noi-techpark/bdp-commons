package it.fos.noibz.skyalps.conf;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import it.fos.noibz.skyalps.SpringBootApp;
import it.fos.noibz.skyalps.dto.json.AeroCRSFlight;
import it.fos.noibz.skyalps.dto.json.AeroCRSGetScheduleSuccess;
import it.fos.noibz.skyalps.dto.json.AeroCRSGetScheduleSuccessResponse;
import it.fos.noibz.skyalps.rest.AeroCRSRest;
import it.fos.noibz.skyalps.service.SyncScheduler;

@Configuration
public class SkyAlpsconf {
	private static final DateFormat SDF_FORMAT = new SimpleDateFormat("yyyy/MM/dd");
	private static final String IATA = "IATA";
	private static final String BN = "BN";
	private static final Logger LOG = LoggerFactory.getLogger(SpringBootApp.class);

	@Autowired
	private RestTemplate clients;

	@Autowired
	SyncScheduler sync;

	@Value("${commandlinerunner_days_before}")
	private int DAYS_BEFORE;
	@Value("${commandlinerunner_days_after}")
	private int DAYS_AFTER;

	@Value("${ssim_enabled}")
	private boolean ssimEnabled;

	@Bean(name = "appRestClient")
	public static RestTemplate getRestClient() {
		RestTemplate restClient = new RestTemplate(
				new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
		restClient.setInterceptors(Collections.singletonList((request, body, execution) -> {
			LOG.debug("Intercepting...");
			return execution.execute(request, body);
		}));
		return restClient;
	}


	//Boolean to retrieve ssim format
	@Bean
	public boolean ssim(){
		return ssimEnabled;
	}
	
	@SuppressWarnings("static-access")
	@Bean
	public CommandLineRunner runner(RestTemplate clients, boolean ssim) throws Exception {
		// Parsing user string into a Date object
		// Adding fixed amount of days to the Date object
		// Substracting fixed amount of days to the Date object
		// Retrieving flights in between
		Date date = new Date();
		Calendar calFrom = Calendar.getInstance();
		calFrom.setTime(date);
		calFrom.add(Calendar.DATE, -DAYS_BEFORE);
		Calendar calTo = Calendar.getInstance();
		calTo.setTime(date);
		calTo.add(Calendar.DATE, DAYS_AFTER);
		Date fltsFROMperiod = calFrom.getTime();
		Date fltsTOperiod = calTo.getTime();
		LOG.info("Date flight retrieval will start from {} to {}", fltsFROMperiod, fltsTOperiod);
		return args -> {
			AeroCRSGetScheduleSuccessResponse result = AeroCRSRest.getSchedule(clients, fltsFROMperiod, fltsTOperiod,
					IATA, BN, false, ssim);
			LOG.info("The Result is " + result.getAerocrs());
			LOG.info("The result AeroCRS is " + result.getAerocrs());
			if (result.getAerocrs() instanceof AeroCRSGetScheduleSuccess) {
				AeroCRSGetScheduleSuccess success = (AeroCRSGetScheduleSuccess) result.getAerocrs();
				LOG.info("AeroCRSGetScheduleSuccess: " + success);
				if (success.isSuccess()) {
					for (AeroCRSFlight flight : success.getFlight()) {
						LOG.info("Flights: " + flight);
					}
					LOG.info("Result are displayed...moving on sync and push flights: ");
					try {
						sync.syncJobStations();

					} catch (Exception e) {
						e.getCause();
					}
				}
			}

		};
	}

}
