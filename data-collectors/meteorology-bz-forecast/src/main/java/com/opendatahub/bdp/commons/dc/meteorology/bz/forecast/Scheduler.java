// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.bdp.commons.dc.meteorology.bz.forecast;

import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.config.DataConfig;
import com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.config.DataTypes;
import com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.config.ProvenanceConfig;
import com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.config.StationConfig;
import com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.dto.ForecastDto;
import com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.services.S3Service;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;

import com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.services.OdhClient;

@Service
public class Scheduler {
    private static final Logger LOG = LoggerFactory.getLogger(Scheduler.class);

    // hard coded bz coordinates for main Station Dto location 46.49067, 11.33982
    private final Double BZ_LAT = 46.49067;
    private final Double BZ_LON = 11.33982;

    @Lazy
    @Autowired
    private OdhClient odhClient;

    @Lazy
    @Autowired
    private S3Service dataService;

    @Autowired
    private DataConfig dataC;

    @Autowired
    private StationConfig stationC;

    @Autowired
    private ProvenanceConfig provC;

    @Autowired
    private S3Service s3;

    @PostConstruct
    private void postConstruct() {
        odhClient.syncDataTypes(stationType,
                Arrays.stream(DataTypes.values())
                        .filter(d -> d.syncToOdh)
                        .map(DataTypes::toDataTypeDto)
                        .toList());
    }

    @Scheduled(cron = "${scheduler.job}")
    public void collectForecastData() throws InterruptedException, JsonMappingException, JsonProcessingException {
        LOG.info("Cron job started");
        ForecastDto dto = s3.getForecastDto();

        // create main station of model
        StationDto modelStation = new StationDto(dto.info.model, dto.info.model, BZ_LAT, BZ_LON);
        // and its metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("currentModelRun", dto.info.currentModelRun);
        metadata.put("nextModelRun", dto.info.nextModelRun);
        metadata.put("fileName", dto.info.fileName);
        modelStation.setMetaData(metadata);
        // and its data
        DataMapDto<RecordDtoImpl> modelMeasurements = new DataMapDto<>();
        Long timestamp = 0; // TODO get from dto.info.currentModelRun
        SimpleRecordDto record = new SimpleRecordDto(timestamp, dto.info.absTempMax, dataC.period12h);
        modelMeasurements.addRecord(dto.info.model, DataTypes.airTemperatureMax.key, record);

        odhClient.syncStations(stationC.modelStationType, new StationList(Arrays.asList(modelStation)));
        odhClient.pushData(stationC.modelStationType, modelMeasurements);

    }

}
