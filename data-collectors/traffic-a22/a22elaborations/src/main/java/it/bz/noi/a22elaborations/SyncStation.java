package it.bz.noi.a22elaborations;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;

public class SyncStation
{

	private static Logger log = Logger.getLogger(SyncStation.class);

	public static void main(String[] args) throws IOException, SQLException
	{
		Connection connection = Utility.createConnection();
		saveStations(connection);
		connection.close();
	}

	/**
	 * Saves all stations and data types to the bdp-core
	 * @param connection 
	 * @throws SQLException 
	 */
	public static StationList saveStations(Connection connection) throws IOException, SQLException
	{
		log.debug("Start MainSaveStation");

		log.debug("Read stations");
		StationList stationList = readStationList(connection);
		log.debug("Size stationlist: " + stationList.size());

		log.debug("Push stations");
		A22TrafficJSONPusher pusher = new A22TrafficJSONPusher();
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

		log.debug("Create stationlist");
		StationList stationList = new StationList();
		log.debug("Read stations from db");
		String query = Utility.readResourceText(SyncStation.class, "read-all-stations.sql");
		log.debug("Create prepared statement");
		PreparedStatement ps = connection.prepareStatement(query);
		log.debug("Create result set");
		ResultSet resultSet = ps.executeQuery();
		while (resultSet.next())
		{
			log.debug("Read stationcode:" );
			String code = resultSet.getString("code");
			log.debug(code);
			log.debug("Read stationname:");
			String name = resultSet.getString("name");
			log.debug(name);
			log.debug("Read geo:");
			String geo = resultSet.getString("geo");
			log.debug(geo);
			String[] latlng = geo.split(",");
			double lat = Double.parseDouble(latlng[0]);
			double lng = Double.parseDouble(latlng[1]);

			log.debug("Create stationDto");
			StationDto station = new StationDto(code, name, lat, lng);
			log.debug("Add stationDto to stationList");
			stationList.add(station);
		}
		log.debug("Return stationlist");
		return stationList;

	}

}
