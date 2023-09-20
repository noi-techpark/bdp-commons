// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.traffic.a22.forecast.services;

import java.time.YearMonth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.opendatahub.traffic.a22.forecast.dto.ForecastDto;

@Service
public class A22Service {
    private final Logger LOG = LoggerFactory.getLogger(A22Service.class);
    private final String ENDPOINT_FORECAST = "/GetPrevisioniTrafficoComplete";

    @Autowired
    @Qualifier("a22Client") // avoid overlap with webClient in bdp-core
    private WebClient client;

    public ForecastDto getForecasts(YearMonth date) {
        return client.post()
                .uri(u -> u.path(ENDPOINT_FORECAST).build())
                .accept(MediaType.ALL)
                .contentType(MediaType.APPLICATION_JSON)
                .header("month", String.valueOf(date.getMonthValue()))
                .header("year", String.valueOf(date.getYear()))
                .header("User-Agent", "NOI/A22TrafficForecastConnector")
                .retrieve()
                .bodyToMono(ForecastDto.class)
                .block();
    }

}
