/*
 *  Parking Station Json Pusher
 *
 *  (C) 2021 NOI Techpark Südtirol / Alto Adige
 *
 *  changelog:
 *  2022-02-23  1.0 - thomas.nocker@catch-solve.tech
 */

package it.bz.noi.sta.parkingforecast.pusher;

import org.springframework.stereotype.Service;

@Service
public class ParkingStationJSONPusher extends AbstractParkingForecastJSONPusher {
	@Override
	public String initIntegreenTypology() {
		return connectorConfiguration.getStationtypeParkingStation();
	}
}
