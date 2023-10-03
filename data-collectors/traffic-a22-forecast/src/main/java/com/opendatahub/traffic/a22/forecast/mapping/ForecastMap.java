// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.traffic.a22.forecast.mapping;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opendatahub.traffic.a22.forecast.JobScheduler;
import com.opendatahub.traffic.a22.forecast.dto.ForecastDto;
import com.opendatahub.traffic.a22.forecast.dto.ForecastDto.TrafficData;
import com.opendatahub.traffic.a22.forecast.dto.ForecastDto.TrafficDataLine;
import com.opendatahub.traffic.a22.forecast.dto.ForecastDto.TrafficValues;

// Maps the toll booth name to its direction and its values by date
// example 'Bolzano Sud' -> '09-2023' -> 0,1,0,3
public class ForecastMap extends HashMap<String, Map<Long, TrafficValues>> {

    private static final Logger LOG = LoggerFactory.getLogger(ForecastMap.class);

    public void add(ForecastDto dto) {
        for (TrafficDataLine line : dto.data.trafficDataLines) {
            if (line.isValid())
                for (TrafficData data : line.data) {
                    putValues(data.south, data.date, "Sud");
                    putValues(data.north, data.date, "Nord");
                }
            else {
                if (line.data.isEmpty())
                    LOG.info("Data is empty. Skipping...");
                else
                    LOG.info("Not valid data for date {}/{}. Skipping...", line.data.get(0).month,
                            line.data.get(0).year);

            }
        }
    }

    private void putValues(Map<String, TrafficValues> data, String date, String direction) {
        data.entrySet().forEach(entry -> {
            String key = entry.getKey() + " " + direction;
            Map<Long, TrafficValues> map = get(key);
            if (map == null)
                map = new HashMap<>();
            Long timestamp = a22DateToMillis(date);
            map.put(timestamp, entry.getValue());
            put(key, map);
        });
    }

    // extracts value from a22 date string "\/Date(1696197600000)\/"
    private Long a22DateToMillis(String date) {
        return Long.valueOf(date.substring(6, 19));
    }
}
