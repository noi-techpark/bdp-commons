package info.datatellers.appatn.dieciminuti;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataFetcher {

	private final ResourceBundle rb = ResourceBundle.getBundle("config");

	private static final Logger LOG = LogManager.getLogger(DataFetcher.class.getName());


	private final String endpoint = rb.getString("odp.url.stations.10minuti");
	private final String stations_endpoint =
			endpoint + "stations?key=" +
			rb.getString("odp.url.stations.10minuti.key");

	private final String observations_endpoint =
			endpoint + "observations?key=" +
			rb.getString("odp.url.stations.10minuti.key");

	private final String sensors_endpoint =
			endpoint + "sensors?key=" +
			rb.getString("odp.url.stations.10minuti.key");

	/**
	 * fetches measurements from observations_endpoint
	 * getting station_ID, type_ID, timestamp and value.
	 */

	private String fetch(String endpoint, String parameters) {
		URL website;
		URLConnection connection;
		BufferedReader in;
		StringBuilder response = new StringBuilder();
		String inputLine;
		try {
			website = new URL(endpoint+parameters);
			LOG.debug("Endpoint requested: {}", website);
			try {
				connection = website.openConnection();
				String responseCode = connection.getHeaderFields().get(null).get(0);
				if(responseCode.contains("200")) {
					try {
						in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF8"));
						while ((inputLine = in.readLine()) != null)
							response.append(inputLine);

						in.close();
					} catch (UnsupportedEncodingException e) {
						LOG.error("UnsupportedEncodingException, {}", e.getMessage());
						e.printStackTrace();
					} catch (IOException e) {
						LOG.error("IOException, {}", e.getMessage());
						e.printStackTrace();
					}
				} else {
					LOG.error("Failed API call");
					LOG.debug("Called " + website + " and received HTTP code {}", responseCode);
				}

			} catch (IOException e) {
				LOG.error("IOException, {}", e.getMessage());
				e.printStackTrace();
			}

		} catch (MalformedURLException e) {
			LOG.error("MalformedURLException, {}", e.getMessage());
			e.printStackTrace();
		}

		return response.toString();
	}

	public String fetchData(String stationId, String measurements) {
		return fetch(observations_endpoint, "&stations=" + stationId + "[" + measurements + "]");
	}

	public String fetchSensors(String stationId) {
		return fetch(sensors_endpoint, "&stations=" + stationId);
	}

	public String fetchStations() {
		return fetch(stations_endpoint, "");
	}
}
