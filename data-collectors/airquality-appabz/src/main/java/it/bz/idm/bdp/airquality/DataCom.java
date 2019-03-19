package it.bz.idm.bdp.airquality;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpStatVFS;

import it.bz.idm.bdp.airquality.utils.FileTools;
import it.bz.idm.bdp.airquality.utils.FtpTools;
import it.bz.idm.bdp.dto.StationDto;

@Component
@PropertySource({ "classpath:/META-INF/spring/application.properties" })
public class DataCom {

	private static final Logger log = LogManager.getLogger(DataCom.class.getName());

	@Autowired
	private Environment env;

	/**
	 * Fetch meta data from FTP. The FTP folder must contain a mapping.zip files containing
	 * all files that are described in "infos/mapping-files/README.md"
	 *
	 * @throws Exception
	 */
	public void fetchMetaDataFromFTP() throws Exception {
		FtpTools ftp = null;
		try {
			String knownHostsFile = FileTools.expandPath(env.getRequiredProperty("ftp.ssh.knownhosts"));
			String privateKeyFile = FileTools.expandPath(env.getRequiredProperty("ftp.ssh.privatekey"));
			String localFolder = env.getRequiredProperty("ftp.folder.local.meta");
			String remoteFolder = env.getRequiredProperty("ftp.folder.remote");
			String mappingFile = env.getRequiredProperty("ftp.file.mapping");

			FileTools.createWriteableFolderIfNotExists(localFolder);
			FileTools.purgeDirectory(localFolder);

			ftp = new FtpTools(knownHostsFile, privateKeyFile,
					env.getRequiredProperty("ftp.user"), env.getRequiredProperty("ftp.server"),
					env.getRequiredProperty("ftp.port", Integer.class), env.getRequiredProperty("ftp.pass"),
					env.getRequiredProperty("ftp.stricthostkeychecking", Boolean.class));

			ChannelSftp c = ftp.connect();

			log.log(Level.forName("FEEDBACK", 1), "Fetching " + remoteFolder + File.separator + mappingFile);
			c.get(remoteFolder + File.separator + mappingFile, localFolder + File.separator + mappingFile);
			FileTools.unZip(localFolder + File.separator + mappingFile, localFolder);
			log.log(Level.forName("FEEDBACK", 1),
					"Download of " + remoteFolder + File.separator + mappingFile + " complete");
		} catch (JSchException | SftpException e) {
			log.error(e.getMessage());
			log.log(Level.forName("FEEDBACK", 1), e.getMessage());
			e.printStackTrace();
			throw e;
		} finally {
			if (ftp != null)
				ftp.close();
		}
	}

