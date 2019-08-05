package it.bz.odh.spreadsheets;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import it.bz.odh.spreadsheets.JobScheduler;
@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })

public class GoogleSpreadsheetIT extends AbstractJUnit4SpringContextTests{

	@Autowired
	private JobScheduler scheduler;

	@Test
	public void testListMapping() {
		scheduler.syncData();
	}

}
