/*
 *  Parking Forecast Connector
 *
 *  (C) 2021 NOI Techpark Südtirol / Alto Adige
 *
 *  changelog:
 *  2022-02-23  1.0 - thomas.nocker@catch-solve.tech
 */


package it.bz.noi.sta.parkingforecast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.bz.noi.sta.parkingforecast.configuration.ParkingForecstConfiguration;
import it.bz.noi.sta.parkingforecast.dto.ParkingForecastDataPoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ParkingForecastConnector {

	private static final Logger LOG = LogManager.getLogger(ParkingForecastConnector.class);

	@Autowired
	private ParkingForecstConfiguration connectorConfiguration;

	public Map<String, List<ParkingForecastDataPoint>> getParkingStationTimeseries() throws IOException {
		HttpURLConnection conn = (HttpURLConnection) (new URL(connectorConfiguration.getEndpoint())).openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		int status = conn.getResponseCode();
		if (status != 200) {
			throw new RuntimeException("could not get parking forecast data (response code was " + status + ")");
		}

		// get response
		BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
		StringBuilder response = new StringBuilder();
		String s;
		while ((s = br.readLine()) != null) {
			response.append(s);
		}
		conn.disconnect();

		// parse response

		Map<String, List<ParkingForecastDataPoint>> output = new HashMap<>();

		JsonObject timeseries;
		try {
			JsonObject jsonObject = new Gson().fromJson(response.toString(), JsonObject.class);
			timeseries = jsonObject.getAsJsonObject("timeseries");
		} catch (Exception e) {
			// throw an error in case not even the top level element cannot be extracted as expected
			LOG.warn("---");
			LOG.warn("getParkingStationTimeseries() ERROR: unparsable response:");
			LOG.warn("vvv");
			LOG.warn(response.toString());
			LOG.warn("^^^");
			LOG.warn(e.getMessage(), e);
			LOG.warn("---");
			throw new IOException("ERROR: unparsable response");
		}

		int skipped = 0;
		for (String scode : timeseries.keySet()) {
			try {
				JsonArray parkingStationTimeseriesArray = timeseries.getAsJsonArray(scode);
				List<ParkingForecastDataPoint> forecastDataList = new ArrayList<>();
				for (int i = 0; i < parkingStationTimeseriesArray.size(); i++) {
					ParkingForecastDataPoint forecastDataPoint = new ParkingForecastDataPoint();
					JsonObject forecastDataPointJson = parkingStationTimeseriesArray.get(i).getAsJsonObject();
					forecastDataPoint.setTs(extractDate(forecastDataPointJson, "ts"));
					forecastDataPoint.setLo(extractDouble(forecastDataPointJson, "lo"));
					forecastDataPoint.setMean(extractDouble(forecastDataPointJson, "mean"));
					forecastDataPoint.setHi(extractDouble(forecastDataPointJson, "hi"));
					forecastDataList.add(forecastDataPoint);
				}
				output.put(scode, forecastDataList);

			} catch (Exception e) {

				// null pointer, cast or number format exception in case the json hasn't the expected form
				// or has incompatible data types: log and skip the record
				LOG.warn("---");
				LOG.warn("getParkingStationTimeseries() ERROR: skipping unparsable record:");
				LOG.warn("vvv");
				LOG.warn(timeseries.get(scode).toString());
				LOG.warn("^^^");
				LOG.warn(e.getMessage(), e);
				LOG.warn("---");
				skipped++;
				continue;
			}
		}

		LOG.debug("getParkingStationTimeseries() OK: got data about " + output.size() + " stations, " + skipped + " skipped");

		return output;

	}

	public static Double extractDouble(JsonObject jsonObject, String prop) {
		JsonElement ret = jsonObject.get(prop);
		if (ret == null || ret.isJsonNull())
			return null;
		return ret.getAsDouble();
	}

	public static ZonedDateTime extractDate(JsonObject jsonObject, String prop) {
		JsonElement ret = jsonObject.get(prop);
		if (ret == null || ret.isJsonNull())
			return null;
		return ZonedDateTime.parse(ret.getAsString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssVV"));
	}
}
