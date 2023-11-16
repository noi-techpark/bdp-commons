// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize
public class MunicipalityDto {

    @JsonProperty("Id")
    public String id;

    @JsonProperty("Detail.de.Title")
    public String name;

    @JsonProperty("Latitude")
    public double latitude;

    @JsonProperty("Longitude")
    public double longitude;

}
