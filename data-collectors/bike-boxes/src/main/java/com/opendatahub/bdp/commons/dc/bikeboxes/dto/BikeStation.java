// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.bdp.commons.dc.bikeboxes.dto;

import java.util.HashMap;
import java.util.Map;

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
    public int stationID;
    // main name used for station name
    public String locationName;
    // other languages saved in metadata
    public Map<String, String> translatedNames = new HashMap<>();
    public int locationID;
    public String name;
    public String address;
    // other languages saved in metadata
    public Map<String, String> addresses = new HashMap<>();
    public double latitude;
    public double longitude;
    public int type;
    public int state;
    public int countFreePlacesAvailable_MuscularBikes;
    public int countFreePlacesAvailable_AssistedBikes;
    public int countFreePlacesAvailable;
    public int totalPlaces;
    
    // only present in case of /station call
    public Place[] places; 

    @ToString
    public static class Place {
        public int position;
        public int state;
        public int level;
        public int type;
    }
}