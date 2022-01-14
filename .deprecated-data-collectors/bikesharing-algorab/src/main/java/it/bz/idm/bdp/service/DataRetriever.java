package it.bz.idm.bdp.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;
import it.bz.idm.bdp.dto.bikesharing.DataResult;
import it.bz.idm.bdp.dto.bikesharing.DataResultR;
import it.bz.idm.bdp.dto.bikesharing.MetaDataBicylceR;
import it.bz.idm.bdp.dto.bikesharing.MetaDataResultR;
import it.bz.idm.bdp.dto.bikesharing.StationParameter;
import it.bz.idm.bdp.dto.bikesharing.StationResponse;
import it.bz.idm.bdp.dto.bikesharing.StationResponseR;
import it.bz.idm.bdp.dto.bikesharing.TypeParameter;
import it.bz.idm.bdp.dto.bikesharing.TypeResultR;
import it.bz.idm.bdp.dto.bikesharing.TypesResult;

@Component
@PropertySource({"classpath:/META-INF/spring/types.properties","classpath:/META-INF/spring/application.properties"})
public class DataRetriever{
	private static final Integer AQUISITION_INTERVAL = 300;
	private static final String DATA_PROVIDER_KEY = "endpoint.dataprovider";
	private static final String BIKE_ABBR = "bike";
	private static final String BIKESTATION_ABBR = "bikestation";
	private static final String STATUS_OK = "OK";
	private HttpClientBuilder builder = HttpClients.custom();
	private CloseableHttpClient client;
	private HttpClientContext localContext;
	private HttpHost target;
	private ObjectMapper mapper = new ObjectMapper();
	private final static DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	@Autowired
	public Environment environment;

	@PostConstruct
	private void initClient(){
		if (client==null){
	        CredentialsProvider credsProvider = new BasicCredentialsProvider();
	        target = new HttpHost(environment.getProperty("endpoint.host"),environment.getProperty("endpoint.port", Integer.class,80), "http");
	        credsProvider.setCredentials(
	                new AuthScope(target.getHostName(), target.getPort()),
	                new UsernamePasswordCredentials(environment.getProperty("endpoint.user"),environment.getProperty("endpoint.password")));
	        client = builder.setDefaultCredentialsProvider(credsProvider).build();
	        AuthCache authCache = new BasicAuthCache();
	        BasicScheme basicAuth = new BasicScheme();
	        localContext = HttpClientContext.create();
	        localContext.setAuthCache(authCache);
	        authCache.put(target, basicAuth);
		formatter.setTimeZone(TimeZone.getTimeZone(environment.getProperty("default_timezone")));
		}
	}
	public Map<String, Integer> retrieveStationData(String identifier) throws JsonParseException, JsonMappingException, IOException {
		String response = getResponseEntity("/TIS/ws/get_data_station?ID="+identifier);
		Map<String,Integer> parameters = new HashMap<String, Integer>();
		if (response!=null ){
			DataResultR resultResponse = mapper.readValue(response, DataResultR.class);
			if (resultResponse != null && resultResponse.getResponse()!=null && STATUS_OK.equals(resultResponse.getResponse().getStatus())){
				DataResult result = resultResponse.getResponse();
				for (StationParameter record:result.getResult()){
					String key = record.getParameter();
					Integer value = Integer.valueOf(record.getValue().toString());
					parameters.put(key, value);
				}
			}
		}
		return parameters;
	}
	public List<String> retrieveStationIds() throws JsonParseException, JsonMappingException, IOException{
		String stations = getResponseEntity("/TIS/ws/get_station_IDs");
		StationResponseR deserializedEntity = mapper.readValue(stations, StationResponseR.class);
		if (deserializedEntity != null && deserializedEntity.getResponse()!=null && STATUS_OK.equals(deserializedEntity.getResponse().getStatus()))
			return deserializedEntity.getResponse().getResult();
		return new ArrayList<String>();
	}

