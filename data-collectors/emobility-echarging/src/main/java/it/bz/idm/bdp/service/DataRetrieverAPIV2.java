package it.bz.idm.bdp.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.bz.idm.bdp.service.dto.ChargerDtoV2;

@Component
@PropertySource({"classpath:/META-INF/spring/types.properties","classpath:/META-INF/spring/application.properties"})
public class DataRetrieverAPIV2 {
	private HttpClientBuilder builder = HttpClients.custom();
	private CloseableHttpClient client;
	private HttpClientContext localContext;
	private HttpHost endPoint;
	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private Environment env;
	private List<ChargerDtoV2> stations;

	@PostConstruct
	private void initClient(){
		if (client==null){
			endPoint = new HttpHost(env.getRequiredProperty("endpoint.host"), env.getProperty("endpoint.port", Integer.class, 443) , ("yes").equals(env.getProperty("endpoint.ssl"))?"https":"http");
			localContext = HttpClientContext.create();
			client = builder.build();
		}
	}
	private String fetchResponseEntity(String path) {
		HttpGet get = new HttpGet(path);
		String xcallerHeader = env.getProperty("app.callerId");
		String apikey = env.getProperty("app.apikey");
		if (xcallerHeader != null)
			get.setHeader("X-Caller-ID",xcallerHeader);
		if (apikey != null)
			get.setHeader("apikey",apikey);
		get.setHeader("Accept","application/json");
		try {
			CloseableHttpResponse response = client.execute(endPoint, get, localContext);
			InputStream entity = response.getEntity().getContent();
			StringWriter writer = new StringWriter();
			IOUtils.copy(entity, writer);
			String data = writer.toString();
			response.close();
			return data;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public List<ChargerDtoV2> fetchStations() {
		if (stations == null){
			String responseEntity = fetchResponseEntity(env.getProperty("endpoint.path"));
			try {
				stations = mapper.readValue(responseEntity,new TypeReference<List<ChargerDtoV2>>() {});
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return stations;
	}
}
