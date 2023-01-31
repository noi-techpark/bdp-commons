package com.opendatahub.bdp.commons.dc.bikeboxes.services;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.ProvenanceDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.json.NonBlockingJSONPusher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.opendatahub.bdp.commons.dc.bikeboxes.config.ProvenanceConfig;

@Lazy
@Service
public class OdhClient extends NonBlockingJSONPusher {

    @Autowired
    private ProvenanceConfig provenanceConfig;

    @Value("${odh_client.stationtype}")
    public String stationtype;

    @Override
    public <T> DataMapDto<RecordDtoImpl> mapData(T data) {
		/* You can ignore this legacy method call */
        return null;
    }

    @Override
    public String initIntegreenTypology() {
        return stationtype;
    }

    @Override
    public ProvenanceDto defineProvenance() {
        return new ProvenanceDto(null, provenanceConfig.name, provenanceConfig.version, provenanceConfig.origin);
    }

	public ProvenanceDto getProvenanceConfig() {
		return provenance;
	}
}
