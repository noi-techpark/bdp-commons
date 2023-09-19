// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.traffic.a22.forecast.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.opendatahub.traffic.a22.forecast.dto.ForecastDto;

@Service
public class A22Client {
    private final Logger LOG = LoggerFactory.getLogger(A22Client.class);
    private final String ENDPOINT_FORECAST = "/GetPrevisioniTrafficoComplete";

    @Value("${a22.url}")
    private String url;

    @Value("${a22.username}")
    private String userName;

    @Value("${a22.password}")
    private String password;

    // public A22Client() {

    // LOG.info("username {}", userName);
    // LOG.info("password {}", password);
    // client = WebClient.builder()
    // .defaultHeaders(header -> header.setBasicAuth(userName, password))
    // .baseUrl(url)
    // .build();
    // }

    public ForecastDto getForecasts(String year, String month) {
        WebClient client = WebClient.builder()
                .defaultHeaders(header -> header.setBasicAuth(userName, password))
                .baseUrl(url)
                .build();

        return client.post().uri(u -> u.path(ENDPOINT_FORECAST).build()).accept(MediaType.ALL)
                .contentType(MediaType.APPLICATION_JSON)
                .header("month", month)
                .header("year", year)
                .header("User-Agent", "NOI/A22TrafficForecastConnector")
                .retrieve()
                .bodyToMono(ForecastDto.class)
                .block();
    }

}
