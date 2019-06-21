package it.bz.idm.bdp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.ProvenanceDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationList;
import it.bz.idm.bdp.forecast.domain.ParkingForecast;
import it.bz.idm.bdp.forecast.domain.ParkingForecasts;
import it.bz.idm.bdp.json.JSONPusher;

@Component
public class ParkingPusher extends JSONPusher{

	private static final ProvenanceDto merano_provenance = new ProvenanceDto(null, "dc-parking-MeBo", "2.0.0-SNAPSHOT", "Municipality Merano");

	private static final String STATION_TYPE = "ParkingStation";

	private static final String PARKINGSLOT_TYPEIDENTIFIER = "occupied";

	private static final String PARKINGSLOT_METRIC = "Count";

	private static final String TYPE_UNIT = "";

	private static final String FORECAST_METRIC = "Forecast";

	private static final String TYPEDESCRIPTION_SUFFIX = " minutes forecast";

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
		DataMapDto<RecordDtoImpl> dataMap = new DataMapDto<RecordDtoImpl>(), dataMap2 = new DataMapDto<>();
		this.provenance = new ProvenanceDto(null, "dc-parking-MeBo", "2.0.0-SNAPSHOT", config.getString("pbz_origin"));
		parkingClient.insertDataInto(dataMap);

		this.provenance = merano_provenance;
		parkingMeranoClient.insertDataInto(dataMap2);
		pushData(dataMap);
	}




	@Override
	public String initIntegreenTypology() {
		return STATION_TYPE;
	}


	@Override
	public <T> DataMapDto<RecordDtoImpl> mapData(T data) {
		return null;
	}


	public static List<DataTypeDto> getDataTypeList() {
		List<DataTypeDto> dataTypes = new ArrayList<>();
		dataTypes.add(new DataTypeDto(PARKINGSLOT_TYPEIDENTIFIER,TYPE_UNIT,"Occupacy of a parking area",PARKINGSLOT_METRIC));
		for (int minutesForecast:PREDICTION_FORECAST_TIMES_IN_MINUTES)
			dataTypes.add(new DataTypeDto(FORECAST_PREFIX+ minutesForecast,TYPE_UNIT,minutesForecast+TYPEDESCRIPTION_SUFFIX,FORECAST_METRIC));
		return dataTypes;
	}


	@Override
	public ProvenanceDto defineProvenance() {
		return new ProvenanceDto(null, "dc-parking-MeBo", "2.0.0-SNAPSHOT", config.getString("pbz_origin"));
	}
}