	public StationList retrieveStations() throws JsonParseException, JsonMappingException, IllegalStateException, IOException{
		List<String> stationIds = retrieveStationIds();
		StationList dtos = new StationList();
		if (stationIds!=null){
			for(String identifier:stationIds){
				String metaResponse = getResponseEntity("/TIS/ws/get_metadata_station?ID="+identifier);
				MetaDataResultR deserializedMeta = mapper.readValue(metaResponse, MetaDataResultR.class);
				if (deserializedMeta != null && deserializedMeta.getResponse() != null && STATUS_OK.equals(deserializedMeta.getResponse().getStatus())){
					List<StationParameter> result = deserializedMeta.getResponse().getResult();
					if (result.isEmpty())
						throw new IllegalStateException("Bikestation '"+identifier+"' returned no metadata");
					@SuppressWarnings("unchecked")
					List<String> coordinates = (List<String>) result.get(2).getValue();
					Integer status = Integer.valueOf(result.get(3).getValue().toString());
					Double lon = Double.valueOf(coordinates.get(0));
					Double lat = Double.valueOf(coordinates.get(1));
					StationDto dto = new StationDto(BIKESTATION_ABBR+identifier, result.get(1).getValue().toString(), lon, lat);
					dto.setStationType("BikeSharingStation");
					dto.getMetaData().put("status", status);
					dto.setOrigin(environment.getProperty(DATA_PROVIDER_KEY));
					dtos.add(dto);
				}
			}
		}
		return dtos;

	}
	public StationList retrieveBicycles() throws JsonParseException, JsonMappingException, IllegalStateException, IOException{
		StationList dtos = new StationList();
		String bicycles = getResponseEntity("/TIS/ws/get_bicycle_IDs");
		StationResponseR deserializedEntity = mapper.readValue(bicycles, StationResponseR.class);
		if (deserializedEntity != null && deserializedEntity.getResponse()!=null && STATUS_OK.equals(deserializedEntity.getResponse().getStatus())){
			StationResponse stationsResponse = deserializedEntity.getResponse();
			for(String identifier:stationsResponse.getResult()){
				String metaResponse = getResponseEntity("/TIS/ws/get_metadata_bicycle?ID="+identifier);
				MetaDataBicylceR deserializedResponse = mapper.readValue(metaResponse, MetaDataBicylceR.class);
				if (deserializedResponse != null && deserializedResponse.getResponse()!= null && deserializedResponse.getResponse().getResult()!=null){

					List<StationParameter> result = deserializedResponse.getResponse().getResult();
					if (result.isEmpty())
						throw new IllegalStateException("Bicycle '"+identifier+"' returned no metadata");
					String italianName = result.get(1).getValue().toString();
					String type = environment.getProperty(italianName.replace(" ",""),italianName);

					String name = type+"("+identifier+")";
					Integer state = Integer.valueOf(result.get(2).getValue().toString());
					Integer inStoreHouse = Integer.valueOf(result.get(3).getValue().toString());
					String bikestation =BIKESTATION_ABBR+result.get(4).getValue().toString();
					StationDto dto = new StationDto();
					dto.setParentStation(bikestation);
					dto.setId(BIKE_ABBR+identifier);
					dto.setName(name);
					dto.setStationType("BikeSharingBike");
					dto.setOrigin(environment.getProperty(DATA_PROVIDER_KEY));
					dto.getMetaData().put("state", state);
					dto.getMetaData().put("instorehouse", inStoreHouse);
					dto.getMetaData().put("type", type);
					dtos.add(dto);
				}
			}
		}
		return dtos;
	}
	public DataMapDto<RecordDtoImpl> retrieveBicyclesData() throws JsonParseException, JsonMappingException, IllegalStateException, IOException, ParseException{
		DataMapDto<RecordDtoImpl> dtos = new DataMapDto<>();
		String bicycles = getResponseEntity("/TIS/ws/get_bicycle_IDs");
		StationResponseR deserializedEntity = mapper.readValue(bicycles, StationResponseR.class);
		StationResponse response = deserializedEntity.getResponse();
		if (deserializedEntity != null && deserializedEntity.getResponse()!=null && STATUS_OK.equals(response.getStatus())){
			StationResponse stationsResponse = response;
			for(String identifier:stationsResponse.getResult()){
				DataMapDto<RecordDtoImpl> typeBranch = new DataMapDto<>();
				String metaResponse = getResponseEntity("/TIS/ws/get_metadata_bicycle?ID="+identifier);
				MetaDataBicylceR deserializedResponse = mapper.readValue(metaResponse, MetaDataBicylceR.class);
				if (deserializedResponse != null && deserializedResponse.getResponse()!= null && deserializedResponse.getResponse().getResult()!=null){
					List<StationParameter> result = deserializedResponse.getResponse().getResult();
					Long timestamp = formatter.parse(response.getInfo().getUpdated()).getTime();
					Double inStoreHouse = Double.valueOf(result.get(3).getValue().toString());
					String type = "availability";	// environment.getProperty(italianName.replace(" ",""),italianName);
					SimpleRecordDto simpleRecordDto = new SimpleRecordDto(timestamp,inStoreHouse);
					simpleRecordDto.setPeriod(AQUISITION_INTERVAL);
					DataMapDto<RecordDtoImpl> recordsBranch = typeBranch.getBranch().get(type);
					if (recordsBranch != null) {
						recordsBranch.getData().add(simpleRecordDto);
					} else{

						List<RecordDtoImpl> records = new ArrayList<RecordDtoImpl>();
						records.add(simpleRecordDto);
						recordsBranch = new DataMapDto<RecordDtoImpl>();
						recordsBranch.setData(records);
						typeBranch.getBranch().put(type, recordsBranch);
					}
				}
				if (dtos.getBranch().get(BIKE_ABBR+identifier) == null)
					dtos.getBranch().put(BIKE_ABBR+identifier, typeBranch);
			}
		}
		return dtos;
	}
	public List<DataTypeDto> retrieveDataTypes() throws JsonParseException, JsonMappingException, IllegalStateException, IOException{
		String response = getResponseEntity("/TIS/ws/get_data_types_station");
		List <DataTypeDto> list = new ArrayList<DataTypeDto>();
		if (response!=null ){
			TypeResultR metaResultResponse = mapper.readValue(response, TypeResultR.class);
			if (metaResultResponse != null && metaResultResponse.getResponse()!=null && STATUS_OK.equals(metaResultResponse.getResponse().getStatus())){
				TypesResult result = metaResultResponse.getResponse();
				for (TypeParameter it : result.getResult()){
					DataTypeDto dto = new DataTypeDto();
					dto.setName(it.getParameter());
					list.add(dto);
				}
			}
		}
		return list;
	}
	public DataMapDto<RecordDtoImpl> retrieveCurrentState() throws JsonParseException, JsonMappingException, IOException, ParseException {
		List<String> stationIds = retrieveStationIds();
		DataMapDto<RecordDtoImpl> dtos = new DataMapDto<>();
		if (stationIds!=null)
			for (String station:stationIds){
				String realtimeData = getResponseEntity("/TIS/ws/get_data_station?ID="+station);
				DataResultR deserializedEntity = mapper.readValue(realtimeData, DataResultR.class);
				DataMapDto<RecordDtoImpl> typeBranch = new DataMapDto<>();
				if(deserializedEntity!=null && deserializedEntity.getResponse()!=null && STATUS_OK.equals(deserializedEntity.getResponse().getStatus())){
					DataResult response = deserializedEntity.getResponse();
					Long timestamp = formatter.parse(response.getInfo().getUpdated()).getTime();
					for (StationParameter parameter: response.getResult()){
						String type = environment.getProperty(parameter.getParameter().replace(" ", ""));
						if (type == null && parameter.getParameter().equals("bici disponibili"))
							type = DataTypeDto.NUMBER_AVAILABE;
						Double value = Double.valueOf(parameter.getValue().toString());
						SimpleRecordDto simpleRecordDto = new SimpleRecordDto(timestamp,value);
						simpleRecordDto.setPeriod(AQUISITION_INTERVAL);
						DataMapDto<RecordDtoImpl> recordBranch = typeBranch.getBranch().get(type);
						if (recordBranch != null) {
							recordBranch.getData().add(simpleRecordDto);
						} else{
							List<RecordDtoImpl> records = new ArrayList<RecordDtoImpl>();
							records.add(simpleRecordDto);
							recordBranch = new DataMapDto<RecordDtoImpl>();
							recordBranch.setData(records);
							typeBranch.getBranch().put(type, recordBranch);
						}
					}
				}

				String key = BIKESTATION_ABBR+station;
				if (dtos.getBranch().get(key) == null)
					dtos.getBranch().put(key, typeBranch);
			}
		return dtos;
	}

	private String getResponseEntity(String path) {
		HttpPost post = new HttpPost(path);
        try {
			CloseableHttpResponse response = client.execute(target, post, localContext);
			if (response.getStatusLine().getStatusCode()==302)
				throw new IllegalAccessError("Username password authentication failed");
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
		throw new IllegalStateException("Unable to get Response data");
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
		return new PropertySourcesPlaceholderConfigurer();
	}

}
