package it.bz.idm.bdp.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;
import it.bz.idm.bdp.dto.meteo.MeteoStationDto;
import it.bz.idm.bdp.dto.meteo.SegmentDataPointDto;
import it.bz.idm.bdp.dto.meteo.SegmentDto;
import it.bz.tis.zrx2json.MeteoStationDataPoint;
import it.bz.tis.zrx2json.Meteostation;
import it.bz.tis.zrx2json.Zrx2json;

@Component
@PropertySource({"classpath:/META-INF/spring/types.properties","classpath:/META-INF/spring/application.properties"})
public class MeteoUtil {
	private static final String GEOMTRY_CRS = "geometry_crs";
	private static final String ZRX_ENDPOINT_KEY = "zrx_endpoint";
	private static final String[] CUSTOM_PROVINCE_BZ_PARAMETER = new String[] {"SRW","SHW","SEINZUG6926","SLAGE0","SPNP","SFGEBIET","DAYSTART"};
	private static final String AREA = "SFGEBIET";
	private static final String ZEUS_ID = "SANR";
	public static final String NAME = "SNAME";
	private static final String Y = "SHW";
	private static final String X = "SRW";
	public static final Object CNAME = "CNAME";
	public static final Object CUNIT = "CUNIT";
	public static final Object VALUE_TYPE = "RTYPE";
	public static final Integer PERIOD = 600;
	public static final String CMW = "CMW";


	private static NumberFormat numberFormatter = NumberFormat.getInstance(Locale.GERMAN);
	private static final DateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmmss");

	@Autowired
	private MeteoPusher pusher;

	@Autowired
	private Environment environment;

