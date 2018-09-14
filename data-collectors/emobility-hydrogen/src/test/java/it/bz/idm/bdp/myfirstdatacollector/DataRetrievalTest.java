package it.bz.idm.bdp.myfirstdatacollector;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import it.bz.idm.bdp.myfirstdatacollector.dto.CoolDataDto;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class DataRetrievalTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	private DataRetrieval dr;

	@Test
	public void testEverything() {
		try {
			List<CoolDataDto> data = dr.fetchData();
			assertEquals(4, data.size());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
}
