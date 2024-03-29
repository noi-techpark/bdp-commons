// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.dto.LocationDto;
import com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.dto.MunicipalityDto;

// taken from https://tourism.opendatahub.com/v1/Municipality?fields=Detail.de.Title,Latitude,Longitude
// some names have been changed to fit the names from the forecast data
@Service
public class MunicipalityLocationMap extends HashMap<String, LocationDto> {

    public MunicipalityLocationMap() throws IOException {
        // read from static json file
        ObjectMapper mapper = new ObjectMapper();
        InputStream inputStream = MunicipalityLocationMap.class.getResourceAsStream("/municipalities.json");
        MunicipalityDto[] municipalities = mapper.readValue(inputStream, MunicipalityDto[].class);
        // add to map
        for (MunicipalityDto municipality : municipalities) {
            put(municipality.name, new LocationDto(municipality.latitude, municipality.longitude));
        }
    }
}
