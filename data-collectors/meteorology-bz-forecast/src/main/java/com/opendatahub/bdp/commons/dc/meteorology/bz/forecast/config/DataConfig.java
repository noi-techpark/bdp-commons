// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.bdp.commons.dc.meteorology.bz.forecast.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DataConfig {
    @Value("${data.period.3h}")
    public int period3h;

    @Value("${data.period.24h}")
    public int period24h;
}
