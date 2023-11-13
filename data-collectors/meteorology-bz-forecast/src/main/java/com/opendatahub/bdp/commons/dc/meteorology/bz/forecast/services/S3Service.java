// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.services;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class S3Service {
    private final static Logger LOG = LoggerFactory.getLogger(S3Service.class);

    @Autowired
    @Qualifier("dataWebClient") // avoid overlap with webClient in bdp-core
    private WebClient client;

    public List<Object> getForecastData() {
        LOG.info("Getting forecast data...");

        LOG.info("Getting forecast data DONE.");
        return null;
    }
}
