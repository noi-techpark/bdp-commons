package it.bz.idm.bdp.service;

import java.util.List;

import org.springframework.stereotype.Component;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.OddsRecordDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.json.JSONPusher;

@Component
public class OddsPusher extends JSONPusher {


	private static final String ODDS_TYPE = "vehicle detection";

	@Override
	public String initIntegreenTypology() {
		return "Bluetoothstation";
	}

	@Override
	public <T> DataMapDto<RecordDtoImpl> mapData(T data) {
		DataMapDto<RecordDtoImpl> dataMap = new DataMapDto<>();
		@SuppressWarnings("unchecked")
		List<OddsRecordDto> dtos = (List<OddsRecordDto>) data;
		for (OddsRecordDto dto : dtos){
			DataMapDto<RecordDtoImpl> stationMap = dataMap.getBranch().get(dto.getStationcode());
			if (stationMap == null){
				stationMap = new DataMapDto<>();
				dataMap.getBranch().put(dto.getStationcode(), stationMap);
			}
			DataMapDto<RecordDtoImpl> typeMap = stationMap.getBranch().get(ODDS_TYPE);
			if (typeMap == null){
				typeMap = new DataMapDto<>();
				stationMap.getBranch().put(ODDS_TYPE, typeMap);
			}
			SimpleRecordDto textDto = new SimpleRecordDto();
			textDto.setValue(dto.getMac());
			textDto.setTimestamp(dto.getGathered_on().getTime());
			typeMap.getData().add(textDto);
		}
		return dataMap;
	}
}
