package it.bz.noi.bikechargers.pusher;

import org.springframework.stereotype.Component;

@Component
public class BikeChargerJSONPusher extends AbstractBikeChargerJSONPusher {

    @Override
    public String initIntegreenTypology() {
        return bikeChargerConfiguration.getBikeChargerStationtype();
    }
}
