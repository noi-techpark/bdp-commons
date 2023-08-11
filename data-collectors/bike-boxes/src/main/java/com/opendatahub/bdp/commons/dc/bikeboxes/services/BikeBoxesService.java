// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.bdp.commons.dc.bikeboxes.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.opendatahub.bdp.commons.dc.bikeboxes.dto.BikeLocation;
import com.opendatahub.bdp.commons.dc.bikeboxes.dto.BikeStation;

@Service
public class BikeBoxesService {
    private final static Logger LOG = LoggerFactory.getLogger(BikeBoxesService.class);

    // language used for station name
    private final static String DEFAULT_LANGUAGE = "it";
    // languages saved in metadata
    private final static List<String> METADATA_LANGUAGES = new ArrayList<String>(
            Arrays.asList("it", "en", "de", "lld"));
    private final static String ENDPOINT_LOCATIONS = "/resources/locations";
    private final static String ENDPOINT_STATION = "/resources/station";

    @Autowired
    @Qualifier("bikeParkingWebClient") // avoid overlap with webClient in bdp-core
    private WebClient client;

    public List<BikeStation> getBikeStations(BikeLocation bikeLocation) {
        LOG.info("Fetching data for location id = {}, {}", bikeLocation.locationID, bikeLocation.locationName);
        int count = 0;
        List<BikeStation> bikeStationsWithPlace = new ArrayList<>();

        for (BikeLocation.LocationStation bikeLocationStation : bikeLocation.stations) {
            BikeStation fetchBikeStation = fetchBikeStationWithPlace(bikeLocationStation.stationID);
            bikeStationsWithPlace.add(fetchBikeStation);
            count++;
        }
        
        LOG.info("Fetching data done. {} stations found", count);
        return bikeStationsWithPlace;
    }

    public List<BikeLocation> getBikeLocations(){
        List<BikeLocation> locations = getBikeLocations(DEFAULT_LANGUAGE);

        // default language metadata translation
        for (BikeLocation location : locations) {
            location.translatedLocationNames.put(DEFAULT_LANGUAGE, location.locationName);
        }
    
        Map<Integer, BikeLocation> locationById = locations.stream().collect(Collectors.toMap(l -> l.locationID, l -> l));

        // add other language metadata translations
        for (String language : METADATA_LANGUAGES) {
            List<BikeLocation> translatedLocations = getBikeLocations(language);
            for (BikeLocation translatedLocation : translatedLocations) {
                BikeLocation location = locationById.get(translatedLocation.locationID);
                location.translatedLocationNames.put(language, location.locationName);
            }
        }
        return locations;
    }

    private List<BikeLocation> getBikeLocations(String language) {
        return client.get()
                .uri(u -> u
                        .path(ENDPOINT_LOCATIONS)
                        .queryParam("languageID", DEFAULT_LANGUAGE)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(BikeLocation.class)
                .collectList()
                .block();
    }

    private BikeStation fetchBikeStationWithPlace(int stationId) {
        // get stations with default language
        BikeStation station = getStation(stationId, DEFAULT_LANGUAGE);

        // add default language to metadata languages
        station.translatedNames.put(DEFAULT_LANGUAGE, station.name);
        station.addresses.put(DEFAULT_LANGUAGE, station.address);

        // get stations with additional metadata language
        for (String language : METADATA_LANGUAGES) {
            BikeStation languageStation = getStation(stationId, language);

            // assign metadata languages
            station.translatedNames.put(language, languageStation.name);
            station.addresses.put(language, languageStation.address);
        }

        return station;
    }

    private BikeStation getStation(int stationId, String language) {
        return client.get()
                .uri(u -> u
                        .path(ENDPOINT_STATION)
                        .queryParam("languageID", language)
                        .queryParam("stationID", stationId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .attribute("idStation", stationId)
                .retrieve()
                .bodyToMono(BikeStation.class)
                .block();
    }
}
