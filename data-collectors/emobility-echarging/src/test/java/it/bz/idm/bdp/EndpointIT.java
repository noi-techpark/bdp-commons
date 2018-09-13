package it.bz.idm.bdp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import it.bz.idm.bdp.service.DataRetrieverAPIV2;
import it.bz.idm.bdp.service.dto.ChargerDtoV2;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class EndpointIT extends AbstractJUnit4SpringContextTests{
	
	@Autowired
	public DataRetrieverAPIV2 retriever;
	
	
	@Test
	public void testFetchStations(){
		List<ChargerDtoV2> data = retriever.fetchStations();
		assertNotNull(data);
		assertTrue(data.size()>0);
		assertNotNull(data.get(0).getId());
		assertFalse(data.get(0).getId().isEmpty());
		assertFalse(data.get(0).getChargingPoints().isEmpty());
		assertFalse(data.get(0).getChargingPoints().get(0).getOutlets().isEmpty());
	}

}
