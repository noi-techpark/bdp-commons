// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.traffic.a22.forecast.mapping;

import java.util.HashMap;
import java.util.Map;

import com.opendatahub.traffic.a22.forecast.dto.ForecastDto;
import com.opendatahub.traffic.a22.forecast.dto.ForecastDto.TrafficData;
import com.opendatahub.traffic.a22.forecast.dto.ForecastDto.TrafficDataLine;
import com.opendatahub.traffic.a22.forecast.dto.ForecastDto.Values;

public class ForecastMap {

    Map<String, Map<String, Values>> map;

    public ForecastMap(ForecastDto dto) {
        map = new HashMap<>();
        for (TrafficDataLine line : dto.data.trafficDataLines) {
            for (TrafficData data : line.data) {
                for (String key : data.south.keySet()) {
                    Map<String, Values> valuesByDate = new HashMap<>();
                    valuesByDate.put(data.date, data.south.get(key));
                    map.put(key + " Sud", valuesByDate);
                }

                for (String key : data.north.keySet()) {
                    Map<String, Values> valuesByDate = new HashMap<>();
                    valuesByDate.put(data.date, data.north.get(key));
                    map.put(key + " Nord", valuesByDate);
                }
            }
        }
    }
}
