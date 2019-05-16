package it.bz.idm.bdp.carpooling;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.bz.idm.bdp.carpooling.dto.generated.Hub;
import it.bz.idm.bdp.carpooling.dto.generated.JServices;
import it.bz.idm.bdp.carpooling.dto.generated.JUsers;
import it.bz.idm.bdp.carpooling.dto.generated.User;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;



@Component
public class DataRetriever {

	private static final String DATA_ORIGIN = "FLOOTA";

	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private SimpleDateFormat dateFormatter;

	@Autowired
	private CloseableHttpClient httpClient;

	@Autowired
	public HttpHost webserviceEndpoint;

	@Value("${endpoint.path}")
	public String endpointPath;

	private static DataTypeDto co2 		= new DataTypeDto("avoided-co2","kg","avoided carbon dioxide consumption","Instantaneous"),
			registeredUsers = new DataTypeDto("carpooling-users",null,"number of registered users in the carpooling system","Instantaneous"),
			confirmedTrips  = new DataTypeDto("carpooling-trips",null,"Number of car pooling trips","Instantaneous"),
			drivers			= new DataTypeDto("carpooling-drivers",null,"Number of drivers in the car pooling system","Instantaneous"),
			passengers		= new DataTypeDto("carpooling-passenger",null,"Number of passenger in the car pooling system","Instantaneous"),
			distance		= new DataTypeDto("traveled-distance","km","Number of km traveled","Instantaneous");


	public StationList getHubIds() {
		String stations = getResponseEntity(endpointPath + "/jServices.json");
		StationList dtos = new StationList();
		try {
			JServices response = mapper.readValue(stations, JServices.class);
			for (Hub hub: response.getServices().getHub()){
				Date hubExpires = dateFormatter.parse(hub.getAvailability());
				if (hubExpires.after(new Date())){
					StationDto dto = new StationDto();
					dto.setId(hub.getId().toString());
					dto.setName(hub.getName());
					Double parsedLon, parsedLat;
					parsedLon = hub.getLongitude();
					parsedLat = hub.getLatitude();
					dto.setLongitude(parsedLon);
					dto.setLatitude(parsedLat);
					dto.setStationType("CarpoolingHub");
					dto.setOrigin(DATA_ORIGIN);
					dto.getMetaData().put("address", hub.getAddress());
					dto.getMetaData().put("city", hub.getCity());
					dtos.add(dto);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dtos;
	}
	public StationList getUsers() {
		String users = getResponseEntity(endpointPath + "/jUsers.json");
		StationList dtos = new StationList();
		try {
			JUsers response = mapper.readValue(users, JUsers.class);
			for (User user: response.getUser()){
				Date userExpires = dateFormatter.parse(user.getUserAvailability());
				if (userExpires.after(new Date())){
					StationDto dto = new StationDto();
					dto.setId(user.getId().toString());
					dto.setOrigin(DATA_ORIGIN);
					dto.setName(user.getUserName());
					dto.getMetaData().put("gender", user.getUserGender().charAt(0));
					dto.getMetaData().put("type",user.getUserType());
					dto.getMetaData().put("pendular",user.getUserPendular());
					dto.setParentStation(user.getTripToId().toString());
					dto.getMetaData().put("arrival",user.getTripArrival());
					dto.getMetaData().put("departure",user.getTripDeparture());
					dto.getMetaData().put("tripFrom",user.getTripFrom());

					dto.setStationType("CarpoolingUser");
					dto.setLongitude(user.getTripLongitude());
					dto.setLatitude(user.getTripLatitude());
					dtos.add(dto);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dtos;
	}
	private String getResponseEntity(String path) {
		HttpGet get = new HttpGet(path);
		try {
			CloseableHttpResponse response = httpClient.execute(webserviceEndpoint, get);
			if (response.getStatusLine().getStatusCode() != 200)
				throw new IllegalAccessError("HTTP response code not 200");
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
	public List<DataTypeDto> getDataTypes() {
		List<DataTypeDto> dtos = new ArrayList<DataTypeDto>();
		dtos.addAll(getStatTypes());
		return dtos;
	}
	private static List<? extends DataTypeDto> getStatTypes() {
		List<DataTypeDto> dtos = new ArrayList<DataTypeDto>();
		dtos.add(co2);
		dtos.add(registeredUsers);
		dtos.add(confirmedTrips);
		dtos.add(drivers);
		dtos.add(passengers);
		dtos.add(distance);
		return dtos;
	}
	public JServices getCurrentStats() {
		String stations = getResponseEntity(endpointPath + "/jServices.json");
		try {
			JServices response = mapper.readValue(stations, JServices.class);
			return response;
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public StationList generateOriginStation() {
		return new StationList() {{
			StationDto dto =new StationDto("innovie", "Car pooling Innovie", null, null);
			dto.setOrigin("FLOOTA");
			add(dto);
			}};
	}

}
