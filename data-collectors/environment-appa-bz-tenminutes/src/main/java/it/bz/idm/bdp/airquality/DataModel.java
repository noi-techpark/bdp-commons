// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.airquality;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import it.bz.idm.bdp.airquality.parser.AirQualityLexer;
import it.bz.idm.bdp.airquality.parser.AirQualityParser;
import it.bz.idm.bdp.airquality.parser.AirQualityParser.DatasetContext;
import it.bz.idm.bdp.airquality.parser.MyAirQualityListener;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.StationDto;

@Component
@PropertySource({ "classpath:/META-INF/spring/application.properties" })
public class DataModel {

	private static final Logger log = LoggerFactory.getLogger(DataModel.class.getName());

	@Autowired
	private Environment env;

	public Map<Integer, String> validStations = new HashMap<>();
	public Map<String, StationDto> validStationsFull = new HashMap<>();
	public Map<String, DataTypeDto> validParametersFull = new HashMap<>();
	public Map<Integer, String> validErrors = new HashMap<>();
	public List<Character> validMetrics = new ArrayList<>();
	public List<Integer> validParameters = new ArrayList<>();

	private Reader csvReader = null;
	private CSVParser csvParser = null;

	public List<Integer> getValidStationsAsList() {
		return new ArrayList<>(validStations.keySet());
	}

	public List<Integer> getValidErrorsAsList() {
		return new ArrayList<>(validErrors.keySet());
	}

	private CSVParser getCSVRecords(final String filename) throws IOException {
		try {
			csvReader = new FileReader(filename);
			log.debug("Read mapping from '{}'.", filename);
			csvParser = new CSVParser(csvReader, CSVFormat.DEFAULT.withHeader().withQuote('\''));
			return csvParser;
		} catch (Exception e) {
			log.debug(e.getMessage());
			log.error(e.getMessage());
			throw e;
		}
	}

	private void closeCSVReader() {
		try {
			if (csvReader != null)
				csvReader.close();
			if (csvParser != null)
				csvParser.close();
		} catch (IOException e) {
		}
	}

	public void parseMetaData(final String folder) throws Exception {

		validStations.clear();
		validStationsFull.clear();
		validParametersFull.clear();
		validErrors.clear();

		try {
			for (CSVRecord record : getCSVRecords(folder + File.separator + "stations.csv")) {
				validStations.put(Integer.parseInt(record.get("station")), record.get("mapping"));
				Double lat = null;
				Double lon = null;
				try {
					lat = Double.parseDouble(record.get("lat"));
					lon = Double.parseDouble(record.get("lon"));
				} catch (Exception e) {
					/* Coordinates are optional; skip on failure */
				}
				validStationsFull.put(record.get("mapping"), new StationDto(record.get("mapping"), "", lat, lon));
			}
			for (CSVRecord record : getCSVRecords(folder + File.separator + "errors.csv")) {
				validErrors.put(Integer.parseInt(record.get("error")), record.get("desc"));
			}
			for (CSVRecord record : getCSVRecords(folder + File.separator + "parameters.csv")) {
				String param = record.get("parameter").trim();
				String metric = record.get("metric").trim();

				if (param == null || metric == null || param.length() == 0 || metric.length() == 0) {
					log.debug("Empty parameter mapping: " + record.toString());
					continue;
				}

				DataTypeDto dtd = new DataTypeDto(record.get("name"), record.get("unit"), record.get("desc"),
						record.get("type"));
				dtd.setPeriod(env.getRequiredProperty("odh.datatype.period", Integer.class));

				if (validParametersFull.containsKey(record.get("name"))) {
					log.debug("Parameter already exists... skipping: " + record.get("name"));
					continue;
				}
				validParametersFull.put(param + metric, dtd);
				if (!validMetrics.contains(metric.charAt(0)))
					validMetrics.add(metric.charAt(0));
				if (!validParameters.contains(Integer.parseInt(param)))
					validParameters.add(Integer.parseInt(param));
			}
		} catch (Exception e) {
			log.debug(e.getMessage());
			throw e;
		} finally {
			closeCSVReader();
		}
	}

	public MyAirQualityListener parse(final InputStream is) throws IOException {
		AirQualityLexer lexer = new AirQualityLexer(CharStreams.fromStream(is));
		AirQualityParser parser = new AirQualityParser(new CommonTokenStream(lexer));
		DatasetContext tree = parser.dataset();
		MyAirQualityListener listener = new MyAirQualityListener(getValidStationsAsList(), validParameters,
				validMetrics, getValidErrorsAsList());
		ParseTreeWalker walker = new ParseTreeWalker();
		walker.walk(listener, tree);
		return listener;
	}

	public MyAirQualityListener parse(final String input) throws IOException {
		InputStream is = new ByteArrayInputStream(input.getBytes());
		return parse(is);
	}
}
