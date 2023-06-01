// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package info.datatellers.appatn.tenminutes;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import junit.framework.TestSuite;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import info.datatellers.appatn.tenminutes.DataFetcher;


@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class DataFetcherIT extends AbstractJUnit4SpringContextTests {
	
	DataFetcher fetcher = new DataFetcher();
	
	
	// temporary way to list station and sensor id. will become dynamic.
	String[] stations = {"27"};
	String[] sensors = {"4", "7", "8", "9", "52", "57", "58", "59", "60", "61", "62", "63"};
	
	@Test
	public void testGetStations(){
		try {
			JsonObject station = (JsonObject) new JsonParser().parse(fetcher.fetchStations());
			String id = ((JsonObject) ((JsonObject) ((JsonArray) station.get("features")).get(0))
					.get("properties")).get("id").getAsString();
			Assert.assertEquals(Arrays.asList(stations).contains(id), true);
		} catch (JsonParseException e) {
			Assert.fail("Unexpected value returned from station API call, " + e.getMessage());
		} catch (ClassCastException e) {
			Assert.fail("Unexpected JSON format (possibly it has been changed), " + e.getMessage());
		} catch (AssertionError e) {
			TestSuite.warning("New station id has been added, a code update needed to manage the update");
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetSensors() {
		try {
			for (String stationId : stations) {
				JsonArray sensor = (JsonArray) new JsonParser().parse(fetcher.fetchSensors(stationId));
			}
		} catch (JsonParseException e) {
			Assert.fail("Unexpected value returned from sensor API call, " + e.getMessage());
		} catch (ClassCastException e) {
			Assert.fail("Unexpected JSON format (possibly it has been changed), " + e.getMessage());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetData() {
		try {
			for (String stationId : stations) {
				for (String sensorId : sensors) {
					JsonObject data = (JsonObject) new JsonParser().parse(fetcher.fetchData(stationId, sensorId));
				}
			}
		} catch (JsonParseException e) {
			Assert.fail("Unexpected value returned from sensor API call, " + e.getMessage());
		} catch (ClassCastException e) {
			Assert.fail("Unexpected JSON format (possibly it has been changed), " + e.getMessage());
		} catch (Exception e){
			Assert.fail(e.getMessage());
		}
	}
}
