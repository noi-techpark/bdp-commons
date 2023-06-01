// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.bz.idm.bdp.forecast.domain.ForecastStep;
import it.bz.idm.bdp.forecast.domain.ParkingForecasts;

@Service
public class PredictionRetriever {

	
	@Value("${prediction_url_time}")
	private String prediction_url_time;
	
	@Value("${prediction_url_station}")
	private String prediction_url_station;
	
	
	private static ObjectMapper mapper = new ObjectMapper(); 


	public ForecastStep predict(Integer minutes){
		URL url;
		try {
			ForecastStep value = null;
			url = new URL(prediction_url_time + minutes);
			URLConnection connection = url.openConnection();
			HttpURLConnection httpConnection = (HttpURLConnection) connection;
			if (httpConnection.getResponseCode() != 403)
				value = mapper.readValue(httpConnection.getInputStream(),ForecastStep.class);
			return value;
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public ParkingForecasts predict(String pid){
		URL url;
		try {
			ParkingForecasts value = null;
			url = new URL(prediction_url_station+pid);
			URLConnection connection = url.openConnection();
			HttpURLConnection httpConnection = (HttpURLConnection) connection;
			if (httpConnection.getResponseCode() != 403)
				value = mapper.readValue(connection.getInputStream(),ParkingForecasts.class);
			return value;
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
