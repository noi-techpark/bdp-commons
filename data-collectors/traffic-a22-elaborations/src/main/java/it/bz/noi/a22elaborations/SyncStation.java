package it.bz.noi.a22elaborations;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;

@Component
public class SyncStation
{

	private static final Logger LOG = LoggerFactory.getLogger(SyncStation.class);
	private static String origin;
	private static String stationtype;

	@Autowired
	private A22TrafficJSONPusher pusher;

	// Development / Testing only
	public static void main(String[] args) throws IOException, SQLException
	{
		Connection connection = Utility.createConnection();
		//saveStations(connection);
		connection.close();
		InputStream in = Utility.class.getResourceAsStream("elaborations.properties");
		Properties prop = new Properties();
		try {
			prop.load(in);
			origin = prop.getProperty("origin");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Saves all stations and data types to the bdp-core
	 * @param connection
	 * @throws SQLException
	 */
	public StationList saveStations(Connection connection) throws IOException, SQLException
	{
		LOG.debug("Start MainSaveStation");

		LOG.debug("Read stations");
		StationList stationList = readStationList(connection);
		LOG.debug("Size stationlist: " + stationList.size());

		LOG.debug("Push stations");
		pusher.syncStations(stationList);

		return stationList;
	}

	/**
	 * Reads the stations from the db and returns a list of stations
	 *
	 * @return List of all stations
	 * @throws IOException
	 * @throws SQLException
	 */
	public static StationList readStationList(Connection connection) throws IOException, SQLException
	{

		InputStream in = Utility.class.getResourceAsStream("elaborations.properties");
		Properties prop = new Properties();
		try {
			prop.load(in);
			origin = prop.getProperty("origin");
			stationtype = prop.getProperty("stationtype");
		} catch (IOException e) {
			e.printStackTrace();
		}
		LOG.debug("Create stationlist");
		StationList stationList = new StationList();
		LOG.debug("Read stations from db");
		String query = Utility.readResourceText(SyncStation.class, "read-all-stations.sql");
		LOG.debug("Create prepared statement");
		try (
			PreparedStatement ps = connection.prepareStatement(query);
		) {
			LOG.debug("Create result set");
			ResultSet resultSet = ps.executeQuery();
			while (resultSet.next())
			{
				LOG.debug("Read stationcode:" );
				String code = resultSet.getString("code");
				LOG.debug(code);
				LOG.debug("Read stationname:");
				String name = resultSet.getString("name");
				LOG.debug(name);
				LOG.debug("Read geo:");
				String geo = resultSet.getString("geo");
				LOG.debug(geo);
				String[] latlng = geo.split(",");
				double lat = Double.parseDouble(latlng[0]);
				double lng = Double.parseDouble(latlng[1]);

				LOG.debug("Create stationDto");
				StationDto station = new StationDto(code, name, lat, lng);
				station.setOrigin(origin);
				station.setStationType(stationtype);
				HashMap<String, Object> metadataMap = new HashMap<String, Object>();
				String metadata = resultSet.getString("metadata");
				metadataMap.put("a22_metadata", metadata);
				station.setMetaData(metadataMap);
				LOG.debug("Add stationDto to stationList");
				stationList.add(station);
			}
		}
		LOG.debug("Return stationlist");
		return stationList;

	}

}
