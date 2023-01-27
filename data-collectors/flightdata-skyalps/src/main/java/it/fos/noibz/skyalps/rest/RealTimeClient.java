package it.fos.noibz.skyalps.rest;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.fos.noibz.skyalps.dto.json.realtime.RealtimeDto;

@Lazy
@Service
public class RealTimeClient {

    private final Logger LOG = LoggerFactory.getLogger(RealTimeClient.class);

    @Value("${AUTH_TOKEN_REAL_TIME}")
    private String token;

    private final String URL = "https://dataprovider.ifly.aero:8443/fidsdataproviderproxy/dataProvider.ashx";
    private RestTemplate restTemplate;

    private ObjectMapper mapper;

    public RealTimeClient() {
        restTemplate = new RestTemplate();
        mapper = new ObjectMapper();
    }

    public RealtimeDto getRealTimeData() {
        LOG.debug("Getting Realtime data...");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Token", token);
        headers.set("Accept", "application/json");
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        // API gives text/html as contenttype instead of application/json
        // So reading String and then converting to RealtimeDto
        // When api give correct content type the code below should work
        // ResponseEntity<RealtimeDto> response = restTemplate
        // .exchange(URL, HttpMethod.GET, new HttpEntity<Object>(headers),
        // RealtimeDto.class);

        ResponseEntity<String> response = restTemplate
                .exchange(URL, HttpMethod.GET, new HttpEntity<Object>(headers), String.class);

        String realtimeString = null;

        if (response.getStatusCode() == HttpStatus.OK) {
            LOG.debug("Realtime Request Successful");
            LOG.debug("Response body: {}", response.getBody());
            realtimeString = response.getBody();
        } else {
            LOG.error("Realtime request failed with status code: {}", response.getStatusCode());
            return null;
        }

        if (realtimeString == null || realtimeString.isEmpty()) {
            return null;
        }

        // destinations have scaped quotes: \" and start with "{ and ends with }"
        // removing them to prevent parse exception
        realtimeString = realtimeString.replace("\\\"", "\"");
        realtimeString = realtimeString.replace("\"{", "{");
        realtimeString = realtimeString.replace("}\"", "}");

        try {
            return mapper.readValue(realtimeString, RealtimeDto.class);
        } catch (JsonProcessingException e) {
            LOG.error("Error while parsing json with error: {}", e.getMessage());
        }
        return null;
    }

}
