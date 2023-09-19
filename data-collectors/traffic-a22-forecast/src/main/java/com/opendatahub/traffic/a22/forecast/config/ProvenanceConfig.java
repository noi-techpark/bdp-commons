// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.traffic.a22.forecast.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProvenanceConfig {
    @Value("${provenance.name}")
    public String name;

    @Value("${provenance.version}")
    public String version;

    @Value("${provenance.origin}")
    public String origin;
}
