package it.bz.odh.spreadsheets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@RequestMapping("/trigger")
@Controller
@EnableWebMvc
public class TriggerController {

	@Autowired
	private JobScheduler scheduler;

	/**
	 * Endpoint call for google notification service
	 * https://developers.google.com/drive/api/v3/push.
	 *
	 * @param gDto request body of push notification by google as defined in
	 *             https://developers.google.com/drive/api/v3/push
	 */
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody void post(@RequestBody(required = false) GooglePushDto gDto){
		scheduler.syncData();
	}

}
