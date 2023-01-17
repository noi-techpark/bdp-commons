package it.fos.noibz.skyalps;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import it.fos.noibz.skyalps.dto.json.schedule.AeroCRSFlight;
import it.fos.noibz.skyalps.dto.json.schedule.AeroCRSGetScheduleSuccess;
import it.fos.noibz.skyalps.dto.json.schedule.AeroCRSGetScheduleSuccessResponse;
import it.fos.noibz.skyalps.rest.AeroCRSRest;

//@SpringBootApplication
public class SkyalpsApplication {

	private static final DateFormat SDF_FORMAT = new SimpleDateFormat("yyyy/MM/dd");

	private static final Logger LOG = LoggerFactory.getLogger(SkyalpsApplication.class);
	@Autowired
	private RestTemplate restTemplate;

	public static void main(String[] args) {
		SpringApplication.run(SkyalpsApplication.class, args);
	}

	@Bean(name = "appRestClient")
	public static RestTemplate getRestClient() {
		RestTemplate restClient = new RestTemplate(
				new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
		// Add one interceptor like in your example, except using anonymous class.
		restClient.setInterceptors(Collections.singletonList((request, body, execution) -> {
			LOG.debug("Intercepting...");
			return execution.execute(request, body);
		}));
		return restClient;
	}

	@Bean
	public CommandLineRunner run(RestTemplate restTemplate) throws Exception {
		Date fltsFROMperiod = SDF_FORMAT.parse("2022/05/20");
		Date fltsTOperiod = SDF_FORMAT.parse("2022/06/24");
		return args -> {
			AeroCRSGetScheduleSuccessResponse result = AeroCRSRest.getSchedule(restTemplate, fltsFROMperiod,
					fltsTOperiod, "IATA", "BN", false, false);
			LOG.debug("Result: " + result);
			LOG.debug("Result AeroCRS: " + result.getAerocrs());
			if (result.getAerocrs() instanceof AeroCRSGetScheduleSuccess) {
				AeroCRSGetScheduleSuccess success = (AeroCRSGetScheduleSuccess) result.getAerocrs();
				LOG.debug("AeroCRSGetScheduleSuccess: " + success);
				if (success.isSuccess()) {
					for (AeroCRSFlight flight : success.getFlight()) {
						LOG.debug("Flight: " + flight);
					}
				}
			}
		};
	}
}