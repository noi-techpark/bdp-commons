// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.bdp.commons.dc.meteorology.bz.forecast;

import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.format.datetime.standard.InstantFormatter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.config.DataConfig;
import com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.config.DataTypes;
import com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.config.ModelDataTypes;
import com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.config.ProvenanceConfig;
import com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.config.StationConfig;
import com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.dto.ForecastDto;
import com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.dto.ForecastDto.ForecastDouble;
import com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.dto.ForecastDto.ForecastDoubleSet;
import com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.dto.ForecastDto.ForecastStringSet;
import com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.dto.ForecastDto.Municipality;
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
        odhClient.syncDataTypes(stationC.modelStationType,
                Arrays.stream(ModelDataTypes.values())
                        .filter(d -> d.syncToOdh)
                        .map(ModelDataTypes::toDataTypeDto)
                        .toList());

        odhClient.syncDataTypes(stationC.dataStationType,
                Arrays.stream(DataTypes.values())
                        .filter(d -> d.syncToOdh)
                        .map(DataTypes::toDataTypeDto)
                        .toList());
    }

    @Scheduled(cron = "${scheduler.job}")
    public void collectForecastData()
            throws InterruptedException, JsonMappingException, JsonProcessingException, ParseException {
        LOG.info("Cron job started");
        ForecastDto dto = s3.getForecastDto();

        ////////////////////
        // Model
        ////////////////////
        StationDto modelStation = new StationDto(dto.info.model, dto.info.model, BZ_LAT, BZ_LON);
        // and its metadata
        Map<String, Object> modelMetadata = new HashMap<>();
        // TODO should be a measurement?
        modelMetadata.put("currentModelRun", dto.info.currentModelRun);
        modelMetadata.put("nextModelRun", dto.info.nextModelRun);
        modelMetadata.put("fileName", dto.info.fileName);
        modelStation.setMetaData(modelMetadata);
        // and its data
        DataMapDto<RecordDtoImpl> modelMeasurements = new DataMapDto<>();

        Long modelTimestamp = new InstantFormatter().parse(dto.info.currentModelRun, Locale.ITALY).toEpochMilli();
        SimpleRecordDto absTempMax = new SimpleRecordDto(modelTimestamp, dto.info.absTempMax, dataC.period12h);
        SimpleRecordDto absTempMin = new SimpleRecordDto(modelTimestamp, dto.info.absTempMin, dataC.period12h);
        SimpleRecordDto absPrecMax = new SimpleRecordDto(modelTimestamp, dto.info.absPrecMax, dataC.period12h);
        SimpleRecordDto absPrecMin = new SimpleRecordDto(modelTimestamp, dto.info.absPrecMin, dataC.period12h);

        modelMeasurements.addRecord(dto.info.model, ModelDataTypes.airTemperatureMax.key, absTempMax);
        modelMeasurements.addRecord(dto.info.model, ModelDataTypes.airTemperatureMax.key, absTempMin);
        modelMeasurements.addRecord(dto.info.model, ModelDataTypes.airTemperatureMax.key, absPrecMax);
        modelMeasurements.addRecord(dto.info.model, ModelDataTypes.airTemperatureMax.key, absPrecMin);

        ////////////////////
        // Forecast Data
        ////////////////////
        DataMapDto<RecordDtoImpl> measurements = new DataMapDto<>();
        StationList stationList = new StationList();

        for (Municipality municipality : dto.municipalities) {
            // TODO replace BZ_LAT and BZ_LON with correct position from tousrism API
            StationDto station = new StationDto(municipality.code, municipality.nameDe + "_" + municipality.nameIt,
                    BZ_LAT, BZ_LON);
            Map<String, Object> metadata = new HashMap<>();
            // TODO should be a measurement?
            metadata.put("nameEn", municipality.nameEn);
            metadata.put("nameRm", municipality.nameRm);
            modelStation.setMetaData(metadata);
            stationList.add(station);

            // temperature min 24 hours
            for (ForecastDouble data : municipality.tempMin24.data) {
                addDoubleRecord(measurements, municipality.code, data, DataTypes.airTemperatureMax.key,
                        dataC.period24h);
            }
            // temperature max 24 hours
            for (ForecastDouble data : municipality.tempMax24.data) {
                addDoubleRecord(measurements, municipality.code, data, DataTypes.airTemperatureMax.key,
                        dataC.period24h);
            }
            // temperature every 3 hours
            for (ForecastDouble data : municipality.temp3.data) {
                addDoubleRecord(measurements, municipality.code, data, DataTypes.airTemperature.key, dataC.period3h);
            }
            // sunshine duration 24 hours
            for (ForecastDouble data : municipality.ssd24.data) {
                addDoubleRecord(measurements, municipality.code, data, DataTypes.sunshineDuration.key, dataC.period24h);
            }
            // precipitation probability 3 hours
            for (ForecastDouble data : municipality.precProb3.data) {
                addDoubleRecord(measurements, municipality.code, data, DataTypes.precipitationProbability.key,
                        dataC.period3h);
            }
            // probably precipitation 24 hours
            for (ForecastDouble data : municipality.precProb24.data) {
                addDoubleRecord(measurements, municipality.code, data, DataTypes.precipitationProbability.key,
                        dataC.period24h);
            }
            // probably precipitation sum 3 hours
            for (ForecastDouble data : municipality.precSum3.data) {
                addDoubleRecord(measurements, municipality.code, data, DataTypes.precipitationSum.key, dataC.period3h);
            }
            // probably precipitation sum 24 hours
            for (ForecastDouble data : municipality.precSum24.data) {
                addDoubleRecord(measurements, municipality.code, data, DataTypes.precipitationSum.key, dataC.period24h);
            }
            // wind direction 3 hours
            for (ForecastDouble data : municipality.windDir3.data) {
                addDoubleRecord(measurements, municipality.code, data, DataTypes.windDirection.key, dataC.period3h);
            }
            // wind speed 3 hours
            for (ForecastDouble data : municipality.windSpd3.data) {
                addDoubleRecord(measurements, municipality.code, data, DataTypes.windSpeed.key, dataC.period3h);
            }
        }

        // First, sync model stations and push data
        odhClient.syncStations(stationC.modelStationType, new StationList(Arrays.asList(modelStation)));
        odhClient.pushData(stationC.modelStationType, modelMeasurements);
        // Then sync data stations and push data
        odhClient.syncStations(stationC.dataStationType, stationList);
        odhClient.pushData(stationC.dataStationType, measurements);
    }

    private void addDoubleRecord(DataMapDto<RecordDtoImpl> measurements, String stationCode, ForecastDouble data,
            String dataType, int period) throws ParseException {
        Long timestamp = new InstantFormatter().parse(data.date, Locale.ITALY).toEpochMilli();
        SimpleRecordDto recordDto = new SimpleRecordDto(timestamp, data.value, period);
        measurements.addRecord(stationCode, dataType, recordDto);
    }

}
