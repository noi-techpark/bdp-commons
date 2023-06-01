// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.noi.a22elaborations;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.ProvenanceDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.json.NonBlockingJSONPusher;

@Service
public class A22TrafficJSONPusher extends NonBlockingJSONPusher{

	@Autowired
	private Environment env;

	private String origin;

	public <T> DataMapDto<RecordDtoImpl> mapData(T arg0)
	{
		throw new IllegalStateException("it is used by who?");
	}

	@Override
	public String initIntegreenTypology()
	{
		return "TrafficSensor";
	}

	@Override
	public ProvenanceDto defineProvenance() {
		InputStream in = Utility.class.getResourceAsStream("elaborations.properties");
		Properties prop = new Properties();
		try {
			prop.load(in);
			origin = prop.getProperty("origin");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ProvenanceDto(null, env.getProperty("provenance_name"), env.getProperty("provenance_version"),  origin);
	}

}
