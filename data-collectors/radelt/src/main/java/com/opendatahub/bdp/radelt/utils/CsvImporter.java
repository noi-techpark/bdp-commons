// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.bdp.radelt.utils;

import com.opendatahub.bdp.radelt.dto.common.RadeltGeoDto;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class CsvImporter {

	public static Map<String, RadeltGeoDto> syncCsvActions() {
		String filePath = "/radelt-actions.csv";
		return readCsv(filePath);
	}

	public static Map<String, RadeltGeoDto> syncCsvOrganizations() {
		String filePath = "/radelt-organizations.csv";
		return readCsv(filePath);
	}

	private static Map<String, RadeltGeoDto> readCsv(String filePath) {
		Map<String, RadeltGeoDto> coordinatesMap = new HashMap<>();

		try (Reader reader = new InputStreamReader(CsvImporter.class.getResourceAsStream(filePath));
			 CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

			for (CSVRecord csvRecord : csvParser) {
				// Assuming the CSV has columns: id, latitude, longitude
				String id = String.valueOf(csvRecord.get("id"));
				double latitude = Double.parseDouble(csvRecord.get("latitude"));
				double longitude = Double.parseDouble(csvRecord.get("longitude"));

				RadeltGeoDto geoDto = new RadeltGeoDto(id, latitude, longitude);
				coordinatesMap.put(id, geoDto);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return coordinatesMap;
	}
}
