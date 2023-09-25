// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.traffic.a22.forecast;

import org.slf4j.LoggerFactory;

import java.time.YearMonth;
import java.util.ArrayList;
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
import com.opendatahub.traffic.a22.forecast.dto.ForecastDto;
import com.opendatahub.traffic.a22.forecast.dto.ForecastDto.TrafficData;
import com.opendatahub.traffic.a22.forecast.dto.ForecastDto.TrafficDataLine;
import com.opendatahub.traffic.a22.forecast.dto.ForecastDto.TrafficValue;
import com.opendatahub.traffic.a22.forecast.services.A22Service;
import com.opendatahub.traffic.a22.forecast.services.OdhClient;

import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;

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

    @PostConstruct
    public void postConstruct() {
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

        ForecastMap forecastMap = new ForecastMap();

        TollBothMap tollBothMap = new TollBothMap(a22Service.getTollBooths());
        TollBothCoordinatesMap coordinateMap = new TollBothCoordinatesMap(a22Service.getCoordinates());

        // get forecast data
        for (YearMonth i = from; i.isBefore(to); i = i.plusMonths(1))
            forecastMap.add(a22Service.getForecasts(i), i);

        // sync with Open Data Hub
        for (String toolBoothName : forecastMap.keySet()) {
            Coordinate coordinate = coordinateMap.get("10");

            StationDto stationDto = new StationDto(toolBoothName, toolBoothName, coordinate.latitude,
                    coordinate.latitude);
            stations.add(stationDto);
        }

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
