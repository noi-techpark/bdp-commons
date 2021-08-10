package it.bz.noi.ondemandmerano.pusher;

import org.springframework.stereotype.Component;

@Component
public class PolygonJSONPusher extends OnDemandServiceJSONPusher {

    @Override
    public String initIntegreenTypology() {
        return onDemandMeranoConfiguration.getPolygonsCategory();
    }
}
