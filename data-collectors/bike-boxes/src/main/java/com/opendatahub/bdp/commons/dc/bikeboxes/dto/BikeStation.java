package com.opendatahub.bdp.commons.dc.bikeboxes.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.ToString;

/**
 * dto for /resources/stations and /resources/station
 */
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize
public class BikeStation {
    public String idStation;
    public String name;
    public String address;
    public double latitude;
    public double longitude;
    public int type;
    public String urlGuide;
    public int state;
    public int countMuscularBikesAvailable;
    public int countAssistedBikesAvailable;
    public int countFreePlacesAvailable;
    public int totalPlaces;
    public int maxDistanceRent;
    public Place[] stationPlaces; // only present in case of /station call
 
    @ToString
    public static class Place {
        public int position;
        public int state;
        public String bikeTag;
        public String bikeNum;
        public boolean isAssisted;
        public int type;
    }
}