// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import it.bz.idm.bdp.ws.DataRetriever;
import it.bz.idm.bdp.ws.RestClient;
import it.bz.idm.bdp.ws.RestController;

@Controller
@Conditional(ChildCondition.class)
@RequestMapping("/rest/${bdp.childrenpath}")
@PropertySource("classpath:META-INF/spring/application.properties")
public class ChildEntityController extends RestController {

	@Autowired
	private RestClient childrenRetriever;

	@Value("${bdp.childstationtype}")
	protected String stationType;

	@Override
	public DataRetriever initDataRetriever() {
		childrenRetriever.setStationType(stationType);
		childrenRetriever.connect();
		return childrenRetriever;
	}
}
