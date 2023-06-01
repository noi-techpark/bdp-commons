// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.odh.spreadsheets;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import it.bz.odh.spreadsheets.dto.GooglePushDto;

@RequestMapping("/trigger")
@Controller
@EnableWebMvc
public class TriggerController {

	private static final String GOOGLE_CONTENT_ID = "content";

	private static final int MINIMAL_SYNC_PAUSE_SECONDS = 60;

	@Autowired
	@Lazy
	private ISpreadsheetCollector collector;

	private static Long lastRequest;

	private Logger logger = LoggerFactory.getLogger(TriggerController.class);

	/**
	 * Endpoint call for google notification service
	 * https://developers.google.com/drive/api/v3/push.
	 *
	 * @param gDto request body of push notification by google as defined in
	 *             https://developers.google.com/drive/api/v3/push
	 */
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody void post(@RequestBody(required = false) GooglePushDto gDto,
			@RequestHeader(value = "x-goog-changed", required = false) String googleState) {
		logger.debug("Trigger spreadsheet synchronization at " + lastRequest);
		if (googleState != null) {
			List<String> changeDetails = Arrays.asList(googleState.split(","));
			if (changeDetails != null && changeDetails.contains(GOOGLE_CONTENT_ID)) {
				Long now = new Date().getTime();
				if (lastRequest == null || lastRequest < now - (MINIMAL_SYNC_PAUSE_SECONDS * 1000)) {
					lastRequest = now;
					collector.syncData();
					logger.info("Synching executed at:" + lastRequest);
				}
			}
		}
	}
}
