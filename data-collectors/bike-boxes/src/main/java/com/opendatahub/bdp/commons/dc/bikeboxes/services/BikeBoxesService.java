package com.opendatahub.bdp.commons.dc.bikeboxes.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.opendatahub.bdp.commons.dc.bikeboxes.dto.BikeService;
import com.opendatahub.bdp.commons.dc.bikeboxes.dto.BikeStation;

@Service
public class BikeBoxesService implements IBikeBoxesService {
    private final static Logger LOG = LoggerFactory.getLogger(BikeBoxesService.class);
    private final static String ENDPOINT_STATIONS = "/resources/stations";
    private final static String ENDPOINT_STATION = "/resources/station";
    private final static String ENDPOINT_SERVICES = "/resources/services";

    @Autowired
    @Qualifier("bikeParkingWebClient") // avoid overlap with webClient in bdp-core
    private WebClient client;

    @Override
    public List<BikeService> getBikeServices() {
        return client.get()
                .uri(ENDPOINT_SERVICES)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(BikeService.class)
                .collectList()
                .block();
    }

    private List<BikeStation> getBikeStationsByType(String cityId, String serviceType){
        return client.get()
                .uri(u -> u
                    .path(ENDPOINT_STATIONS)
                    .queryParam("idCity", cityId)
                    .queryParam("serviceType", serviceType)
                    .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(BikeStation.class)
                .collectList()
                .block();
    }
    @Override
    public List<BikeStation> getBikeStations(String cityId) {
        List<BikeStation> ret = getBikeStationsByType(cityId, "1");
        ret.addAll(getBikeStationsByType(cityId, "2"));
        return ret;
    }

    @Override
    public BikeStation getBikeStation(String stationId) {
        return client.get()
                .uri(u -> u
                    .path(ENDPOINT_STATION)
                    .queryParam("idStation", stationId)
                    .build())
                .accept(MediaType.APPLICATION_JSON)
                .attribute("idStation", stationId)
                .retrieve()
                .bodyToMono(BikeStation.class)
                .block();
    }
}
