// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.traffic.a22.forecast;

import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.opendatahub.traffic.a22.forecast.mapping.Coordinate;
import com.opendatahub.traffic.a22.forecast.mapping.ForecastMap;
import com.opendatahub.traffic.a22.forecast.mapping.TollBothCoordinatesMap;
import com.opendatahub.traffic.a22.forecast.mapping.TollBothMap;
import com.opendatahub.traffic.a22.forecast.dto.ForecastDto.TrafficValue;
import com.opendatahub.traffic.a22.forecast.services.A22Service;
import com.opendatahub.traffic.a22.forecast.services.OdhClient;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;

import com.opendatahub.traffic.a22.forecast.config.DataTypes;
import com.opendatahub.traffic.a22.forecast.config.ProvenanceConfig;;

@Service
public class JobScheduler {
    private static final Logger LOG = LoggerFactory.getLogger(JobScheduler.class);

    @Value("${historyimport.enabled}")
    private boolean historyEnabled;

    @Value("${historyimport.year}")
    private int historyYear;

    @Value("${historyimport.month}")
    private int historyMonth;

    @Value("${forecast.months}")
    private int forecastMonths;

    @Lazy
    @Autowired
    private OdhClient odhClient;

    @Lazy
    @Autowired
    private A22Service a22Service;

    @Value("${data.period}")
    public int period;

    @Value("${station.stationType}")
    public String stationType;

    @Autowired
    private ProvenanceConfig provenanceConfig;

    // @PostConstruct
    public void postConstruct() {
        // sync data types
        odhClient.syncDataTypes(stationType,
                Arrays.stream(DataTypes.values())
                        .filter(d -> d.syncToOdh)
                        .map(DataTypes::toDataTypeDto)
                        .toList());

        // historical import
        if (historyEnabled) {
            LOG.info("Historical import job started...");
            syncData(YearMonth.of(historyYear, historyMonth), YearMonth.now());
            LOG.info("Historical import job successful.");
        }
    }

    @Scheduled(cron = "${scheduler.job}")
    public void forecastImport() {
        LOG.info("Cron job stations started...");
        YearMonth currentDate = YearMonth.now();
        syncData(currentDate, currentDate.plusMonths(forecastMonths));
        LOG.info("Cron job successful.");
    }

    public void syncData(YearMonth from, YearMonth to) {
        LOG.info("Sync started from {} to {}...", from, to);

        StationList stations = new StationList();
        DataMapDto<RecordDtoImpl> measurements = new DataMapDto<>();

        ForecastMap forecastMap = new ForecastMap();

        TollBothMap tollBothMap = new TollBothMap(a22Service.getTollBooths());
        TollBothCoordinatesMap coordinateMap = new TollBothCoordinatesMap(a22Service.getCoordinates());

        // get forecast data
        for (YearMonth i = from; i.isBefore(to); i = i.plusMonths(1))
            forecastMap.add(a22Service.getForecasts(i));

        // sync with Open Data Hub
        forecastMap.entrySet().forEach(forecast -> {
            // create station
            String tollBoothName = forecast.getKey();
            String km = tollBothMap.findValue(tollBoothName);
            if (km != null) {
                Coordinate coordinate = coordinateMap.get(km);
                StationDto stationDto = new StationDto(tollBoothName, tollBoothName, coordinate.latitude,
                        coordinate.longitude);
                stationDto.setOrigin(provenanceConfig.origin);
                stations.add(stationDto);

                // create measurements
                forecast.getValue().entrySet().forEach(value -> {
                    // time windows are always 6 hours, so use middle of 3 hours
                    Long threeHours = 10800000L;
                    Long timestamp = value.getKey();

                    timestamp += threeHours;
                    SimpleRecordDto record0to6 = new SimpleRecordDto(timestamp,
                            mapTrafficValue(value.getValue().value0to6), period);
                    timestamp += threeHours;
                    SimpleRecordDto record6to12 = new SimpleRecordDto(timestamp,
                            mapTrafficValue(value.getValue().value6to12), period);
                    timestamp += threeHours;
                    SimpleRecordDto record12to18 = new SimpleRecordDto(timestamp,
                            mapTrafficValue(value.getValue().value12to18), period);
                    timestamp += threeHours;
                    SimpleRecordDto record18to24 = new SimpleRecordDto(timestamp,
                            mapTrafficValue(value.getValue().value18to24), period);

                    measurements.addRecord(tollBoothName, DataTypes.forecast.key, record0to6);
                    measurements.addRecord(tollBoothName, DataTypes.forecast.key, record6to12);
                    measurements.addRecord(tollBoothName, DataTypes.forecast.key, record12to18);
                    measurements.addRecord(tollBoothName, DataTypes.forecast.key, record18to24);
                });
            } else {
                LOG.error("Station with name {} has no valid km <-> coordinate mapping. Skipping...", tollBoothName);
            }

        });

        odhClient.syncStations(stationType, stations, 25);
        odhClient.pushData(stationType, measurements);

        LOG.info("Sync done. Imported {} months.", forecastMap.size());
    }

    private String mapTrafficValue(TrafficValue value) {
        switch (value.type) {
            case 0:
                return "regular";
            case 1:
                return "heavy";
            case 2:
                return "severe";
            case 3:
                return "critical";
            default:
                return "not defined";
        }
    }
}
