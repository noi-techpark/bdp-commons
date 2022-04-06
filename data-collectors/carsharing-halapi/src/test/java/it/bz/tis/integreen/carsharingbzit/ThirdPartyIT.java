package it.bz.tis.integreen.carsharingbzit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import it.bz.tis.integreen.carsharingbzit.api.ApiClient;

public class ThirdPartyIT {

	private ApiClient client;
	private Properties props = new Properties(); 

	@Before
	public void setup() throws FileNotFoundException, IOException {
		URL resource = getClass().getClassLoader().getResource("application.properties");
		props.load(new FileInputStream(resource.getFile()));
		String endpoint = props.getProperty("endpoint");
		String user = props.getProperty("user");
		String password = props.getProperty("password");
		client = new ApiClient(endpoint, user, password);
	}
	@Test
	public void testStationsEndpoint() throws IOException {
		Set<String> stations = ConnectorLogic.fetchStations(client);
		assertNotNull(stations);
		assertFalse(stations.isEmpty());
	}
}
