// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.traffic.a22.forecast;

import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.opendatahub.traffic.a22.forecast.config.DataConfig;
import com.opendatahub.traffic.a22.forecast.config.StationConfig;
import com.opendatahub.traffic.a22.forecast.dto.ForecastDto;
import com.opendatahub.traffic.a22.forecast.services.A22Client;
import com.opendatahub.traffic.a22.forecast.services.OdhClient;
import com.opendatahub.traffic.a22.forecast.config.ProvenanceConfig;


@Service
public class JobScheduler {
    private static final Logger LOG = LoggerFactory.getLogger(JobScheduler.class);

    @Lazy
    @Autowired
    private OdhClient odhClient;

    @Lazy
    @Autowired
    private A22Client a22Client;

    @Autowired
    private DataConfig dataC;

    @Autowired
    private StationConfig stationC;

    @Autowired
    private ProvenanceConfig provC;

    @Scheduled(cron = "${scheduler.job}")
    public void collectBikeBoxData() {
        LOG.info("Cron job started");

        ForecastDto forecasts = a22Client.getForecasts("2023", "9");
        LOG.info("DONE");

    }
}
