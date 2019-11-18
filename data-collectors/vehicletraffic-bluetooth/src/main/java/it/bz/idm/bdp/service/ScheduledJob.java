package it.bz.idm.bdp.service;

import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.google.api.services.drive.model.Channel;

@Service
public class ScheduledJob {
	private Logger logger = Logger.getLogger(ScheduledJob.class);

	@Lazy
	@Autowired
	private SpreadsheetWatcher watcher;

	public void watchBluetoothBoxesSpreadsheet() {
		logger.debug("Run scheduled channel creation");
		Channel channel = new Channel();
		channel.setId(UUID.randomUUID().toString());
		channel.setType("web_hook");
		channel.setAddress("https://boxes.opendatahub.bz.it/dc-vehicletraffic-bluetooth/trigger");
		watcher.registerWatch(channel);
		logger.debug("Channel created and registered");
	}
}
