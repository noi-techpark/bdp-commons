package it.bz.noi.ondemandmerano.pusher;

import org.springframework.stereotype.Component;

@Component
public class ItineraryJSONPusher extends OnDemandServiceJSONPusher {

    @Override
    public String initIntegreenTypology() {
        return onDemandMeranoConfiguration.getItineraryStationtype();
    }
}
