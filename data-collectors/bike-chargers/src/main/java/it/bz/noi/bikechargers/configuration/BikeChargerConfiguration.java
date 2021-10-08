package it.bz.noi.bikechargers.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:bikecharger.properties")
public class BikeChargerConfiguration {

    @Value( "${origin}" )
    private String origin;

    @Value("${bikeCharger.stationtype}")
    private String bikeChargerStationtype;

    @Value("${bikeChargerBay.stationtype}")
    private String bikeChargerBayStationtype;

    @Value("${period}")
    private Integer period;

    public String getOrigin() {
        return origin;
    }

    public String getBikeChargerStationtype() {
        return bikeChargerStationtype;
    }

    public String getBikeChargerBayStationtype() {
        return bikeChargerBayStationtype;
    }

    public Integer getPeriod() {
        return period;
    }
}
