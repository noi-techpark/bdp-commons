// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.odh.service;

import java.util.UUID;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.google.api.services.drive.model.Channel;

@Service
public class ScheduledJob {
	private Logger logger = LoggerFactory.getLogger(ScheduledJob.class);

	@Lazy
	@Autowired
	private SpreadsheetWatcher watcher;

	public void watchBluetoothBoxesSpreadsheet() {
		logger.debug("Run scheduled channel creation");
		Channel channel = new Channel();
		channel.setId(UUID.randomUUID().toString());
		channel.setType("web_hook");
		channel.setAddress("https://boxes.opendatahub.bz.it/trigger");
		watcher.registerWatch(channel);
		logger.debug("Channel created and registered");
	}
}
