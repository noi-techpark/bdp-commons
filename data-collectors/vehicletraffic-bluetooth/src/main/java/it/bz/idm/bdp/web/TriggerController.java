package it.bz.idm.bdp.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import it.bz.idm.bdp.service.OddsPusher;
import it.bz.idm.bdp.util.BluetoothMappingUtil;

@RequestMapping("/trigger")
@Controller
@EnableWebMvc
public class TriggerController {

	@Autowired
	private Environment env;

	@Autowired
	private OddsPusher pusher;
	
	@Autowired
	private BluetoothMappingUtil metaUtil;


	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody void post(@RequestBody RecordList records){

	}

}
