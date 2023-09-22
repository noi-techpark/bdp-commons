// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.traffic.a22.forecast.services;

import java.time.YearMonth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.opendatahub.traffic.a22.forecast.dto.TollBothCoordinatesDto;
import com.opendatahub.traffic.a22.forecast.dto.ForecastDto;
import com.opendatahub.traffic.a22.forecast.dto.TollBoothDto;

@Service
public class A22Service {

    private final String ENDPOINT_FORECAST = "/GetPrevisioniTrafficoComplete";
    private final String ENDPOINT_COORDINATES = "/GetCoordinate";
    private final String ENDPOINT_TOLL_BOOTH = "/GetCaselli";

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

    public TollBoothDto getTollBooths() {
        return client.post()
                .uri(u -> u.path(ENDPOINT_TOLL_BOOTH).build())
                .contentType(MediaType.APPLICATION_JSON)
                .header("User-Agent", "NOI/A22TrafficForecastConnector")
                .retrieve()
                .bodyToMono(TollBoothDto.class)
                .block();
    }

    public TollBothCoordinatesDto getCoordinates() {
        return client.post()
                .uri(u -> u.path(ENDPOINT_COORDINATES).build())
                .contentType(MediaType.APPLICATION_JSON)
                .header("User-Agent", "NOI/A22TrafficForecastConnector")
                .retrieve()
                .bodyToMono(TollBothCoordinatesDto.class)
                .block();
    }

}
