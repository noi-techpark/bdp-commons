package it.bz.idm.bdp.carpooling;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import it.bz.idm.bdp.carpooling.DataRetriever;
import it.bz.idm.bdp.dto.StationDto;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class WebServiceIT extends AbstractJUnit4SpringContextTests{
	
	@Autowired
	private DataRetriever retriever;
	
	@Test
	public void testRetrieveServices(){
		List<StationDto> hubIds = retriever.getHubIds();
		assertNotNull(hubIds);
		assertFalse(hubIds.isEmpty());
		assertNotNull(hubIds.get(0).getName());
		assertNotNull(hubIds.get(0).getId());
		assertNotNull(hubIds.get(0).getLatitude());
		assertNotNull(hubIds.get(0).getLongitude());
		assertNotNull(hubIds.get(0).getOrigin());
	}
	@Test
	public void testRetrieveUsers(){
		List<StationDto> userIds = retriever.getUsers();
		assertNotNull(userIds);
		assertFalse(userIds.isEmpty());
		assertNotNull(userIds.get(0).getName());
		assertNotNull(userIds.get(0).getId());
		assertNotNull(userIds.get(0).getOrigin());
	}

}
