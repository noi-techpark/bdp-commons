package it.bz.idm.bdp;

import java.io.IOException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class JobScheduler {

	@Autowired
	private ParkingPusher pusher;

	public void currentSlots() {
		try {
			pusher.pushData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void parkingStations() throws IOException {
		try {
			pusher.pushParkingMetaData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void pushPredictions(){
		try {
			pusher.pushPredictionData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
