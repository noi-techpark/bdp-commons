// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.bdp.commons.dc.meteorology.bz.forecast;

import org.slf4j.LoggerFactory;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.config.DataConfig;
import com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.config.ProvenanceConfig;
import com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.config.StationConfig;
import com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.services.S3Service;
import com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.services.OdhClient;

@Service
public class Scheduler {
    private static final Logger LOG = LoggerFactory.getLogger(Scheduler.class);

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

    @Scheduled(cron = "${scheduler.job}")
    public void collectForecastData() {
        LOG.info("Cron job started");

    }

}
