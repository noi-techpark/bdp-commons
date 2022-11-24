package it.fos.noibz.skyalps.rest;

import java.io.IOException;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.fos.noibz.skyalps.dto.json.AeroCRSGetScheduleSuccessResponse;
import it.fos.noibz.skyalps.dto.json.AeroCRSParms;
import it.fos.noibz.skyalps.dto.json.AeroCRSRequest;
import it.fos.noibz.skyalps.dto.json.GetScheduleRequest;
import it.fos.noibz.skyalps.dto.string.AeroCRSGetScheduleSuccessResponseString;
import it.fos.noibz.skyalps.service.AeroCRSConst;

/**
 *
 * @author Thierry BODHUIN, bodhuin@gmail.com
 */
public class AeroCRSRest {

	private static RestTemplate restTemplate = new RestTemplate();
	private static final String URL_GET_SCHEDULE = "https://api.aerocrs.com/v5/getSchedule";
	private static final Logger LOG = LoggerFactory.getLogger(AeroCRSRest.class);

	@RequestMapping(value = "/getSchedule", method = RequestMethod.POST)
	@ResponseBody
	public static AeroCRSGetScheduleSuccessResponse getSchedule(RestTemplate restTemplate,
			@RequestParam @DateTimeFormat(pattern = "yyyy/MM/dd") Date fltsFROMperiod, // YYYY/MM/DD start period of the
																						// required data
			@RequestParam @DateTimeFormat(pattern = "yyyy/MM/dd") Date fltsTOperiod, // YYYY/MM/DD end period of the
																						// required data
			@RequestParam String codeformat, // IATA / ICAO / INTERNAL / AIRLINE – output of codes
			// IATA - the system will generate the output only for routes with IATA codes,
			// and the output codes will be IATA
			// ICAO - the system will generate the output only for routes with ICAO codes,
			// and the output codes will be ICAO
			// INTERNAL - The system will generate the output with internal assigned codes
			// for non IATA destinations, note that AeroCRS internal codes can conflict with
			// real IATA destinations, the rest of the destinations will be in IATA codes.
			// AIRLINE - The system will output the destinations in the airline defined
			// codes
			@RequestParam String companycode, // Company short code (as supplied to you by the team) - Adding this
												// element will narrow the search of the data for the specific airline.
			@RequestParam boolean soldonline, // when set to true will show only flights which are sold online.
			@RequestParam(required = false) boolean ssim
	// 1 - when the response should be in an SSIM format, 0 - string SSIM format
	// (for non SSIM results do not send this prameter)) {
	) throws MalformedURLException, IOException, ParseException {
		// create an instance of RestTemplate
		// RestTemplate restTemplate = new RestTemplate();
		// create headers
		HttpHeaders headers = new HttpHeaders();
		// headers.setBasicAuth(AUTH_ID, AUTH_PASSWORD);
		headers.set("auth_id", AeroCRSConst.getAUTHID_STATIC());
		headers.set("auth_password", AeroCRSConst.getAUTHPASSWORD_STATIC());
		// set `content-type` header
		headers.setContentType(MediaType.APPLICATION_JSON);
		// set `accept` header
		headers.setAccept(Arrays.asList(MediaType.ALL) /* Collections.singletonList(MediaType.APPLICATION_JSON) */);
		// build the request
		GetScheduleRequest getScheduleRequest = new GetScheduleRequest(fltsFROMperiod, fltsTOperiod, codeformat,
				companycode, soldonline, ssim);
		// build the request
		HttpEntity<AeroCRSRequest> request = new HttpEntity<>(new AeroCRSRequest(new AeroCRSParms(getScheduleRequest)),
				headers);

		if (ssim == false) {
			// send POST request
			ResponseEntity<AeroCRSGetScheduleSuccessResponse> response = restTemplate.postForEntity(URL_GET_SCHEDULE,
					request, AeroCRSGetScheduleSuccessResponse.class);
			// check response
			if (response.getStatusCode() == HttpStatus.OK && request.getBody() != null) {
				LOG.info("Request Successful");
				LOG.info("" + response.getBody());
				return response.getBody();

			} else {
				LOG.info("Request Failed");
				LOG.info("" + response.getStatusCode());
			}
		} else if (ssim == true) {
			restTemplate.getMessageConverters().add(0, mappingJacksonHttpMessageConverter());
			ResponseEntity<AeroCRSGetScheduleSuccessResponseString> response = restTemplate
					.postForEntity(URL_GET_SCHEDULE, request, AeroCRSGetScheduleSuccessResponseString.class);
			// check response
			if (response.getStatusCode() == HttpStatus.OK && request.getBody() != null) {
				LOG.info("Request Successful");
				LOG.info("" + response.getBody());
				return response.getBody().getAerocrs().decodeFlights();

			} else {
				LOG.info("Request Failed");
				LOG.info("" + response.getStatusCode());
			}
		}
		return (null);
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
