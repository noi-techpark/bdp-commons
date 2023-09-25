// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.traffic.a22.forecast.mapping;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

import com.opendatahub.traffic.a22.forecast.dto.ForecastDto;
import com.opendatahub.traffic.a22.forecast.dto.ForecastDto.TrafficData;
import com.opendatahub.traffic.a22.forecast.dto.ForecastDto.TrafficDataLine;
import com.opendatahub.traffic.a22.forecast.dto.ForecastDto.TrafficValues;

// Maps the toll booth name to its direction and its values by date
// example 'Bolzano Sud' -> '09-2023' -> 0,1,0,3
public class ForecastMap extends HashMap<String, Map<String, TrafficValues>> {

    public void add(ForecastDto dto, YearMonth date) {
        for (TrafficDataLine line : dto.data.trafficDataLines) {
            for (TrafficData data : line.data) {
                putValues(date, data.south, "Sud");
                putValues(date, data.north, "Nord");
            }
        }
    }

    private void putValues(YearMonth date, Map<String, TrafficValues> data, String direction) {
        data.entrySet().forEach((entry) -> {
            String key = entry.getKey() + " " + direction;
            Map<String, TrafficValues> map = get(key);
            if (map == null)
                map = new HashMap<>();
            map.put(date.toString(), data.get(entry.getKey()));
            put(key, map);
        });
    }
}
