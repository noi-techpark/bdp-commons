package it.bz.noi.onstreetparking;

import it.bz.noi.onstreetparking.dto.ParkingData;

public interface IMqttMessageArrivedCallback {

	void onParkingDataArrived(ParkingData parkingData);
}
