package it.bz.idm.bdp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationList;
import it.bz.idm.bdp.forecast.domain.ParkingForecast;
import it.bz.idm.bdp.forecast.domain.ParkingForecasts;
import it.bz.idm.bdp.json.JSONPusher;

@Component
public class ParkingPusher extends JSONPusher{

	public static final String FORECAST_PREFIX = "parking-forecast-";

	public static final int[] PREDICTION_FORECAST_TIMES_IN_MINUTES = {30,60,90,120,150,180,210,240};

	@Autowired
	private ParkingClient parkingClient;

	@Autowired
	private ParkingMeranoClient parkingMeranoClient;

	@Autowired
	private PredictionRetriever predictionRetriever;
	@Autowired
	private ParkingFrontEndRetriever parkingFrontEndRetriever;

	public void connectToParkingServer() {
		parkingClient.connect();
	}


	public void pushParkingMetaData() {
		connectToParkingServer();
		StationList stations = new StationList();
		parkingClient.insertParkingMetaDataInto(stations);
		parkingMeranoClient.insertParkingMetaDataInto(stations);
		if (!stations.isEmpty())
			syncStations(this.integreenTypology,stations);
	}


	public void pushPredictionData(){
		String[] identifers = parkingFrontEndRetriever.getActiveStationIdentifers();
		if (identifers!=null){
			connectToDataCenterCollector();
			DataMapDto<RecordDtoImpl> dataMap = new DataMapDto<>();
			for (String stationIdentifier:identifers){
				ParkingForecasts predictions = predictionRetriever.predict(stationIdentifier);
				DataMapDto<RecordDtoImpl> typeMap = generateTypeMap(predictions);
				dataMap.getBranch().put(stationIdentifier, typeMap);
			}
			pushData(dataMap);
		}
	}

	public DataMapDto<RecordDtoImpl> generateTypeMap(ParkingForecasts predictions) {
		DataMapDto<RecordDtoImpl> typeMap = new DataMapDto<>();
		if (predictions != null){
			for (Integer period : ParkingPusher.PREDICTION_FORECAST_TIMES_IN_MINUTES){
				List<RecordDtoImpl> records = new ArrayList<>();
				ParkingForecast prediction = predictions.findByTime(period);
				Date date = new Date(prediction.getStartDate().getTime());
				Double value = new Double(prediction.getPrediction().getPredictedFreeSlots().doubleValue());
				SimpleRecordDto dto = new SimpleRecordDto(date.getTime(), value);
				dto.setPeriod(period*60);
				records.add(dto);
				DataMapDto<RecordDtoImpl> recordMap = new DataMapDto<>();
				recordMap.setData(records);
				typeMap.getBranch().put(FORECAST_PREFIX+period, recordMap);
			}
		}
		return typeMap;
	}

	public void pushData() {
		connectToParkingServer();
		connectToDataCenterCollector();
		DataMapDto<RecordDtoImpl> dataMap = new DataMapDto<RecordDtoImpl>();
		parkingClient.insertDataInto(dataMap);
		parkingMeranoClient.insertDataInto(dataMap);
		for (Map.Entry<String, DataMapDto<RecordDtoImpl>> entry : dataMap.getBranch().entrySet()) {
            System.out.println("Station: "+entry.getKey());
            for (Map.Entry<String, DataMapDto<RecordDtoImpl>> typeEntry : entry.getValue().getBranch().entrySet()) {
                System.out.println("\tDatatype: "+typeEntry.getKey()+" records:"+typeEntry.getValue().getData().size());
            }
 }
		System.out.println();
		pushData(dataMap);
	}




	@Override
	public String initIntegreenTypology() {
		return "ParkingStation";
	}


	@Override
	public <T> DataMapDto<RecordDtoImpl> mapData(T data) {
		// TODO Auto-generated method stub
		return null;
	}

}
