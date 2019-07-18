package it.bz.idm.bdp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import it.bz.idm.bdp.spreadsheets.GoogleSpreadSheetDataFetcher;
@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })

public class GoogleSpreadsheetIT extends AbstractJUnit4SpringContextTests{

	@Autowired
	private GoogleSpreadSheetDataFetcher spreadsheetClient;


}
