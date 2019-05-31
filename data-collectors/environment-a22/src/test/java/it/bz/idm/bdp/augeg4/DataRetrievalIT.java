package it.bz.idm.bdp.augeg4;


import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import it.bz.idm.bdp.augeg4.face.DataRetrieverFace;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class DataRetrievalIT extends AbstractJUnit4SpringContextTests {

	@Autowired
	private DataRetrieverFace dr;

	@Test
	@Ignore
	public void testEverything() {
		try {
			//List<AugeG4FromAlgorabDataDto> data = dr.fetchData();
			//assertEquals(0, data.size());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
}
