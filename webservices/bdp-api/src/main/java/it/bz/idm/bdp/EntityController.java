// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import it.bz.idm.bdp.ws.DataRetriever;
import it.bz.idm.bdp.ws.RestClient;
import it.bz.idm.bdp.ws.RestController;

@Controller
@RequestMapping("/rest/")
public class EntityController extends RestController{

	@Autowired
	private RestClient retriever;

	@Value("${bdp.stationtype}")
	protected String stationType;

	@Override
	public DataRetriever initDataRetriever() {
		retriever.setStationType(stationType);
		retriever.connect();
		return retriever;
	}
}
