package info.datatellers.appatn.dieciminuti;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class ConfigTest extends AbstractJUnit4SpringContextTests {
	
	private final String[] resources = {
			"odp.url.stations.10minuti",
			"odp.url.stations.10minuti.key",
			"odh.station.type",
			"odh.station.origin",
			"odh.station.projection",
			"odp.unit.description.10minuti",
			"odp.unit.rtype.10minuti",
			"odp.unit.availability.10minuti",
			"odp.unit.description.10minuti.availability",
			"odp.unit.rtype.10minuti.availability"
	};
	
	private ResourceBundle rb;
	
	@Test
	public void testConfigExists() {
		try {
			this.rb = ResourceBundle.getBundle("config");
		} catch (MissingResourceException e) {
			Assert.fail("Missing or non-readable resource bundle. Add config.properties file");
		}
		
	}
	
	@Test
	public void testConfigValues() {
		this.rb = ResourceBundle.getBundle("config");
		for (String resource : resources) {
			try {
				rb.getString(resource);
			} catch (MissingResourceException e) {
				Assert.fail("Missing " + resource + " value");
			} catch (ClassCastException e) {
				Assert.fail("Unexpected value for " + resource + " - it must be a string");
			}
		}
	}

}
