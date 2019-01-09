package it.bz.idm.bdp;

import org.springframework.beans.factory.annotation.Autowired;
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

	@Override
	public DataRetriever initDataRetriever() {
		retriever.connect();
		return retriever;
	}
}
