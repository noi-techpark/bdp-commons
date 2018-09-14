package it.bz.idm.bdp.myfirstdatacollector;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class DataPusherTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	private JobScheduler js;

	private boolean doTests = false;

	@Test
	public void testEverything() {
		try {
			if ( doTests ) {
				js.pushStations();
				js.pushDataTypes();
				js.pushData();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
}
