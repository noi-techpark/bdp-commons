package it.bz.odh.trafficprovbz;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.odh.trafficprovbz.dto.AggregatedDataDto;
import it.bz.odh.trafficprovbz.dto.MetadataDto;
import net.minidev.json.JSONObject;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

	@Test
	void checkIfCreationOfStationWorks() throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();

		String metadata = "[{ \"Id\": 1, \"Nome\": \"4\",\"GeoInfo\": {\"Latitudine\": 46.4497009548582, \"Longitudine\":11.3448734664564, \"Regione\":\"Trentino-Alto Adige\", \"Provincia\":\"Bolzano\", \"Comune\":\"Laives\"},\"StradaInfo\": {\"Nome\": \"SS 12 dell'Abetone e del Brennero\", \"Chilometrica\": 432.69},\"Direzioni\": [{\"Tipo\": \"ascendente\",\"Descrizione\": \"Verso Bolzano\"},{\"Tipo\": \"discendente\", \"Descrizione\": \"Verso Trento\"}], \"SchemaDiClassificazione\": 1,\"NumeroCorsie\": 2,\"CorsieInfo\": [{\"Id\": 1,\"Descrizione\": \"verso Bolzano\", \"SensoDiMarcia\": \"ascendente\"},{\"Id\": 2,\"Descrizione\": \"verso Trento\",\"SensoDiMarcia\": \"discendente\"}]}]";
		MetadataDto[] stationsUnderTest = objectMapper.readValue(metadata, MetadataDto[].class);
		JSONObject otherFields = new JSONObject(stationsUnderTest[0].getOtherFields());
		ArrayList<LinkedHashMap<String, String>> lanes = JsonPath.read(otherFields, "$.CorsieInfo");

		for (LinkedHashMap<String, String> lane: lanes) {
			StationDto stationUnderTest = Parser.createStation(stationsUnderTest[0], otherFields, lane, null);
			assertThat("1").isEqualTo(stationUnderTest.getId());
			assertThat(46.4497009548582).isEqualTo(stationUnderTest.getLatitude());
			assertThat(11.3448734664564).isEqualTo(stationUnderTest.getLongitude());
		}
	}

	@Test
	void checkIfCreationOfTrafficMeasurmentWorks() throws JsonProcessingException, ParseException {
		ObjectMapper objectMapper = new ObjectMapper();
		Integer period = 300;

		String aggregatedData = "[{\"IdPostazione\": 3, \"Data\": \"2021-12-02T11:10:00Z\", \"Corsia\": 0, \"Direzione\": \"ascendente\", \"TotaleVeicoli\": 64, \"TotaliPerClasseVeicolare\": { \"2\": 59, \"4\": 5 }, \"MediaArmonicaVelocita\": 79.3, \"HeadwayMedioSecondi\": 4.68, \"VarianzaHeadwayMedioSecondi\": 26.01, \"GapMedioSecondi\": 4.42, \"VarianzaGapMedioSecondi\": 26.12}]";
		AggregatedDataDto[] aggregatedDataDtos = objectMapper.readValue(aggregatedData, AggregatedDataDto[].class);

			SimpleRecordDto measurementUnderTest = Parser.createTrafficMeasurement(aggregatedDataDtos[0], period);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			Long timestamp = formatter.parse(aggregatedDataDtos[0].getDate()).getTime();
			assertThat(period).isEqualTo(measurementUnderTest.getPeriod());
			assertThat(new Date().getTime()).isCloseTo(measurementUnderTest.getCreated_on(), Percentage.withPercentage(2));
			assertThat(timestamp).isEqualTo(measurementUnderTest.getTimestamp());
	}
}
