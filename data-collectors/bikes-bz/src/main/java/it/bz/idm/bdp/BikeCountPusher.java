// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.ProvenanceDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.json.NonBlockingJSONPusher;

@Component
public class BikeCountPusher extends NonBlockingJSONPusher {

	@Autowired
	private Environment env;

	public static final String STATIONTYPE_IDENTIFIER = "BikeCounter";

	@Override
	public String initIntegreenTypology() {
		return STATIONTYPE_IDENTIFIER;
	}

	@Override
	public <T> DataMapDto<RecordDtoImpl> mapData(T data) {
		return null;
	}

	@Override
	public ProvenanceDto defineProvenance() {
		return new ProvenanceDto(null, env.getProperty("provenance_name"), env.getProperty("provenance_version"),  env.getProperty("origin"));
	}

}
