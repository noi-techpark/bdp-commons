package info.datatellers.appatn.dieciminuti;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import java.text.SimpleDateFormat;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import java.util.ResourceBundle;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataFetcher {

	private final ResourceBundle rb = ResourceBundle.getBundle("config");

	private static final Logger LOG = LogManager.getLogger(DataFetcher.class.getName());

	private final String endpoint = rb.getString("odp.url.stations.10minuti");
	private final String stations_endpoint = endpoint + "stations?key=" + rb.getString("odp.url.stations.10minuti.key");

	private final String observations_endpoint = endpoint + "observations?key="
			+ rb.getString("odp.url.stations.10minuti.key");

	private final String sensors_endpoint = endpoint + "sensors?key=" + rb.getString("odp.url.stations.10minuti.key");

	/**
	 * fetches measurements from observations_endpoint getting station_ID, type_ID,
	 * timestamp and value.
	 * 
	 * @param endpoint   The endpoint string for the requested data
	 * @param parameters The additional parameters in the request query string
	 * @return String representation of the fetched json
	 */

	private String fetch(String endpoint, String parameters) {
		URL website;
		URLConnection connection;
		BufferedReader in;
		StringBuilder response = new StringBuilder();
		String inputLine;
		try {
			website = new URL(endpoint + parameters);
			LOG.debug("Endpoint requested: {}", website);
			try {
				connection = website.openConnection();

				if (connection.getHeaderFields().get(null).get(0).contains("200")) {
					try {
						in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF8"));
						while ((inputLine = in.readLine()) != null)
							response.append(inputLine);

						in.close();
					} catch (UnsupportedEncodingException e) {
						LOG.error("Unexpected non-UTF8 encoding for endpoint {}, {}", website.toString(),
								e.getMessage());
					} catch (IOException e) {
						LOG.error("IOException for endpoint {}, {}", website.toString(), e.getMessage());
					}
				} else {
					LOG.error("Expected HTTP response 200, got {} for endpoint {} {}",
							connection.getHeaderFields().get(null).get(0), website.toString());
				}

			} catch (IOException e) {
				LOG.error("Unable to open connection for endpoint {} {}", website.toString(), e.getMessage());
			}

		} catch (MalformedURLException e) {
			LOG.error("Unexpected url format {} {}", endpoint + parameters, e.getMessage());
		}

		return response.toString();
	}

	/**
	 * Fetches current observations from the endpoint
	 * 
	 * @param stationId
	 * @param measurements
	 * @return String representation of the json response
	 */

	public String fetchData(String stationId, String measurements) {
		return fetch(observations_endpoint, "&stations=" + stationId + "[" + measurements + "]");
	}

	/**
	 * Fetches historic observations from the endpoint
	 * 
	 * @param stationId
	 * @param measurements
	 * @param from
	 * @param to
	 * @return String representation of the json response
	 */
	public String fetchHistoricData(String stationId, String measurements, Date from, Date to) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return fetch(observations_endpoint, "&stations=" + stationId + "[" + measurements + "]" + "&date_from="
				+ format.format(from) + "&date_to=" + format.format(to));
	}

	/**
	 * Fetches sensors from the endpoint
	 * 
	 * @param stationId
	 * @return String representation of the json response
	 */
	public String fetchSensors(String stationId) {
		return fetch(sensors_endpoint, "&stations=" + stationId);
	}

	/**
	 * Fetches stations from the endpoint
	 * 
	 * @return String representation of the json response
	 */
	public String fetchStations() {
		return fetch(stations_endpoint, "");
	}
}
