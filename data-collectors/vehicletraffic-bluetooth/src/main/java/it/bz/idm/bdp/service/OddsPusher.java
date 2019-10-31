package it.bz.idm.bdp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.OddsRecordDto;
import it.bz.idm.bdp.dto.ProvenanceDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.json.JSONPusher;
import it.bz.idm.bdp.util.EncryptUtil;

@Component
@PropertySource({ "classpath:/META-INF/spring/application.properties" })
public class OddsPusher extends JSONPusher {

	@Autowired
	private Environment env;

	@Autowired
	private EncryptUtil cryptUtil;

	@Override
	public String initIntegreenTypology() {
		return "BluetoothStation";
	}

	@Override
	public <T> DataMapDto<RecordDtoImpl> mapData(T data) {
		DataMapDto<RecordDtoImpl> dataMap = new DataMapDto<>();
		@SuppressWarnings("unchecked")
		List<OddsRecordDto> dtos = (List<OddsRecordDto>) data;
		for (OddsRecordDto dto : dtos){
			DataMapDto<RecordDtoImpl> stationMap = dataMap.upsertBranch(dto.getStationcode());
			DataMapDto<RecordDtoImpl> typeMap = stationMap.upsertBranch(env.getRequiredProperty("datatype"));
			SimpleRecordDto textDto = new SimpleRecordDto();
			String stringValue = dto.getMac();
			if (cryptUtil.isValid())
				stringValue = cryptUtil.encrypt(stringValue);
			textDto.setValue(stringValue);
			textDto.setTimestamp(dto.getGathered_on().getTime());
			textDto.setPeriod(1);
			typeMap.getData().add(textDto);
		}
		return dataMap;
	}

	@Override
	public ProvenanceDto defineProvenance() {
		return new ProvenanceDto(null, env.getProperty("provenance.name"), env.getProperty("provenance.version"),  env.getProperty("origin"));
	}
}