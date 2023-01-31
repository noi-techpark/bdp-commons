package com.opendatahub.bdp.commons.dc.bikeboxes.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.ToString;

/**
 * dto for /resources/services
 */
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize
public class BikeService {
    public String idPortal;
    public String name;
    public City[] cities;

    @ToString
    public static class City {
        public String idCity;
        public String name;
        public String serviceName;
        public String urlLogo;
        public String urlGuide;
        public double latitude;
        public double longitude;
        public boolean sharingEnabled;
        public boolean parkingEnabled;
    }
}