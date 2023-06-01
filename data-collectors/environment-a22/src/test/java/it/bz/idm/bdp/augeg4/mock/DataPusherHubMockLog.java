// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.augeg4.mock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.bz.idm.bdp.augeg4.fun.push.DataPusherHub;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.StationList;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

public class DataPusherHubMockLog extends DataPusherHub {


    ObjectMapper mapper = new ObjectMapper();

    public DataPusherHubMockLog(int period) {
        super(period);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Rome"));
        mapper.setDateFormat(dateFormat);

    }

    @Override
    public Object syncStations(StationList stationDtoList) {
        log(stationDtoList);
        return new Object();
    }

    @Override
    public Object syncDataTypes(List<DataTypeDto> dataTypeDtoList) {
        log(dataTypeDtoList);
        return new Object();
    }

    @Override
    public void pushData() {
        log(getRootMap());
    }

    private void log(Object objToLog) {
        System.out.println(toJson(objToLog));
    }


    private String toJson(Object objToJson) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(objToJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "toJsonFail";
    }
}
