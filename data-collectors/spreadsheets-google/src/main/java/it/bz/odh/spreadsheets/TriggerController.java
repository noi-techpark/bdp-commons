package it.bz.odh.spreadsheets;

import java.util.Date;

import org.apache.log4j.Logger;
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

	private static final int MINIMAL_SYNC_PAUSE_SECONDS = 60;

	@Autowired
	private JobScheduler scheduler;

	private static Long lastRequest;

	private Logger logger = Logger.getLogger(TriggerController.class);

	/**
	 * Endpoint call for google notification service
	 * https://developers.google.com/drive/api/v3/push.
	 *
	 * @param gDto request body of push notification by google as defined in
	 *             https://developers.google.com/drive/api/v3/push
	 */
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody void post(@RequestBody(required = false) GooglePushDto gDto){
		Long now = new Date().getTime();
		if (lastRequest == null || lastRequest < now - (MINIMAL_SYNC_PAUSE_SECONDS *1000)) {
			lastRequest = now;
			scheduler.syncData();
			logger.info("Synching executed at:"+lastRequest);
		}
	}

}