	public String getMeteoDataAsJsonString(){
		URL url;
		BufferedReader bufferedReader=null;
		try {
			url = new URL(environment.getProperty(ZRX_ENDPOINT_KEY));
			URLConnection connection = url.openConnection();
			bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(),"ISO-8859-15"));
			StringBuffer buffer = new StringBuffer();
			String line;
			while((line=bufferedReader.readLine())!=null){
				buffer.append(line);
				buffer.append("\n");
			}
			String zrx = buffer.toString();
			if (zrx != null){
				String json = Zrx2json.parse(zrx);
				return json;
			}
			bufferedReader.close();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<SegmentDto> getSegmentDtosFromJson(String json){
		List<SegmentDto> segments = new ArrayList<SegmentDto>();
		List<Meteostation> stations = parseJson(json);
		if (stations!=null)
			for (Meteostation station : stations){
				List<SegmentDataPointDto> data = new ArrayList<SegmentDataPointDto>();
				for (MeteoStationDataPoint point:station.getDataPoints()){
					SegmentDataPointDto dto;
					try {
						dto = new SegmentDataPointDto(point.getDataPoint(), point.getComment(), point.getDate());
					} catch (ParseException e) {
						continue;
					}
					data.add(dto);
				}
				Map<String, String> optionalParametersFromComments = getOptionalParametersFromComments(station.getComments());
				station.getMetaData().putAll(optionalParametersFromComments);
				SegmentDto segment = new SegmentDto(station.getMetaData(),station.getComments(),data);
				segments.add(segment);
			}
		return segments;
	}
	private List<Meteostation> parseJson(String json) {
		ObjectMapper mapper = new ObjectMapper();
		List<Meteostation> stations = null;
		try {
			stations = mapper.readValue(json,new TypeReference<List<Meteostation>>() {});
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stations;
	}
	private Map<String, String> getOptionalParametersFromComments(List<String> comments) {
		Map<String,String> parameterResult = new HashMap<String, String>();
		String params[];
		for (String headerLine: comments){
			params = headerLine.split("(\\|\\*\\|)|(;\\*;)");
			for (String param:params){
				for(String name:CUSTOM_PROVINCE_BZ_PARAMETER){
					if (param.startsWith(name))
						parameterResult.put(name, param.substring(name.length()));
				}
			}
		}
		return parameterResult;
	}
	public StationList getStationsFromJson(String json) {
		List<Meteostation> data = parseJson(json);
		Set<StationDto> stations = new HashSet<StationDto>();
		if (data != null)
			for (Meteostation station:data){

				Map<String, String> optionalParametersFromComments = getOptionalParametersFromComments(station.getComments());
				Map<String, String> params = station.getMetaData();
				params.putAll(optionalParametersFromComments);

				MeteoStationDto dto = new MeteoStationDto();
				dto.setArea(params.get(AREA));
				dto.setId(params.get(ZEUS_ID));
				Double latitude = null,longitude = null;
				try {
					longitude = numberFormatter.parse(params.get(X)).doubleValue();
					latitude = numberFormatter.parse(params.get(Y)).doubleValue();
				} catch (ParseException e) {
					e.printStackTrace();
				}

				dto.setLatitude(latitude);
				dto.setLongitude(longitude);
				dto.setCoordinateReferenceSystem(environment.getProperty(GEOMTRY_CRS));
				dto.setName(params.get(NAME));
				dto.setOrigin("SIAG");
				stations.add(dto);
			}
		return new StationList(stations);
	}

	public DataMapDto<RecordDtoImpl> getRecordsFromJson(String json) {
		DataMapDto<RecordDtoImpl> typesByStation = new DataMapDto<>();
		List<Meteostation> data = parseJson(json);
		for (Meteostation station : data){
			//TODO get timezone dynamically with station coordinates
			dateFormatter.setTimeZone(TimeZone.getTimeZone(environment.getProperty("default_timezone"))); //set the timezone in which your station is currently located (not defined in zrx)
			Map<String, String> params = station.getMetaData();
			String stationId = params.get(ZEUS_ID);
			String type = params.get(CNAME);
			String existingType = environment.getProperty(type);
			if (existingType == null)
					existingType = type;
			DataMapDto<RecordDtoImpl> typeMapDto = typesByStation.getBranch().get(stationId);
			if (typeMapDto == null){
				typeMapDto = new DataMapDto<>();
				typesByStation.getBranch().put(stationId, typeMapDto);
			}
			Date dateOfLastRecord = getLastRecordDate(stationId, existingType);
			DataMapDto<RecordDtoImpl> existingMapDto = typeMapDto.getBranch().get(existingType);
			List<RecordDtoImpl> list;
			if (existingMapDto == null){
				list = new ArrayList<RecordDtoImpl>();
			}else
				list = existingMapDto.getData();
			for (MeteoStationDataPoint point: station.getDataPoints()){
				Date date = null;
				try {
					date = dateFormatter.parse(point.getDate());
					if (dateOfLastRecord.getTime()>=date.getTime())
						continue;
					SimpleRecordDto simpleRecordDto = new SimpleRecordDto(date.getTime(),point.getDataPoint());
					Integer cmw = Integer.valueOf(params.get(CMW));
					Integer period = PERIOD;
					if (cmw != null)
						period = 3600 / (cmw / 24);
					simpleRecordDto.setPeriod(period);
					list.add(simpleRecordDto);
					typeMapDto.getBranch().put(existingType, new DataMapDto<>(list));
				} catch (ParseException e) {
					e.printStackTrace();
					continue;
				}
			}
		}
		return typesByStation;
	}

	public Date getLastRecordDate(String station,String type) {
		Object dateOfLastRecord = pusher.getDateOfLastRecord(station,type, null);
		Date date = (Date) dateOfLastRecord;
		return date;
	}

	public List<DataTypeDto> getDataTypesFromJson(String json) {
		List<Meteostation> data = parseJson(json);
		Set<DataTypeDto> types = new HashSet<DataTypeDto>();
		if (data != null)
			for (Meteostation station:data){
				Map<String, String> params = station.getMetaData();
				DataTypeDto dto = new DataTypeDto();
				String type = params.get(CNAME);
				String property = environment.getProperty(type);
				if(property != null)
						type = property;
				dto.setName(type);
				dto.setUnit(params.get(CUNIT));
				dto.setRtype(params.get(VALUE_TYPE));
				types.add(dto);
			}
		return new ArrayList<>(types);
	}
}
