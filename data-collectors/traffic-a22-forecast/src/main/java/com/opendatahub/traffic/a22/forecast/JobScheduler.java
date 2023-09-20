// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.traffic.a22.forecast;

import org.slf4j.LoggerFactory;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.opendatahub.traffic.a22.forecast.config.DataConfig;
import com.opendatahub.traffic.a22.forecast.config.StationConfig;
import com.opendatahub.traffic.a22.forecast.dto.ForecastDto;
import com.opendatahub.traffic.a22.forecast.services.A22Service;
import com.opendatahub.traffic.a22.forecast.services.OdhClient;
import com.opendatahub.traffic.a22.forecast.config.ProvenanceConfig;

@Service
public class JobScheduler {
    private static final Logger LOG = LoggerFactory.getLogger(JobScheduler.class);

    @Value("${historyimport.enabled}")
    private boolean historyEnabled;

    @Value("#{new java.text.SimpleDateFormat('${historyimport.dateformat}').parse('${historyimport.startdate}')}")
    private YearMonth historyStartDate;

    @Value("${forecast.months}")
    private int forecastMonths;

    @Lazy
    @Autowired
    private OdhClient odhClient;

    @Lazy
    @Autowired
    private A22Service a22Service;

    @Autowired
    private DataConfig dataC;

    @Autowired
    private StationConfig stationC;

    @Autowired
    private ProvenanceConfig provC;

    @PostConstruct
    public void postConstruct() {
        if (historyEnabled) {
            LOG.info("Historical import job started...");
            syncData(historyStartDate, YearMonth.now());
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
        LOG.info("Sync started from {}...", historyStartDate.toString());

        List<ForecastDto> forecasts = new ArrayList<>();

        while (historyStartDate.isBefore(to)) {
            forecasts.add(a22Service.getForecasts(from));
            from = from.plusMonths(1);
        }

        // sync with Open Data Hub

        LOG.info("Sync done. Imported {} months.", forecasts.size());
    }
}
