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
public class TollBoothDto {

    @JsonProperty("d")
    public Data data;

    @ToString
    public static class Data {

        @JsonProperty("__type")
        public String type;

        @JsonProperty("IDCasello")
        public int id;

        @JsonProperty("IDPedaggio")
        public int tollId;

        @JsonProperty("Titolo_IT")
        public String nameIT;

        @JsonProperty("Titolo_DE")
        public String nameDE;

        @JsonProperty("Titolo_EN")
        public String nameEN;

        @JsonProperty("Entrate_Totali")
        public int entranceTotal;

        @JsonProperty("Entrate_Telepass")
        public int entranceTelepass;

        @JsonProperty("Entrate_Dedicate")
        public int entranceDedicated;

        @JsonProperty("Entrate_Allargate")
        public int entranceWide;

        @JsonProperty("Uscite_Totali")
        public int exitTotal;

        @JsonProperty("Uscite_Telepass")
        public int exitTelepass;
        @JsonProperty("Uscite_Dedicate")
        public int exitDedicated;

        @JsonProperty("Uscite_Allargate")
        public int exitWide;

        @JsonProperty("KM")
        public int km;

        @JsonProperty("ItinerariSUD_IT")
        public List<String> itinerarySouthIT;

        @JsonProperty("ItinerariSUD_EN")
        public List<String> itinerarySouthEN;

        @JsonProperty("ItinerariSUD_DE")
        public List<String> itinerarySouthDE;

        @JsonProperty("ItinerariNORD_IT")
        public List<String> itineraryNorthIT;

        @JsonProperty("ItinerariNORD_EN")
        public List<String> itineraryNorthEN;

        @JsonProperty("ItinerariNORD_DE")
        public List<String> itineraryNorthDE;

        @JsonProperty("Descrizione_IT")
        public String descriptionIT;

        @JsonProperty("Descrizione_DE")
        public String descriptionDE;

        @JsonProperty("Descrizione_EN")
        public String descriptionEN;
    }

}