package com.opendatahub.bdp.commons.dc.bikeboxes.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StationConfig {
    @Value("${station.stationType}")
    public String stationType;

    @Value("${station.stationBayType}")
    public String stationBayType;
}
