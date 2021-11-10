package it.bz.noi.trafficeventroadworkbz.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.UUID;

@Configuration
@PropertySource("classpath:trafficeventsroadworks.properties")
public class TrafficEventRoadworkBZConfiguration {

    @Value( "${origin}" )
    private String origin;

    @Value("${integreenTypology}")
    private String integreenTypology;

    @Value("${uuidNamescpace}")
    private UUID uuidNamescpace;

    public String getOrigin() {
        return origin;
    }

    public String getIntegreenTypology() {
        return integreenTypology;
    }

    public UUID getUuidNamescpace() {
        return uuidNamescpace;
    }
}
