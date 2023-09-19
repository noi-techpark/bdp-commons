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
public class ForecastDto {

    @JsonProperty("d")
    public Data data;

    @ToString
    public static class Data {
        @JsonProperty("__type")
        public String type;

        @JsonProperty("MesePrevisioniTraffico")
        public String month;

        @JsonProperty("AnnoPrevisioniTraffico")
        public int year;

        @JsonProperty("NextMonth")
        public boolean hasNextMonth;

        @JsonProperty("PrevMonth")
        public boolean hasPrevMonth;

        @JsonProperty("RighePrevisioniTraffico")
        public List<TrafficData> trafficData;
    }

    @ToString
    public static class TrafficData {
        @JsonProperty("Titolo")
        public String titolo;

        @JsonProperty("Data")
        public String date;

        @JsonProperty("Giorno")
        public int day;

        @JsonProperty("Mese")
        public int month;

        @JsonProperty("Anno")
        public int year;

        @JsonProperty("Nord")
        public Direction north;

        @JsonProperty("Sud")
        public Direction south;
    }

    @ToString
    public static class Direction {

        @JsonProperty("Tipo")
        public int type;

        @JsonProperty("Brennero")
        public Values brennero;

        @JsonProperty("Bolzano")
        public Values bolzano;

        @JsonProperty("Verona")
        public Values verona;

    }

    @ToString
    public static class Values {
        @JsonProperty("Max")
        public int max;

        @JsonProperty("_0_6")
        public Value value0to6;

        @JsonProperty("_6_12")
        public Value value6to12;

        @JsonProperty("_12_18")
        public Value value12to18;

        @JsonProperty("_18_24")
        public Value value18to24;

    }

    @ToString
    public static class Value {
        @JsonProperty("Tipo")
        public int type;

    }
}