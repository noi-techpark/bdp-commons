package it.fos.noibz.skyalps.rest;

import java.io.IOException;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.fos.noibz.skyalps.dto.json.AeroCRSParms;
import it.fos.noibz.skyalps.dto.json.AeroCRSRequest;
import it.fos.noibz.skyalps.dto.json.fares.AeroCRSFaresSuccessResponse;
import it.fos.noibz.skyalps.dto.json.schedule.AeroCRSGetScheduleSuccessResponse;
import it.fos.noibz.skyalps.dto.json.schedule.GetScheduleRequest;
import it.fos.noibz.skyalps.dto.string.AereoCRSConstants;
import it.fos.noibz.skyalps.dto.string.AeroCRSGetScheduleSuccessResponseString;

/**
 *
 * @author Thierry BODHUIN, bodhuin@gmail.com
 */
@Service
public class AeroCRSRest {

	private final String URL_GET_SCHEDULE = "https://api.aerocrs.com/v5/getSchedule";
	private final String URL_GET_FARES = "https://api.aerocrs.com/v5/getFares";

	private final Logger LOG = LoggerFactory.getLogger(AeroCRSRest.class);

	@Value("${auth.fares.id}")
	private String faresAuthId;

	@Value("${auth.fares.password}")
	private String faresAuthPassword;

	@Value("${auth.schedule.id}")
	private String scheduleAuthId;

	@Value("${auth.schedule.password}")
	private String scheduleAuthPassword;

	@Value("${fares.currency}")
	private String currency;

	private RestTemplate restTemplate;

	public AeroCRSRest() {
		restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(0, mappingJacksonHttpMessageConverter());
	}

	// IATA / ICAO / INTERNAL / AIRLINE – output of codes
	// IATA - the system will generate the output only for routes with IATA codes,
	// and the output codes will be IATA
	// ICAO - the system will generate the output only for routes with ICAO codes,
	// and the output codes will be ICAO
	// INTERNAL - The system will generate the output with internal assigned codes
	// for non IATA destinations, note that AeroCRS internal codes can conflict with
	// real IATA destinations, the rest of the destinations will be in IATA codes.
	// AIRLINE - The system will output the destinations in the airline defined
	// codes
	public AeroCRSGetScheduleSuccessResponse getSchedule(Date fltsFROMperiod, Date fltsTOperiod, String codeformat,
			String companycode, boolean soldonline, boolean ssim)
			throws MalformedURLException, IOException, ParseException {
		HttpHeaders headers = new HttpHeaders();
		headers.set("auth_id", scheduleAuthId);
		headers.set("auth_password", scheduleAuthPassword);
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.ALL) /* Collections.singletonList(MediaType.APPLICATION_JSON) */);

		GetScheduleRequest getScheduleRequest = new GetScheduleRequest(fltsFROMperiod, fltsTOperiod, codeformat,
				companycode, soldonline, ssim);
		HttpEntity<AeroCRSRequest> request = new HttpEntity<>(new AeroCRSRequest(new AeroCRSParms(getScheduleRequest)),
				headers);

		if (ssim == false) {
			ResponseEntity<AeroCRSGetScheduleSuccessResponse> response = restTemplate.postForEntity(URL_GET_SCHEDULE,
					request, AeroCRSGetScheduleSuccessResponse.class);
			if (response.getStatusCode() == HttpStatus.OK && request.getBody() != null) {
				LOG.debug("SSIM Schedule Request Successful");
				LOG.debug("" + response.getBody());
				return response.getBody();

			} else {
				LOG.debug("SSIM Schedule Request Failed");
				LOG.debug("" + response.getStatusCode());
			}
		} else if (ssim == true) {
			ResponseEntity<AeroCRSGetScheduleSuccessResponseString> response = restTemplate
					.postForEntity(URL_GET_SCHEDULE, request, AeroCRSGetScheduleSuccessResponseString.class);
			if (response.getStatusCode() == HttpStatus.OK && request.getBody() != null) {
				LOG.debug("Schedule Request Successful");
				LOG.debug("" + response.getBody());
				return response.getBody().getAerocrs().decodeFlights();

			} else {
				LOG.debug("Schedule Request Failed");
				LOG.debug("" + response.getStatusCode());
			}
		}
		return null;
	}

	public AeroCRSFaresSuccessResponse getFares(
			Date fltsFROMperiod, Date fltsTOperiod, String from, String to) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("auth_id", faresAuthId);
		headers.set("auth_password", faresAuthPassword);
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.ALL));

		Map<String, String> params = new HashMap<>();
		params.put("start", AereoCRSConstants.DATE_FORMAT.format(fltsFROMperiod));
		params.put("end", AereoCRSConstants.DATE_FORMAT.format(fltsTOperiod));
		params.put("from", from);
		params.put("to", to);

		ResponseEntity<AeroCRSFaresSuccessResponse> response = restTemplate
				.exchange(URL_GET_FARES + "?start={start}&end={end}&from={from}&to={to}",
						HttpMethod.GET, new HttpEntity<Object>(headers), AeroCRSFaresSuccessResponse.class, params);

		if (response.getStatusCode() == HttpStatus.OK) {
			LOG.debug("Fares Request Successful");
			return response.getBody();

		} else {
			LOG.debug("Fares Request Failed");
			LOG.debug("" + response.getStatusCode());
		}
		return null;
	}

	public static MappingJackson2HttpMessageConverter mappingJacksonHttpMessageConverter() {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setObjectMapper(myObjectMapper());
		return converter;

	}

	public static ObjectMapper myObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
	}
}
