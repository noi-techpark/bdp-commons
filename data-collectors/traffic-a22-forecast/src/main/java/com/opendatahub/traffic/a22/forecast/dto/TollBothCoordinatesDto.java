// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.traffic.a22.forecast.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.ToString;

@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize
public class TollBothCoordinatesDto {

    @JsonProperty("d")
    public List<TollBothCoordinates> data;

    @ToString
    public static class TollBothCoordinates {

        @JsonProperty("__type")
        public String type;

        @JsonProperty("KM")
        public String km;

        @JsonProperty("Lat")
        public double latitude;

        @JsonProperty("Lng")
        public double longitude;
    }

}