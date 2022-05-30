/*
 *  A22 Events Data Collector - Main Application
 *
 *  (C) 2021 NOI Techpark Südtirol / Alto Adige
 *
 *  changelog:
 *  2021-06-04  1.0 - thomas.nocker@catch-solve.tech
 */

package it.bz.noi.onstreetparking;

import it.bz.noi.onstreetparking.dto.ParkingData;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MainOnStreetParking implements IMqttMessageArrivedCallback {

	private static Logger LOG = LoggerFactory.getLogger(MainOnStreetParking.class);

	@Autowired
	private OnStreetParkingDataMqqtConnector onStreetParkingDataMqqtConnector;
	@Autowired
	private OnStreetParkingSensorService onStreetParkingSensorService;

    public void execute() {
		LOG.info("Start MainOnStreetParking execute");
        try {
			onStreetParkingDataMqqtConnector.setMqttMessageArrivedCallback(this);
			onStreetParkingDataMqqtConnector.connect();
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(e.getMessage());
        }
    }
    public void cleanupStations() {
		LOG.info("Start MainOnStreetParking cleanupStations");
        try {
			onStreetParkingSensorService.cleanupStationList();
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(e.getMessage());
        }
    }

	@Override
	public void onParkingDataArrived(ParkingData parkingData) {
		LOG.info("onParkingDataArrived");
		onStreetParkingSensorService.applyParkingData(parkingData);
	}


}
