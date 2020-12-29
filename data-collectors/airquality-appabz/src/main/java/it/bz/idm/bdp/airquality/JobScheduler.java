package it.bz.idm.bdp.airquality;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import it.bz.idm.bdp.airquality.parser.MyAirQualityListener;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;

/**
 * Cronjob configuration can be found under src/main/resources/META-INF/spring/applicationContext.xml
 */
@Component
public class JobScheduler {

	private static final Logger LOG = LogManager.getLogger(JobScheduler.class.getName());

	/* Change this if it gets changed inside log4j2.properties */
	private static final String RESPONSEFILE = "/var/log/opendatahub/data-collectors/air-quality-response.log";

	@Autowired
	private Environment env;

	@Autowired
	private DataPusher pusher;

	@Autowired
	private DataCom com;

	@Autowired
	private DataModel model;


	public void pushDataToCollector() throws Exception {
		com.fetchMetaDataFromFTP();
		model.parseMetaData(env.getRequiredProperty("ftp.folder.local.meta"));
		pusher.setDataModel(model);

		/* Sync stations */
		List<StationDto> stationsODP = com.fetchStationsFromOpenDataPortal(model.validStationsFull);
		pusher.syncStations(new StationList(stationsODP));

		/* Sync data types */
		List<DataTypeDto> dataTypes = new ArrayList<DataTypeDto>(model.validParametersFull.values());
		pusher.syncDataTypes(dataTypes);

		/* Sync measurements */
		com.fetchDataFromFTP();
		File[] files = new File(env.getRequiredProperty("ftp.folder.local.data")).listFiles();
		if (files.length == 0) {
			return;
		}
		Arrays.sort(files,new FileNameComparator());
		for (File file : files) {

			if (file.isDirectory() || (file.isFile() && !file.getName().endsWith(".dat"))) {
				LOG.debug("Skip file/folder: {}", file.getName());
				continue;
			}

			LOG.log(Level.forName("FEEDBACK", 1), "Processing file {}", file.getName());

			try {
				MyAirQualityListener listener = model.parse(new FileInputStream(file));

				LOG.log(Level.forName("FEEDBACK", 1), "    Parsing done. Got some statistics:");
				for (Map.Entry<String, Long> entry : listener.getStatistics().entrySet()) {
					LOG.log(Level.forName("FEEDBACK", 1), "    > {} = {}", entry.getKey(), entry.getValue());
				}

				pusher.mapData(listener.getStations());
				pusher.pushData();
				LOG.log(Level.forName("FEEDBACK", 1), "    Pushing to the database... Done.");

				com.moveProcessedFile(file.getName(), env.getRequiredProperty("ftp.folder.remote"),
						env.getRequiredProperty("ftp.folder.remote") + File.separator + env
								.getRequiredProperty("ftp.folder.remote.processed"));
				if (!file.delete()) {
					throw new Exception(String.format("File %s not deletable locally.", file.getName()));
				}
				LOG.log(Level.forName("FEEDBACK", 1), "    Processing of file {} succeeded.", file.getName());
			} catch (Exception e) {
				LOG.error("Processing of '{}' failed: {}.", file.getAbsolutePath(), e.getMessage());
				LOG.log(Level.forName("FEEDBACK", 1),
						"    Processing of file " + file.getName() + " failed: " + e.getMessage());
				LOG.log(Level.forName("FEEDBACK", 1), "    Stacktrace:\n{}", e);
				com.moveProcessedFile(file.getName(), env.getRequiredProperty("ftp.folder.remote"),
						env.getRequiredProperty("ftp.folder.remote") + File.separator + env
								.getRequiredProperty("ftp.folder.remote.failed"));
			}
		}
		// com.sendFeedbackToFTP(RESPONSEFILE);
	}

	public void deleteOldFiles() throws Exception {
		com.deleteOldFiles();
	}
}