package it.bz.noi.ondemandmerano.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:ondemandmerano.properties")
public class OnDemandMeranoConfiguration {

    @Value( "${origin}" )
    private String origin;

    @Value("${stop.stationtype}")
    private String stopsStationtype;

    @Value("${vehicle.stationtype}")
    private String vehiclesStationtype;

    @Value("${vehicle.period}")
    private Integer vehiclesPeriod;

    public String getOrigin() {
        return origin;
    }

    public String getStopsStationtype() {
        return stopsStationtype;
    }

    public String getVehiclesStationtype() {
        return vehiclesStationtype;
    }

    public Integer getVehiclesPeriod() {
        return vehiclesPeriod;
    }
}
