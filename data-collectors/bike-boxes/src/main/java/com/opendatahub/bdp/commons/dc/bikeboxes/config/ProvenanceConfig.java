package com.opendatahub.bdp.commons.dc.bikeboxes.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ProvenanceConfig {
    @Value("${provenance.name}")
    public String name;

    @Value("${provenance.version}")
    public String version;

    @Value("${provenance.origin}")
    public String origin;
}
