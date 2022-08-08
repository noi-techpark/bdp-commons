package it.bz.odh.trafficprovbz;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.json.parser.JSONParser;
import com.nimbusds.jose.shaded.json.parser.ParseException;
import it.bz.odh.trafficprovbz.dto.AggregatedDataDto;
import it.bz.odh.trafficprovbz.dto.ClassificationSchemaDto;
import it.bz.odh.trafficprovbz.dto.MetadataDto;
import it.bz.odh.trafficprovbz.dto.PassagesDataDto;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

@Lazy
@Service
public class FamasClient {
	private static final String RESPONSE_CHARSET = "UTF-8";

	private static final String STATION_ID_URL_PARAM = "%STATION_ID%";

	@Value("${endpoint.classificationSchemas.url}")
	private String classificationSchemasUrl;

	@Value("${endpoint.stationsData.url}")
	private String stationsDataUrl;

	@Value("${endpoint.aggregatedDataOnStations.url}")
	private String aggregatedDataOnStationsUrl;

	@Value("${endpoint.passageDataOnStations.url}")
	private String passagesDataOnStationsUrl;

	private final ObjectMapper objectMapper = new ObjectMapper();

	private final HttpClient client = HttpClientBuilder.create().build();

	public ClassificationSchemaDto[] getClassificationSchemas() throws IOException {
		//HttpResponse response = client.execute(new HttpGet(classificationSchemasUrl));
		//HttpEntity entity = response.getEntity();
		//String responseString = EntityUtils.toString(entity, RESPONSE_CHARSET);
		String responseString = readJsonFile("jsonfiles/classificationSchemas.json");
		return objectMapper.readValue(responseString, ClassificationSchemaDto[].class);
	}

	public MetadataDto[] getStationsData() throws IOException {
		//HttpResponse response = client.execute(new HttpGet(stationsDataUrl));
		//HttpEntity entity = response.getEntity();
		//String responseString = EntityUtils.toString(entity, RESPONSE_CHARSET);
		String responseString = readJsonFile("jsonfiles/stationsData.json");
		return objectMapper.readValue(responseString, MetadataDto[].class);
	}

	public AggregatedDataDto[] getAggregatedDataOnStations(String stationId, String startPeriod, String endPeriod) throws IOException {
		//String payload = """
		//        data={
		//            "InizioPeriodo": startPeriod,
		//            "FinePeriodo": "endPeriod",
		//        }
		//        """;
		//StringEntity entity = new StringEntity(payload,
		//	ContentType.APPLICATION_JSON);
		//HttpPost request = new HttpPost(aggregatedDataOnStationsUrl);
		//request.setEntity(entity);
		//HttpResponse response = client.execute(request);
		//String responseString = EntityUtils.toString(entity, RESPONSE_CHARSET);
		String responseString = readJsonFile("jsonfiles/aggregatedDataOnStations.json");
		return objectMapper.readValue(responseString, AggregatedDataDto[].class);
	}

	public PassagesDataDto[] getPassagesDataOnStations(String stationId, String startPeriod, String endPeriod) throws IOException {
		//String payload = """
		//        data={
		//			  "IdPostazioni": [
		//				stationId
		//			]
		//            "InizioPeriodo": "admin",
		//            "FinePeriodo": "System",
		//        }
		//        """;
		//StringEntity entity = new StringEntity(payload,
		//	ContentType.APPLICATION_JSON);
		//HttpPost request = new HttpPost(passagesDataOnStationsUrl);
		//request.setEntity(entity);
		//HttpResponse response = client.execute(request);
		//String responseString = EntityUtils.toString(entity, RESPONSE_CHARSET);
		String responseString = readJsonFile("jsonfiles/passagesDataOnStations.json");
		return objectMapper.readValue(responseString, PassagesDataDto[].class);
	}

	/**
	 * TODO: Remove helper class after calling live api
	 *
	 * Read json test files and return them
	 *
	 * @param url is a string where the file location is stored
	 * @return a string containing the data of the json file
	 */
	public String readJsonFile(String url) {
		JSONParser jsonParser = new JSONParser();
		try (FileReader reader = new FileReader(url))
		{
			return String.valueOf(jsonParser.parse(reader));

		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
