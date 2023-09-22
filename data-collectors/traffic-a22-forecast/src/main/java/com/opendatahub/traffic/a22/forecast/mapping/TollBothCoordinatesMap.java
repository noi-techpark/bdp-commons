// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.traffic.a22.forecast.mapping;

import java.util.HashMap;
import java.util.Map;

import com.opendatahub.traffic.a22.forecast.dto.TollBothCoordinatesDto;
import com.opendatahub.traffic.a22.forecast.dto.TollBothCoordinatesDto.TollBothCoordinates;

public class TollBothCoordinatesMap {

    Map<String, Coordinate> map;

    public TollBothCoordinatesMap(TollBothCoordinatesDto dto) {
        map = new HashMap<>();
        for (TollBothCoordinates data : dto.data) {
            map.put(data.km, new Coordinate(data.longitude, data.latitude));
        }
    }

    public class Coordinate {
        public double longitude;
        public double latitude;

        public Coordinate(double longitude, double latitude) {
            this.longitude = longitude;
            this.latitude = latitude;
        }
    }
}
