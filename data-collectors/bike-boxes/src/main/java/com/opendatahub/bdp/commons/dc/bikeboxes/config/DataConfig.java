package com.opendatahub.bdp.commons.dc.bikeboxes.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DataConfig {
    @Value("${data.period}")
    public int period;
    
}
