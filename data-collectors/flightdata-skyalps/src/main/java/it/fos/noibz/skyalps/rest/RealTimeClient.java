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

import it.fos.noibz.skyalps.dto.json.realtime.RealtimeDto;

@Lazy
@Service
public class RealTimeClient {

    private final Logger LOG = LoggerFactory.getLogger(RealTimeClient.class);

    @Value("${AUTH_TOKEN_REAL_TIME}")
    private String token;

    private final String URL = "https://dataprovider.ifly.aero:8443/fidsdataproviderproxy/dataProvider.ashx";
    private RestTemplate restTemplate;

    public RealTimeClient() {
        restTemplate = new RestTemplate();
    }

    public RealtimeDto getRealTimeData() {
        LOG.debug("Getting Realtime data...");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));



        ResponseEntity<RealtimeDto> response = restTemplate
                .exchange(URL, HttpMethod.GET, new HttpEntity<Object>(headers), RealtimeDto.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            LOG.debug("Realtime Request Successful");
            LOG.debug("Response body: {}", response.getBody());
            return response.getBody();

        } else {
            LOG.debug("Realtime Request Failed");
            LOG.debug("" + response.getStatusCode());
        }
        return null;
    }

}
