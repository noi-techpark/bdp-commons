// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.bdp.commons.dc.meteorology.bz.forecast;

import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.format.datetime.standard.InstantFormatter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.config.DataConfig;
import com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.config.DataTypes;
import com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.config.ModelDataTypes;
import com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.config.ProvenanceConfig;
import com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.config.StationConfig;
import com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.dto.ForecastDto;
import com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.dto.ForecastDto.ForecastDouble;
import com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.dto.ForecastDto.ForecastDoubleSet;
import com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.dto.ForecastDto.ForecastString;
import com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.dto.ForecastDto.ForecastStringSet;
import com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.dto.ForecastDto.Municipality;
import com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.dto.LocationDto;
import com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.services.S3Service;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;

import com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.services.MunicipalityLocationMap;
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

    @Autowired
    private MunicipalityLocationMap municipalityLocationMap;

    // @PostConstruct
    private void postCoinstruct() {
        odhClient.syncDataTypes(stationC.modelStationType,
                Arrays.stream(ModelDataTypes.values())
                        .filter(d -> d.syncToOdh)
                        .map(ModelDataTypes::toDataTypeDto)
                        .toList());
    }

    @Scheduled(cron = "${scheduler.job}")
    public void collectForecastData()
            throws InterruptedException, ParseException, IOException {
        LOG.info("Cron job started");

        List<DataTypeDto> dataTypes = new ArrayList<>();

        ForecastDto dto = s3.getForecastDto();

        ////////////////////
        // Model
        ////////////////////
        StationDto modelStation = new StationDto(dto.info.model, dto.info.model, BZ_LAT, BZ_LON);
        modelStation.setOrigin(provC.origin);
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
        modelMeasurements.addRecord(dto.info.model, ModelDataTypes.airTemperatureMin.key, absTempMin);
        modelMeasurements.addRecord(dto.info.model, ModelDataTypes.precipitationMax.key, absPrecMax);
        modelMeasurements.addRecord(dto.info.model, ModelDataTypes.precipitationMin.key, absPrecMin);

        ////////////////////
        // Forecast Data
        ////////////////////
        DataMapDto<RecordDtoImpl> measurements3h = new DataMapDto<>();
        DataMapDto<RecordDtoImpl> measurements24h = new DataMapDto<>();

        StationList stationList = new StationList();

        for (Municipality municipality : dto.municipalities) {
            LocationDto location = municipalityLocationMap.get(municipality.nameDe);

            if (location == null) {
                LOG.error("Location not found: {}. Setting to default BZ location.", municipality.nameDe);
                location = new LocationDto(BZ_LAT, BZ_LON);
            }

            StationDto station = new StationDto(municipality.code, municipality.nameDe + "_" + municipality.nameIt,
                    location.lat, location.lon);
            station.setOrigin(provC.origin);
            // set model as parent station
            station.setParentStation(modelStation.getId());

            Map<String, Object> metadata = new HashMap<>();
            // TODO should be a measurement?
            metadata.put("nameEn", municipality.nameEn);
            metadata.put("nameRm", municipality.nameRm);
            station.setMetaData(metadata);
            stationList.add(station);

            // temperature min 24 hours
            addDoubleDataType(dataTypes, DataTypes.airTemperatureMin, municipality.tempMin24);
            for (ForecastDouble data : municipality.tempMin24.data) {
                addDoubleRecord(measurements24h, municipality.code, data, DataTypes.airTemperatureMin.key,
                        dataC.period24h);
            }
            // temperature max 24 hours
            addDoubleDataType(dataTypes, DataTypes.airTemperatureMax, municipality.tempMax24);
            for (ForecastDouble data : municipality.tempMax24.data) {
                addDoubleRecord(measurements24h, municipality.code, data, DataTypes.airTemperatureMax.key,
                        dataC.period24h);
            }
            // temperature every 3 hours
            addDoubleDataType(dataTypes, DataTypes.airTemperature, municipality.temp3);
            for (ForecastDouble data : municipality.temp3.data) {
                addDoubleRecord(measurements3h, municipality.code, data, DataTypes.airTemperature.key, dataC.period3h);
            }
            // sunshine duration 24 hours
            addDoubleDataType(dataTypes, DataTypes.sunshineDuration, municipality.ssd24);
            for (ForecastDouble data : municipality.ssd24.data) {
                addDoubleRecord(measurements24h, municipality.code, data, DataTypes.sunshineDuration.key, dataC.period24h);
            }
            // precipitation probability 3 hours
            addDoubleDataType(dataTypes, DataTypes.precipitationProbability, municipality.precProb3);
            for (ForecastDouble data : municipality.precProb3.data) {
                addDoubleRecord(measurements3h, municipality.code, data, DataTypes.precipitationProbability.key,
                        dataC.period3h);
            }
            // probably precipitation 24 hours
            addDoubleDataType(dataTypes, DataTypes.precipitationProbability, municipality.precProb24);
            for (ForecastDouble data : municipality.precProb24.data) {
                addDoubleRecord(measurements24h, municipality.code, data, DataTypes.precipitationProbability.key,
                        dataC.period24h);
            }
            // probably precipitation sum 3 hours
            addDoubleDataType(dataTypes, DataTypes.precipitationSum, municipality.precSum3);
            for (ForecastDouble data : municipality.precSum3.data) {
                addDoubleRecord(measurements3h, municipality.code, data, DataTypes.precipitationSum.key, dataC.period3h);
            }
            // probably precipitation sum 24 hours
            addDoubleDataType(dataTypes, DataTypes.precipitationSum, municipality.precSum24);
            for (ForecastDouble data : municipality.precSum24.data) {
                addDoubleRecord(measurements24h, municipality.code, data, DataTypes.precipitationSum.key, dataC.period24h);
            }
            // wind direction 3 hours
            addDoubleDataType(dataTypes, DataTypes.windDirection, municipality.windDir3);
            for (ForecastDouble data : municipality.windDir3.data) {
                addDoubleRecord(measurements3h, municipality.code, data, DataTypes.windDirection.key, dataC.period3h);
            }
            // wind speed 3 hours
            addDoubleDataType(dataTypes, DataTypes.windSpeed, municipality.windSpd3);
            for (ForecastDouble data : municipality.windSpd3.data) {
                addDoubleRecord(measurements3h, municipality.code, data, DataTypes.windSpeed.key, dataC.period3h);
            }
            // weather status symbols 3 hours
            addStringDataType(dataTypes, DataTypes.qualitativeForecast, municipality.symbols3);
            for (ForecastString data : municipality.symbols3.data) {
                addStringRecord(measurements3h, municipality.code, data, DataTypes.qualitativeForecast.key,
                        dataC.period3h);
            }
            // weather status symbols 24 hours
            addStringDataType(dataTypes, DataTypes.qualitativeForecast, municipality.symbols24);
            for (ForecastString data : municipality.symbols24.data) {
                addStringRecord(measurements24h, municipality.code, data, DataTypes.qualitativeForecast.key,
                        dataC.period24h);
            }
        }

        // sync datatypes
        odhClient.syncDataTypes(stationC.dataStationType, dataTypes);

        // First, sync model stations and push data
        odhClient.syncStations(stationC.modelStationType, new StationList(Arrays.asList(modelStation)));
        odhClient.pushData(stationC.modelStationType, modelMeasurements);
        // Then sync data stations and push data
        odhClient.syncStations(stationC.dataStationType, stationList);

        // split up for different periods, because writer currently doesn't support
        // records with same data types but different period
        odhClient.pushData(stationC.dataStationType, measurements3h);
        odhClient.pushData(stationC.dataStationType, measurements24h);

    }

    private void addDoubleDataType(List<DataTypeDto> dataTypes, DataTypes dataType, ForecastDoubleSet data) {
        DataTypeDto dataTypeDto = dataType.toDataTypeDto();
        Map<String, Object> typeMetadata = new HashMap<>();
        typeMetadata.put("nameEn", data.nameEn);
        typeMetadata.put("nameDe", data.nameDe);
        typeMetadata.put("nameIt", data.nameIt);
        dataTypeDto.setMetaData(typeMetadata);

        if (!dataTypes.contains(dataTypeDto))
            dataTypes.add(dataTypeDto);
    }

    private void addStringDataType(List<DataTypeDto> dataTypes, DataTypes dataType, ForecastStringSet data) {
        DataTypeDto dataTypeDto = dataType.toDataTypeDto();
        Map<String, Object> typeMetadata = new HashMap<>();
        typeMetadata.put("nameEn", data.nameEn);
        typeMetadata.put("nameDe", data.nameDe);
        typeMetadata.put("nameIt", data.nameIt);
        dataTypeDto.setMetaData(typeMetadata);

        if (!dataTypes.contains(dataTypeDto))
            dataTypes.add(dataTypeDto);
    }

    private void addDoubleRecord(DataMapDto<RecordDtoImpl> measurements, String stationCode, ForecastDouble data,
            String dataType, int period) throws ParseException {
        Long timestamp = new InstantFormatter().parse(data.date, Locale.ITALY).toEpochMilli();
        SimpleRecordDto recordDto = new SimpleRecordDto(timestamp, data.value, period);
        measurements.addRecord(stationCode, dataType, recordDto);
    }

    private void addStringRecord(DataMapDto<RecordDtoImpl> measurements, String stationCode, ForecastString data,
            String dataType, int period) throws ParseException {
        Long timestamp = new InstantFormatter().parse(data.date, Locale.ITALY).toEpochMilli();
        SimpleRecordDto recordDto = new SimpleRecordDto(timestamp, mapQuantitativeValues(data.value), period);
        measurements.addRecord(stationCode, dataType, recordDto);
    }

    private String mapQuantitativeValues(String value) {
        switch (value) {
            case "a_n":
            case "a_d":
                return "sunny";
            case "b_n":
            case "b_d":
                return "partly cloudy";
            case "c_n":
            case "c_d":
                return "cloudy";
            case "d_n":
            case "d_d":
                return "very cloudy";
            case "e_n":
            case "e_d":
                return "overcast";
            case "f_n":
            case "f_d":
                return "cloudy with moderate rain";
            case "g_n":
            case "g_d":
                return "cloudy with intense rain";
            case "h_n":
            case "h_d":
                return "overcast with moderate rain";
            case "i_n":
            case "i_d":
                return "overcast with intense rain";
            case "j_n":
            case "j_d":
                return "overcast with light rain";
            case "k_n":
            case "k_d":
                return "translucent cloudy";
            case "l_n":
            case "l_d":
                return "cloudy with light snow";
            case "m_n":
            case "m_d":
                return "cloudy with heavy snow";
            case "n_n":
            case "n_d":
                return "overcast with light snow";
            case "o_n":
            case "o_d":
                return "overcast with moderate snow";
            case "p_n":
            case "p_d":
                return "overcast with intense snow";
            case "q_n":
            case "q_d":
                return "cloudy with rain and snow";
            case "r_n":
            case "r_d":
                return "overcast with rain and snow";
            case "s_n":
            case "s_d":
                return "low cloudiness";
            case "t_n":
            case "t_d":
                return "fog";
            case "u_n":
            case "u_d":
                return "cloudy, thunderstorms with moderate showers";
            case "v_n":
            case "v_d":
                return "cloudy, thunderstorms with intense showers";
            case "w_n":
            case "w_d":
                return "cloudy, thunderstorms with moderate snowy and rainy showers";
            case "x_n":
            case "x_d":
                return "cloudy, thunderstorms with intense snowy and rainy showers";
            case "y_n":
            case "y_d":
                return "cloudy, thunderstorms with moderate snowy showers";
            default:
                LOG.error("No mapping configured for value: {}", value);
        }
        return "";
    }

}
