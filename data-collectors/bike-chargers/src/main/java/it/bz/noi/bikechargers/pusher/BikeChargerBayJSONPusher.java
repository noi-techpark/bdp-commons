package it.bz.noi.bikechargers.pusher;

import org.springframework.stereotype.Component;

@Component
public class BikeChargerBayJSONPusher extends AbstractBikeChargerJSONPusher {

    @Override
    public String initIntegreenTypology() {
        return bikeChargerConfiguration.getBikeChargerBayStationtype();
    }
}
