package com.opendatahub.traffic.a22.forecast.mapping;

import java.util.HashMap;
import java.util.Map;

import com.opendatahub.traffic.a22.forecast.dto.TollBoothDto;
import com.opendatahub.traffic.a22.forecast.dto.TollBoothDto.TollBoothData;

public class TollBothMap {

    Map<String, String> map;

    public TollBothMap(TollBoothDto dto) {
        map = new HashMap<>();
        for (TollBoothData data : dto.data) {
            map.put(data.nameIT, data.km);
        }
    }
}
