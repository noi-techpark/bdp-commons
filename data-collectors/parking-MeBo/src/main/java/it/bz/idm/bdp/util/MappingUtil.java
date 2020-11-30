package it.bz.idm.bdp.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.bz.idm.bdp.ParkingPusher;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.forecast.domain.ParkingForecast;
import it.bz.idm.bdp.forecast.domain.ParkingForecasts;

public class MappingUtil {
	
	public static DataMapDto<RecordDtoImpl> generateTypeMap(ParkingForecasts predictions) {
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
				typeMap.getBranch().put(ParkingPusher.FORECAST_PREFIX+period, recordMap);
			}
		}
		return typeMap;
	}
}
