package it.bz.idm.bdp.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.bz.idm.bdp.service.dto.ChargerDtoV2;

@Component
@PropertySource({ "classpath:/META-INF/spring/application.properties" })
public class DataRetrieverAPIV2 {

	private static final Logger LOG = LoggerFactory.getLogger(DataRetrieverAPIV2.class);

	private HttpClientBuilder builder = HttpClients.custom();
	private CloseableHttpClient client;
	private HttpClientContext localContext;
	private HttpHost endPoint;
	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private Environment env;

	@PostConstruct
	private void initClient() {
		if (client == null) {
			endPoint = new HttpHost(env.getRequiredProperty("endpoint_host"),
					env.getProperty("endpoint_port", Integer.class, 443),
					("yes").equals(env.getProperty("endpoint_ssl")) ? "https" : "http");
			localContext = HttpClientContext.create();
			client = builder.build();
		}
	}

	private String fetchResponseEntity(String path) {
		HttpGet get = new HttpGet(path);
		String xcallerHeader = env.getProperty("app_callerId");
		String apikey = env.getProperty("app_apikey");
		if (xcallerHeader != null)
			get.setHeader("X-Caller-ID", xcallerHeader);
		if (apikey != null)
			get.setHeader("apikey", apikey);
		get.setHeader("Accept", "application/json");
		try {
			CloseableHttpResponse response = client.execute(endPoint, get, localContext);
			InputStream entity = response.getEntity().getContent();
			StringWriter writer = new StringWriter();
			IOUtils.copy(entity, writer, StandardCharsets.UTF_8);
			String data = writer.toString();
			response.close();
			return data;
		} catch (IOException e) {
			LOG.error("error occurred during fetching stations with message: {}", e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public List<ChargerDtoV2> fetchStations() {
		List<ChargerDtoV2> stations;

		String responseEntity = fetchResponseEntity(env.getProperty("endpoint_path"));
		try {
			stations = mapper.readValue(responseEntity, new TypeReference<List<ChargerDtoV2>>() {
			});
			return stations;
		} catch (IOException e) {
			LOG.error("error occurred during mapping stations with message: {}", e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
}
