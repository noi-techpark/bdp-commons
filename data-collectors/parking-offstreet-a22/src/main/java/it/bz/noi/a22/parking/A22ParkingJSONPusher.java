// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

/*
 *  A22 Parking Json Pusher
 *
 *  (C) 2021 NOI Techpark Südtirol / Alto Adige
 *
 *  changelog:
 *  2021-06-01  1.0 - thomas.nocker@catch-solve.tech
 */

package it.bz.noi.a22.parking;

import javax.annotation.PostConstruct;

import it.bz.idm.bdp.json.NonBlockingJSONPusher;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.ProvenanceDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;

@Component
public class A22ParkingJSONPusher extends NonBlockingJSONPusher
{

	private static final Logger LOG = LoggerFactory.getLogger(A22ParkingJSONPusher.class);

	private String stationtype;
	private String origin;

	@Autowired
	private Environment env;

	public <T> DataMapDto<RecordDtoImpl> mapData(T arg0)
	{
		throw new IllegalStateException("it is used by who?");
	}

	@PostConstruct
	public void init() {
		LOG.info("start init");
		A22Properties prop = new A22Properties("a22parking.properties");

		stationtype = prop.getProperty("stationtype");
		origin = prop.getProperty("origin");
		super.init();
		LOG.info("end init");
	}

	@Override
	public String initIntegreenTypology()
	{
		return stationtype;
	}

	@Override
	public ProvenanceDto defineProvenance() {
		return new ProvenanceDto(null, env.getProperty("provenance_name"), env.getProperty("provenance_version"),  origin);
	}
}
