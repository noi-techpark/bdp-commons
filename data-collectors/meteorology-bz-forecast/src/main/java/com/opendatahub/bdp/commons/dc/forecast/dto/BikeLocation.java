// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.bdp.commons.dc.forecast.dto;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.ToString;

/**
 * dto for /resources/locations
 */
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize
public class BikeLocation {
    public String name;
    public int locationID;
    public LocationStation[] stations;
    // other languages saved in Metadata
    public Map<String, String> translatedLocationNames = new HashMap<>();

    @ToString
    public static class LocationStation {
        public int stationID;
        public int type;
    }
}