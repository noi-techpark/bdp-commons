package it.bz.idm.bdp.carpooling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class ThirdPartyIT extends AbstractJUnit4SpringContextTests{

	@Autowired
	private CloseableHttpClient httpClient ;
	@Autowired
	private HttpHost webserviceEndpoint;
	
	@Value("${endpoint.path}")
	public String endpointPath;
	
	@Test
	public void testjUsers() throws IllegalStateException, IOException{
		HttpGet get = new HttpGet(endpointPath + "/jUsers.json");
		CloseableHttpResponse response = httpClient.execute(webserviceEndpoint, get);
		assertEquals(200,response.getStatusLine().getStatusCode());
		InputStream entity = response.getEntity().getContent();
		StringWriter writer = new StringWriter();
		IOUtils.copy(entity, writer);
		String data = writer.toString();
		response.close();
		assertNotNull(data);
		assertFalse(data.isEmpty());
	}
	@Test
	public void testjServices() throws IllegalStateException, IOException{
		HttpGet get = new HttpGet(endpointPath + "/jServices.json");
		CloseableHttpResponse response = httpClient.execute(webserviceEndpoint, get);
		assertEquals(200,response.getStatusLine().getStatusCode());
		InputStream entity = response.getEntity().getContent();
		StringWriter writer = new StringWriter();
		IOUtils.copy(entity, writer);
		String data = writer.toString();
		response.close();
		assertNotNull(data);
		assertFalse(data.isEmpty());
	}

}
