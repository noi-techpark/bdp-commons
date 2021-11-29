package info.datatellers.appatn.tenminutes;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class ConfigIT extends AbstractJUnit4SpringContextTests {
	
	private final String[] resources = {
			"odp.url.stations.tenminutes",
			"odp.url.stations.tenminutes.key",
			"odh.station.type",
			"odh.station.origin",
			"odh.station.projection",
			"odp.unit.description.tenminutes",
			"odp.unit.rtype.tenminutes",
			"odp.unit.availability.tenminutes",
			"odp.unit.description.tenminutes.availability",
			"odp.unit.rtype.tenminutes.availability"
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
