// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.bdp.commons.dc.bikeboxes.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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

    // language used for station name
    private final static String DEFAULT_LANGUAGE = "it";
    // languages saved in metadata
    private final static List<String> METADATA_LANGUAGES = new ArrayList<String>(
            Arrays.asList("it", "en", "de", "lld"));
    private final static String ENDPOINT_STATIONS = "/resources/stations";
    private final static String ENDPOINT_STATION = "/resources/station";

    @Autowired
    @Qualifier("bikeParkingWebClient") // avoid overlap with webClient in bdp-core
    private WebClient client;

    public List<BikeStation> getBikeStations() {
        LOG.info("Fetching data...");
        int count = 0;
        List<BikeStation> bikeStationsWithPlace = new ArrayList<>();

        List<BikeStation> bikeStations = fetchBikeStations();
        for (BikeStation bikeStation : bikeStations) {
            BikeStation fetchBikeStation = fetchBikeStationWithPlace(bikeStation.stationID);
            bikeStationsWithPlace.add(fetchBikeStation);
            count++;
        }
        LOG.info("Fetching data done. {} stations found", count);
        return bikeStationsWithPlace;
    }

    private List<BikeStation> fetchBikeStations() {
        // get stations with default language
        return client.get()
                .uri(u -> u
                        .path(ENDPOINT_STATIONS)
                        .queryParam("languageId", DEFAULT_LANGUAGE)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(BikeStation.class)
                .collectList()
                .block();
    }

    private BikeStation fetchBikeStationWithPlace(String stationId) {

        // get stations with default language
        BikeStation station = client.get()
                .uri(u -> u
                        .path(ENDPOINT_STATION)
                        .queryParam("languageId", DEFAULT_LANGUAGE)
                        .queryParam("stationId", stationId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .attribute("idStation", stationId)
                .retrieve()
                .bodyToMono(BikeStation.class)
                .block();

        // get stations with additional metadata language
        for (String language : METADATA_LANGUAGES) {
            BikeStation languageStation = client.get()
                    .uri(u -> u
                            .path(ENDPOINT_STATION)
                            .queryParam("languageId", DEFAULT_LANGUAGE)
                            .queryParam("stationId", stationId)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .attribute("idStation", stationId)
                    .retrieve()
                    .bodyToMono(BikeStation.class)
                    .block();

            // assign metadata languages
            // are sorted, so direct assignment works
            if (station.locationNames == null)
                station.locationNames = new HashMap<String, String>();
            station.locationNames.put(language, languageStation.locationName);

            if (station.addresses == null)
                station.addresses = new HashMap<String, String>();
            station.addresses.put(language, languageStation.address);

            // add default language to metadata languages too
            station.locationNames.put(DEFAULT_LANGUAGE, station.locationName);
            station.addresses.put(DEFAULT_LANGUAGE, station.address);

        }
        return station;
    }
}
