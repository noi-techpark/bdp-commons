// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.traffic.a22.forecast.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.ToString;

@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize
public class ForecastDto {

    @JsonProperty("d")
    public ForecastData data;

    @ToString
    public static class ForecastData {
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
        public List<TrafficDataLine> trafficDataLines;

                // if every day of whole month has value = 0; forecast is not valid
        public boolean isValid() {
            for (TrafficDataLine trafficDataLine : trafficDataLines)
                if (trafficDataLine.isValid())
                    return true;
            return false;
        }
    }

    @ToString
    public static class TrafficDataLine {
        @JsonProperty("PrevisioniTraffico")
        public List<TrafficData> data;

        // if every day of whole month has value = 0; forecast is not valid
        public boolean isValid() {
            for (TrafficData trafficData : data)
                if (trafficData.isValid())
                    return true;
            return false;
        }
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
        @JsonIgnoreProperties("Tipo")
        public Map<String, TrafficValues> north;

        @JsonProperty("Sud")
        @JsonIgnoreProperties("Tipo")
        public Map<String, TrafficValues> south;
        
        public boolean isValid() {
            for (TrafficValues values : south.values())
                if ((values.value0to6.type + values.value6to12.type + values.value12to18.type
                        + values.value18to24.type) == 0)
                    return false;
            return true;
        }
    }

    @ToString
    public static class TrafficValues {
        @JsonProperty("Max")
        public int max;

        @JsonProperty("_0_6")
        public TrafficValue value0to6;

        @JsonProperty("_6_12")
        public TrafficValue value6to12;

        @JsonProperty("_12_18")
        public TrafficValue value12to18;

        @JsonProperty("_18_24")
        public TrafficValue value18to24;

    }

    @ToString
    public static class TrafficValue {
        @JsonProperty("Tipo")
        public int type;
    }
}