	public void fetchDataFromFTP() throws IOException, JSchException {
		FtpTools ftp = null;
		try {
			String knownHostsFile = FileTools.expandPath(env.getRequiredProperty("ftp.ssh.knownhosts"));
			String privateKeyFile = FileTools.expandPath(env.getRequiredProperty("ftp.ssh.privatekey"));
			String remoteFolder = env.getRequiredProperty("ftp.folder.remote");
			String localFolder = env.getRequiredProperty("ftp.folder.local.data");

			FileTools.createWriteableFolderIfNotExists(localFolder);
			FileTools.purgeDirectory(localFolder);

			ftp = new FtpTools(knownHostsFile, privateKeyFile,
					env.getRequiredProperty("ftp.user"), env.getRequiredProperty("ftp.server"),
					env.getRequiredProperty("ftp.port", Integer.class), env.getRequiredProperty("ftp.pass"),
					env.getRequiredProperty("ftp.stricthostkeychecking", Boolean.class));

			ChannelSftp c = ftp.connect();

			c.cd(remoteFolder);

			@SuppressWarnings("unchecked")
			Vector<ChannelSftp.LsEntry> list = c.ls("*.dat");
			if (list.isEmpty()) {
				log.debug("No *.dat files found on server! Nothing to do here... closing connection.");
			} else {
				int downloadLimit = env.getRequiredProperty("ftp.download-limit", Integer.class);
				for (ChannelSftp.LsEntry entry : list) {
					if (downloadLimit == 0)
						break;
					downloadLimit--;
					c.get(entry.getFilename(), localFolder + File.separator + entry.getFilename());
					log.info("File {} downloaded", entry.getFilename());
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			log.log(Level.forName("FEEDBACK", 1), e.getMessage());
			e.printStackTrace();
		} finally {
			if (ftp != null)
				ftp.close();
		}
	}

	public void sendFeedbackToFTP(final String file) throws Exception {
		FtpTools ftp = null;
		try {
			String knownHostsFile = FileTools.expandPath(env.getRequiredProperty("ftp.ssh.knownhosts"));
			String privateKeyFile = FileTools.expandPath(env.getRequiredProperty("ftp.ssh.privatekey"));
			String remoteFolder = env.getRequiredProperty("ftp.folder.remote");

			ftp = new FtpTools(knownHostsFile, privateKeyFile, env.getRequiredProperty("ftp.user"),
					env.getRequiredProperty("ftp.server"), env.getRequiredProperty("ftp.port", Integer.class),
					env.getRequiredProperty("ftp.pass"),
					env.getRequiredProperty("ftp.stricthostkeychecking", Boolean.class));

			ChannelSftp c = ftp.connect();
			c.put(file, remoteFolder);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			throw e;
		} finally {
			if (ftp != null)
				ftp.close();
		}
	}

	public void moveProcessedFile(final String file, final String srcFolder, final String destFolder)
			throws IOException, JSchException {
		FtpTools ftp = null;
		try {
			String knownHostsFile = FileTools.expandPath(env.getRequiredProperty("ftp.ssh.knownhosts"));
			String privateKeyFile = FileTools.expandPath(env.getRequiredProperty("ftp.ssh.privatekey"));

			ftp = new FtpTools(knownHostsFile, privateKeyFile, env.getRequiredProperty("ftp.user"),
					env.getRequiredProperty("ftp.server"), env.getRequiredProperty("ftp.port", Integer.class),
					env.getRequiredProperty("ftp.pass"),
					env.getRequiredProperty("ftp.stricthostkeychecking", Boolean.class));

			ChannelSftp c = ftp.connect();
			try {
				c.mkdir(destFolder);
			} catch (Exception e) {
				/*
				 * Ignore mkdir issues, maybe the folder exists already;
				 * ftp-rename fails otherwise nevertheless, hence we can ignore an
				 * expensive check here.
				 */
			}
			c.rename(srcFolder + File.separator + file, destFolder + File.separator + file);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
			if (ftp != null)
				ftp.close();
		}
	}

	public List<StationDto> fetchStationsFromOpenDataPortal(Map<String, StationDto> validStations) throws IOException {
		List<StationDto> stations = new ArrayList<StationDto>();
		URL url = new URL(env.getRequiredProperty("odp.url.stations"));
		try (InputStream inputStream = url.openStream(); JsonReader rdr = Json.createReader(inputStream)) {

			JsonObject obj = rdr.readObject();

			/*
			 * We ignore projection attributes odp.url.stations, because it is not valid for the
			 * LAT/LONG Json attributes. They refer to the WGS84 coordinate system with a EPSG:4326
			 * projection. The projection attribute refers to the coordinates fields inside JSON,
			 * but those will not be used here.
			 */
			String crs = env.getRequiredProperty("odh.station.projection");

			JsonArray results = obj.getJsonArray("features");
			for (JsonObject result : results.getValuesAs(JsonObject.class)) {

				try {
					String code = result.getJsonObject("properties").getString("SCODE");
					StationDto station = validStations.get(code);
					if (station == null)
						continue;

					station.setCoordinateReferenceSystem(crs);
					station.setName(result.getJsonObject("properties").getString("NAME_I"));
					station.setOrigin(env.getRequiredProperty("odh.station.origin"));

					/*
					 * LAT/LONG attributes are mandatory. We look first into valid stations meta
					 * data, which comes from the mapping.zip file itself. Secondly, if LAT/LONG
					 * attributes are not present inside station.csv, we look at 'odp.url.stations'.
					 * Finally, we skip all stations, that do not have neither coordinate definitions.
					 *
					 * StationDto contains coordinates already, if they were defined in station.csv.
					 */
					if (station.getLatitude() == null || station.getLongitude() == null) {
						try {
							JsonNumber lat = result.getJsonObject("properties").getJsonNumber("LAT");
							JsonNumber lon = result.getJsonObject("properties").getJsonNumber("LONG");
							station.setLatitude(lat.doubleValue());
							station.setLongitude(lon.doubleValue());
						} catch (ClassCastException e) {
							log.info("Skip station '{}' due to invalid latitude/longitude attributes.", code);
							continue;
						}
					}

					stations.add(station);
				} catch (Exception e) {
					e.printStackTrace(System.err);
					log.info("Skip station due to wrong values on input.");
				}
			}
		}

		/* Warn, if a station defined in mapping.zip could not be found on rete-civica */
		for (String stationID : validStations.keySet()) {
			boolean found = false;
			for (StationDto station : stations) {
				if (station.getId().equals(stationID)) {
					found = true;
				}
			}
			if (!found) {
				log.log(Level.forName("FEEDBACK", 1),
						"Mapping contains a station with name '{}', that could not be found or is not complete on {}.",
						stationID, env.getRequiredProperty("odp.url.stations"));
				log.log(Level.forName("FEEDBACK", 1),
						"     Please note, only stations with valid LAT/LONG attributes can be accepted.");
				log.log(Level.forName("FEEDBACK", 1), "     Station '{}' removed from list of valid stations.",
						stationID);
			}
		}

		return stations;
	}

	public void deleteOldFiles() throws Exception {
		deleteOldFiles(null, -1, -1);
	}

	public void deleteOldFiles(final String folder, long spaceLessThan, long olderThanInDays) throws Exception {

		if (env.getRequiredProperty("ftp.deleteFiles").equalsIgnoreCase("false")) {
			return;
		}

		FtpTools ftp = null;
		try {
			String knownHostsFile = FileTools.expandPath(env.getRequiredProperty("ftp.ssh.knownhosts"));
			String privateKeyFile = FileTools.expandPath(env.getRequiredProperty("ftp.ssh.privatekey"));
			String remoteFolder = env.getRequiredProperty("ftp.folder.remote");
			String remoteFolderDone = folder == null
					? remoteFolder + File.separator + env.getRequiredProperty("ftp.folder.remote.processed")
					: folder;

			ftp = new FtpTools(knownHostsFile, privateKeyFile, env.getRequiredProperty("ftp.user"),
					env.getRequiredProperty("ftp.server"), env.getRequiredProperty("ftp.port", Integer.class),
					env.getRequiredProperty("ftp.pass"),
					env.getRequiredProperty("ftp.stricthostkeychecking", Boolean.class));

			ChannelSftp c = ftp.connect();
			SftpStatVFS stat = c.statVFS(remoteFolderDone);
			c.cd(remoteFolderDone);

			long spaceThreshold = spaceLessThan == -1
					? env.getRequiredProperty("ftp.deleteFiles.ifSpaceLessThan", Integer.class)
					: spaceLessThan;
			if (spaceThreshold < 100) {
				spaceThreshold = 512;
			}
			long spaceUsed = stat.getUsed() / 1024;
			if (spaceThreshold - spaceUsed < 0) {
				log.debug("Used space bigger than the defined threshold! Nothing to do here... closing connection.");
				return;
			}

			@SuppressWarnings("unchecked")
			Vector<ChannelSftp.LsEntry> list = c.ls("*.dat");
			if (list.isEmpty()) {
				log.debug("No *.dat files found on server! Nothing to do here... closing connection.");
				return;
			}

			long days = olderThanInDays == -1 ? env.getRequiredProperty("ftp.deleteFiles.ifOlderThan",
					Long.class)
					: olderThanInDays;
			if (days < 1 || days > 100) {
				days = 14;
			}
			long timeThreshold = System.currentTimeMillis() / 1000L - days * 24 * 60 * 60;

			int count = 0;
			for (ChannelSftp.LsEntry entry : list) {
				SftpATTRS attribs = c.lstat(entry.getFilename());
				if (attribs.getMTime() <= timeThreshold) {
					log.debug("Delete old processed file: " + remoteFolderDone + File.separator + entry.getFilename());
					c.rm(entry.getFilename());
					count++;
				}
			}
			log.info("Deletion of old processed files: " + count + " out of " + list.size() + " deleted.");
		} catch (JSchException | SftpException e) {
			log.error(e);
			throw e;
		} finally {
			if (ftp != null)
				ftp.close();
		}
	}
}
