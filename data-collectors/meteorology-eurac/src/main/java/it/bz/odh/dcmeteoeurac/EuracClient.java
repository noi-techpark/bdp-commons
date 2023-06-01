// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.odh.dcmeteoeurac;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.bz.odh.dcmeteoeurac.dto.ClimateDailyDto;
import it.bz.odh.dcmeteoeurac.dto.ClimatologyDto;
import it.bz.odh.dcmeteoeurac.dto.MetadataDto;

@Lazy
@Service
public class EuracClient {
    private static final String RESPONSE_CHARSET = "UTF-8";

    private static final String STATION_ID_URL_PARAM = "%STATION_ID%";

    @Value("${endpoint.stations.url}")
    private String stationsUrl;

    @Value("${endpoint.climatologies.url}")
    private String climatologiesUrl;

    @Value("${endpoint.climateDaily.url}")
    private String climateDailyUrl;

    private ObjectMapper objectMapper = new ObjectMapper();

    private HttpClient client = HttpClientBuilder.create().build();

    public MetadataDto[] getStations() throws IOException {
        HttpResponse response = client.execute(new HttpGet(stationsUrl));
        HttpEntity entity = response.getEntity();
        String responseString = EntityUtils.toString(entity, RESPONSE_CHARSET);
        return objectMapper.readValue(responseString, MetadataDto[].class);
    }

    public ClimatologyDto[] getClimatologies() throws IOException {
        HttpResponse response = client.execute(new HttpGet(climatologiesUrl));
        HttpEntity entity = response.getEntity();
        String responseString = EntityUtils.toString(entity, RESPONSE_CHARSET);
        return objectMapper.readValue(responseString, ClimatologyDto[].class);
    }

    public ClimateDailyDto[] getClimateDaily(int stationId) throws IOException {
        HttpResponse response = client
                .execute(new HttpGet(climateDailyUrl.replace(STATION_ID_URL_PARAM, String.valueOf(stationId))));
        HttpEntity entity = response.getEntity();
        String responseString = EntityUtils.toString(entity, RESPONSE_CHARSET);
        return objectMapper.readValue(responseString, ClimateDailyDto[].class);
    }
}
