package it.bz.idm.bdp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.bz.idm.bdp.dto.DataTypeDto;


@Component
public class JobScheduler {

	private static final List<DataTypeDto> dataTypes = new ArrayList<DataTypeDto>() {
		private static final long serialVersionUID = -6024748755532284647L;

	{
		add(new DataTypeDto("parking-forecast","","60m","Forecast"));
		add(new DataTypeDto("free","","","Instantaneous"));
	}};
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
			pusher.syncDataTypes(dataTypes);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
