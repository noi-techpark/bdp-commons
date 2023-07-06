// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.bdp.commons.dc.bikeboxes.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.opendatahub.bdp.commons.dc.bikeboxes.dto.BikeStation;

@Service
public class BikeBoxesService {
    private final static Logger LOG = LoggerFactory.getLogger(BikeBoxesService.class);
    // en, de, it, lld
    private final static String LANGUAGE = "en";
    private final static String ENDPOINT_STATIONS = "/resources/stations";
    private final static String ENDPOINT_STATION = "/resources/station";

    @Autowired
    @Qualifier("bikeParkingWebClient") // avoid overlap with webClient in bdp-core
    private WebClient client;

    public List<BikeStation> getBikeStations() {
        List<BikeStation> bikeStationsWithPlace = new ArrayList<>();

        List<BikeStation> bikeStations = fetchBikeStations();
        for (BikeStation bikeStation : bikeStations) {
            BikeStation fetchBikeStation = fetchBikeStationWithPlace(bikeStation.stationID);
            bikeStationsWithPlace.add(fetchBikeStation);

        }

        return bikeStationsWithPlace;
    }

    private List<BikeStation> fetchBikeStations() {
        return client.get()
                .uri(u -> u
                        .path(ENDPOINT_STATIONS)
                        .queryParam("languageId", LANGUAGE)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(BikeStation.class)
                .collectList()
                .block();
    }

    private BikeStation fetchBikeStationWithPlace(String stationId) {
        return client.get()
                .uri(u -> u
                        .path(ENDPOINT_STATION)
                        .queryParam("languageId", LANGUAGE)
                        .queryParam("stationId", stationId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .attribute("idStation", stationId)
                .retrieve()
                .bodyToMono(BikeStation.class)
                .block();
    }
}
