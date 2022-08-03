package it.bz.odh.trafficprovbz;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.bz.odh.trafficprovbz.dto.AggregatedDataDto;
import it.bz.odh.trafficprovbz.dto.ClassificationSchemaDto;
import it.bz.odh.trafficprovbz.dto.MetadataDto;
import it.bz.odh.trafficprovbz.dto.PassagesDataDto;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

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
		String responseString = "[{\"Id\": 1, \"Nome\": \"Schema Famas 9+1\", \"Classi\": [{\"Codice\": 0,\"Descrizione\": \"Conteggio\"},{\"Codice\": 1,\"Descrizione\": \"Moto\"},{\"Codice\": 2,\"Descrizione\": \"Auto\"},{\"Codice\": 3,\"Descrizione\": \"Auto con rimorchio\"},{\"Codice\": 4,\"Descrizione\": \"Furgoni\"},{\"Codice\": 5,\"Descrizione\": \"Camion <7,5m\"},{\"Codice\": 6,\"Descrizione\": \"Camion >7,5m\"},{\"Codice\": 7,\"Descrizione\": \"Autotreni\"},{\"Codice\": 8,\"Descrizione\": \"Autoarticolati\"},{\"Codice\": 9,\"Descrizione\": \"Autobus\"},{\"Codice\": 10,\"Descrizione\": \"Altri\"}]}]";
		System.out.println(responseString);
		return objectMapper.readValue(responseString, ClassificationSchemaDto[].class);
	}

    public MetadataDto[] getStationsData() throws IOException {
        //HttpResponse response = client.execute(new HttpGet(stationsDataUrl));
        //HttpEntity entity = response.getEntity();
        //String responseString = EntityUtils.toString(entity, RESPONSE_CHARSET);
		String responseString = "[{ \"Id\": 1, \"Nome\": \"4\",\"GeoInfo\": {\"Latitudine\": 46.4497009548582, \"Longitudine\":11.3448734664564, \"Regione\":\"Trentino-Alto Adige\", \"Provincia\":\"Bolzano\", \"Comune\":\"Laives\"},\"StradaInfo\": {\"Nome\": \"SS 12 dell'Abetone e del Brennero\", \"Chilometrica\": 432.69},\"Direzioni\": [{\"Tipo\": \"ascendente\",\"Descrizione\": \"Verso Bolzano\"},{\"Tipo\": \"discendente\", \"Descrizione\": \"Verso Trento\"}], \"SchemaDiClassificazione\": 1,\"NumeroCorsie\": 2,\"CorsieInfo\": [{\"Id\": 1,\"Descrizione\": \"verso Bolzano\", \"SensoDiMarcia\": \"ascendente\"},{\"Id\": 2,\"Descrizione\": \"verso Trento\",\"SensoDiMarcia\": \"discendente\"}]}, { \"Id\": 2, \"Nome\": \"4\",\"GeoInfo\": {\"Latitudine\": 47.4497009548582, \"Longitudine\":12.3448734664564, \"Regione\":\"Trentino-Alto Adige\", \"Provincia\":\"Bolzano\", \"Comune\":\"Bolzano\"},\"StradaInfo\": {\"Nome\": \"SS 17 dell'Abetone e del Brennero\", \"Chilometrica\": 421.69},\"Direzioni\": [{\"Tipo\": \"descendente\",\"Descrizione\": \"Verso Laives\"},{\"Tipo\": \"discendente\", \"Descrizione\": \"Verso Trento\"}], \"SchemaDiClassificazione\": 1,\"NumeroCorsie\": 2,\"CorsieInfo\": [{\"Id\": 1,\"Descrizione\": \"verso Bolzano\", \"SensoDiMarcia\": \"ascendente\"},{\"Id\": 2,\"Descrizione\": \"verso Trento\",\"SensoDiMarcia\": \"discendente\"}]}]";
		System.out.println(responseString);
        return objectMapper.readValue(responseString, MetadataDto[].class);
    }

    public AggregatedDataDto[] getAggregatedDataOnStations() throws IOException {
		//String payload = """
        //        data={
        //            "InizioPeriodo": "admin",
        //            "FinePeriodo": "System",
        //        }
        //        """;
		//StringEntity entity = new StringEntity(payload,
		//	ContentType.APPLICATION_JSON);
		//HttpPost request = new HttpPost(aggregatedDataOnStationsUrl);
		//request.setEntity(entity);
        //HttpResponse response = client.execute(request);
        //String responseString = EntityUtils.toString(entity, RESPONSE_CHARSET);
		String responseString = "[{\"IdPostazione\": 3, \"Data\": \"2021-12-02T11:10:00Z\", \"Corsia\": 0, \"Direzione\": \"ascendente\", \"TotaleVeicoli\": 64, \"TotaliPerClasseVeicolare\": { \"2\": 59, \"4\": 5 }, \"MediaArmonicaVelocita\": 79.3, \"HeadwayMedioSecondi\": 4.68, \"VarianzaHeadwayMedioSecondi\": 26.01, \"GapMedioSecondi\": 4.42, \"VarianzaGapMedioSecondi\": 26.12}, { \"IdPostazione\": 3, \"Data\": \"2021-12-02T11:10:00Z\", \"Corsia\": 0, \"Direzione\": \"discendente\", \"TotaleVeicoli\": 0 }, { \"IdPostazione\": 3, \"Data\": \"2021-12-02T11:10:00Z\", \"Corsia\": 1, \"Direzione\": \"ascendente\", \"TotaleVeicoli\": 0 }, { \"IdPostazione\": 3, \"Data\": \"2021-12-02T11:10:00Z\", \"Corsia\": 1, \"Direzione\": \"discendente\", \"TotaleVeicoli\": 94, \"TotaliPerClasseVeicolare\": { \"1\": 5, \"2\": 77, \"4\": 3, \"5\": 2, \"6\": 3, \"8\": 3, \"9\": 1 }, \"MediaArmonicaVelocita\": 67.9, \"HeadwayMedioSecondi\": 2.96, \"VarianzaHeadwayMedioSecondi\": 27.47, \"GapMedioSecondi\": 2.65, \"VarianzaGapMedioSecondi\": 27.75 }, { \"IdPostazione\": 3, \"Data\": \"2021-12-02T11:15:00Z\", \"Corsia\": 0, \"Direzione\": \"ascendente\", \"TotaleVeicoli\": 50, \"TotaliPerClasseVeicolare\": { \"2\": 46, \"3\": 2, \"4\": 1, \"8\": 1 }, \"MediaArmonicaVelocita\": 78.1, \"HeadwayMedioSecondi\": 5.97, \"VarianzaHeadwayMedioSecondi\": 47.25, \"GapMedioSecondi\": 5.69, \"VarianzaGapMedioSecondi\": 47.69}]";
        return objectMapper.readValue(responseString, AggregatedDataDto[].class);
    }

    public PassagesDataDto[] getPassagesDataOnStations(int stationId) throws IOException {
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
		String responseString = "[{\"IdPostazione\": 3, \"Data\": \"2021-12-03T08:25:06Z\", \"IdVeicolo\": \"9532E31173B863BE28A5B76CF1BB91C5\" }, { \"IdPostazione\": 3, \"Data\": \"2021-12-03T08:25:08Z\", \"IdVeicolo\": \"A032FA4CC79C8EB1342A2F4A53D2260E\" }, { \"IdPostazione\": 3, \"Data\": \"2021-12-03T08:25:12Z\", \"IdVeicolo\": \"E51B97BB2C56050F1F91C74E5AAF738E\" }]";
        return objectMapper.readValue(responseString, PassagesDataDto[].class);
    }
}
