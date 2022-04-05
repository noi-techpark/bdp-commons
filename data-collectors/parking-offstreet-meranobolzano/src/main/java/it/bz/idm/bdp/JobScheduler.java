package it.bz.idm.bdp;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import it.bz.idm.bdp.dto.DataTypeDto;


@Component
public class JobScheduler {

	@Lazy
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
	public void syncDataTypes(){
		try {
			List<DataTypeDto> dataTypeList = ParkingPusher.getDataTypeList();
			pusher.syncDataTypes(dataTypeList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
