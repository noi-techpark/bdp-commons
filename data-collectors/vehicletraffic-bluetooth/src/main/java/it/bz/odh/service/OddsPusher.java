package it.bz.odh.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.OddsRecordDto;
import it.bz.idm.bdp.dto.ProvenanceDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.json.NonBlockingJSONPusher;
import it.bz.odh.util.EncryptUtil;
import it.bz.odh.web.RecordList;

@Lazy
@Component
public class OddsPusher extends NonBlockingJSONPusher {

	@Autowired
	private Environment env;

	@Autowired
	private EncryptUtil cryptUtil;

	@Override
	public String initIntegreenTypology() {
		return "BluetoothStation";
	}

	/**
	 * maps received data from bluetoothbox to ODH and encrypts mac addresses
	 */
	@Override
	public <T> DataMapDto<RecordDtoImpl> mapData(T data) {
		DataMapDto<RecordDtoImpl> dataMap = new DataMapDto<>();
		@SuppressWarnings("unchecked")
		List<OddsRecordDto> dtos = (List<OddsRecordDto>) data;
		String dataType = env.getRequiredProperty("datatype");
		for (OddsRecordDto dto : dtos){
			DataMapDto<RecordDtoImpl> stationMap = dataMap.upsertBranch(dto.getStationcode());
			DataMapDto<RecordDtoImpl> typeMap = stationMap.upsertBranch(dataType);
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
		return new ProvenanceDto(null, env.getProperty("provenance_name"), env.getProperty("provenance_version"),  env.getProperty("origin"));
	}

    public List<String> hash(RecordList records) {
        List<String> hashes = new ArrayList<String>();
        for (OddsRecordDto r : records) {
            String hash = cryptUtil.encrypt(r.getMac());
            hashes.add(hash);
        }
        return hashes;
    }
}
