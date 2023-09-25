// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.traffic.a22.forecast.mapping;

import java.util.HashMap;

import com.opendatahub.traffic.a22.forecast.dto.TollBothCoordinatesDto;
import com.opendatahub.traffic.a22.forecast.dto.TollBothCoordinatesDto.TollBothCoordinates;

// Maps the km string to the corresponding coordinate
// example '11' -> 42.23287,11.232323
public class TollBothCoordinatesMap extends HashMap<String, Coordinate> {

    public TollBothCoordinatesMap(TollBothCoordinatesDto dto) {
        for (TollBothCoordinates data : dto.data) {
            put(data.km, new Coordinate(data.longitude, data.latitude));
        }
    }


}
