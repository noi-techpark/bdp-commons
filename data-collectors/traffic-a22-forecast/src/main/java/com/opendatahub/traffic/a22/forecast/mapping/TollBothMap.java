// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.traffic.a22.forecast.mapping;

import java.util.HashMap;

import com.opendatahub.traffic.a22.forecast.dto.TollBoothDto;
import com.opendatahub.traffic.a22.forecast.dto.TollBoothDto.TollBoothData;

// Maps the toll booth name to the corresponding km
// example 'Bolzano Sud' -> '123'
public class TollBothMap extends HashMap<String, String> {

    public TollBothMap(TollBoothDto dto) {
        for (TollBoothData data : dto.data) {
            put(data.nameIT, data.km);
        }
    }

    public String findValue(String tollBothName) {
        // find exact match
        for (Entry<String, String> entry : entrySet()) {
            if (entry.getKey().equals(tollBothName))
                return entry.getValue();
        }

        // find by city name by removing direction 'Sud' or 'Nord'
        String cityName = tollBothName.split(" ")[0];
        for (Entry<String, String> entry : entrySet()) {
            if (entry.getKey().contains(cityName))
                return entry.getValue();
        }

        return null;
    }
}
