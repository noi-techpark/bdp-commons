// Copyright (C) 2015 TIS Innovation Park - Bolzano/Bozen - Italy
// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.tis.integreen.carsharingbzit.tis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.ProvenanceDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.json.NonBlockingJSONPusher;

/**
 *
 * @author Davide Montesin <d@vide.bz>
 */

@Component
public class CarSharingPusher extends NonBlockingJSONPusher
{
	@Autowired
	private Environment env;
	
	@Override
	public Object pushData(String datasourceName, DataMapDto<?> dto) {
		return super.pushData(datasourceName, dto);
	}

	@Override
	public String initIntegreenTypology() {
		return "CarsharingStation";
	}

	@Override
	public <T> DataMapDto<RecordDtoImpl> mapData(T data) {
		return null;
	}

	@Override
	public ProvenanceDto defineProvenance() {
		return new ProvenanceDto(null, env.getProperty("provenance_name"), env.getProperty("provenance_version"), "HAL-API");
	}

}
