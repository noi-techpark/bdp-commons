// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.noi.bikechargers.pusher;

import org.springframework.stereotype.Component;

@Component
public class BikeChargerJSONPusher extends AbstractBikeChargerJSONPusher {

    @Override
    public String initIntegreenTypology() {
        return bikeChargerConfiguration.getBikeChargerStationtype();
    }
}
