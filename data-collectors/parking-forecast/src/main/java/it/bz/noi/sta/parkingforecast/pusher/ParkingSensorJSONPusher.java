// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

/*
 *  Parking Sensor Json Pusher
 *
 *  (C) 2021 NOI Techpark Südtirol / Alto Adige
 *
 *  changelog:
 *  2022-02-23  1.0 - thomas.nocker@catch-solve.tech
 */

package it.bz.noi.sta.parkingforecast.pusher;

import org.springframework.stereotype.Service;

@Service
public class ParkingSensorJSONPusher extends AbstractParkingForecastJSONPusher {
	@Override
	public String initIntegreenTypology() {
		return connectorConfiguration.getStationtypeParkingSensor();
	}
}
