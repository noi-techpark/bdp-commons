// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package info.datatellers.appatn.tenminutes;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import info.datatellers.appatn.helpers.CoordinateHelper;
import info.datatellers.appatn.tenminutes.DataFetcher;
import info.datatellers.appatn.tenminutes.DataPusher;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.StationDto;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class DataPusherIT extends AbstractJUnit4SpringContextTests {
	
	DataPusher pusher = new DataPusher();
	
	private ResourceBundle rb = ResourceBundle.getBundle("config");
	
	DataFetcher fetcher = new DataFetcher();
	
	// temporary way to list station and sensor id. will become dynamic.
	String[] stations = {"27"};
	String[] sensors = {"4", "7", "8", "9", "52", "57", "58", "59", "60", "61", "62", "63"};
	
	@Test
	public void testTypology() {
		Assert.assertEquals(pusher.initIntegreenTypology(), rb.getString("odh.station.type"));
	}
	
	@Test
	public void testMapStation() {
		try {
			JsonObject stationJson = (JsonObject) new JsonParser().parse(fetcher.fetchStations());
			StationDto station = pusher.mapStation(stationJson);
			
			String stationId = rb.getString("odh.station.origin") + "_"
					+ ((JsonObject) ((JsonObject) ((JsonArray) stationJson.get("features")).get(0))
							.get("properties")).get("id").getAsString();
			String stationName = ((JsonObject) ((JsonObject) ((JsonArray) stationJson.get("features")).get(0))
							.get("properties")).get("name").getAsString();
			Double easting = Double.parseDouble(
					((JsonObject) ((JsonObject) ((JsonArray) stationJson.get("features")).get(0))
					.get("properties")).get("lon").getAsString());
			Double northing = Double.parseDouble(
					((JsonObject) ((JsonObject) ((JsonArray) stationJson.get("features")).get(0))
					.get("properties")).get("lat").getAsString());
			
			Assert.assertEquals(station.getId(), stationId);
			Assert.assertEquals(station.getName(), stationName);
			try {
				Assert.assertEquals(
						station.getLatitude(),
						Double.valueOf(new CoordinateHelper().UTMtoDecimal(32, easting, northing)[1])
				);
				Assert.assertEquals(
						station.getLongitude(),
						Double.valueOf(new CoordinateHelper().UTMtoDecimal(32, easting, northing)[0])
				);
			} catch (AssertionError e) { // if coordinates are stored in the original format and not converted to EPSG:4326 by the datacollector
				Assert.assertEquals(
						station.getLatitude(),
						Double.valueOf(northing)
				);
				Assert.assertEquals(
						station.getLongitude(),
						Double.valueOf(easting)
				);
			}
			
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testMapDataType() {
		for (String station : stations) {
			JsonArray sensorsJson = (JsonArray) new JsonParser().parse(fetcher.fetchSensors(station));
			for(JsonElement sensorElem : sensorsJson) {
				try {
					JsonObject sensor = (JsonObject) sensorElem;
					HashMap<String, DataTypeDto> sensorMap = pusher.mapDataType((JsonObject) sensor);
					for(Map.Entry<String, DataTypeDto> mappedSensor : sensorMap.entrySet()){
						if(mappedSensor.getKey().contains("_I")) {
							Assert.assertEquals(mappedSensor.getValue().getName(), sensor.get("description").getAsString() + rb.getString("odp.unit.availability.tenminutes"));
							Assert.assertEquals(mappedSensor.getValue().getDescription(), rb.getString("odp.unit.description.tenminutes.availability"));
							Assert.assertEquals(mappedSensor.getValue().getRtype(), rb.getString("odp.unit.rtype.tenminutes.availability"));
							Assert.assertEquals(mappedSensor.getValue().getUnit(), "%");
						} else {
							Assert.assertEquals(mappedSensor.getValue().getName(), sensor.get("description").getAsString());
							Assert.assertEquals(mappedSensor.getValue().getDescription(), rb.getString("odp.unit.description.tenminutes"));
							Assert.assertEquals(mappedSensor.getValue().getRtype(), rb.getString("odp.unit.rtype.tenminutes"));
							Assert.assertEquals(mappedSensor.getValue().getUnit(), sensor.get("uom").getAsString());
						}
					}
				} catch (ClassCastException e) {
					Assert.fail("Unexpected sensor format. Expected JsonObject, received " + sensorElem);
				} catch (Exception e) {
					Assert.fail(e.getMessage());
				}
			}
		}
	}
}