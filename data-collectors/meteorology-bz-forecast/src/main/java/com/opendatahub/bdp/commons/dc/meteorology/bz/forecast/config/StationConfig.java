// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StationConfig {
    @Value("${station.stationType}")
    public String stationType;

    @Value("${station.stationBayType}")
    public String stationBayType;

    @Value("${station.stationLocationType}")
    public String stationLocationType;
}
