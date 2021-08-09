package it.bz.noi.ondemandmerano.pusher;

import org.springframework.stereotype.Component;

@Component
public class StopJSONPusher extends OnDemandServiceJSONPusher {

    @Override
    public String initIntegreenTypology() {
        return onDemandMeranoConfiguration.getStopsStationtype();
    }
}
