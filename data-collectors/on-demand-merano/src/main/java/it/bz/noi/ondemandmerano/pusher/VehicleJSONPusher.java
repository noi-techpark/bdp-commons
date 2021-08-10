package it.bz.noi.ondemandmerano.pusher;

import org.springframework.stereotype.Component;

@Component
public class VehicleJSONPusher extends OnDemandServiceJSONPusher {

    @Override
    public String initIntegreenTypology() {
        return onDemandMeranoConfiguration.getVehiclesStationtype();
    }
}
