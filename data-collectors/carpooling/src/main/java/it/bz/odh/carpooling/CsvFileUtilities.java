// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.odh.carpooling;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CsvFileUtilities {

	private static final Logger LOG = LoggerFactory.getLogger(CsvFileUtilities.class);

	public static List<CarPoolingTripDto> parseCarPoolingCsvData(InputStream inputStream) {
		LOG.info("read car pooling csv data");

		List<CarPoolingTripDto> tripsList = new ArrayList<>();

		try {
			LOG.info("parse par pooling trips csv file from url");
			BOMInputStream bomInputStream = new BOMInputStream(inputStream);
			CSVParser csvParser = CSVParser.parse(bomInputStream,
					Charset.forName(bomInputStream.hasBOM() ? bomInputStream.getBOMCharsetName() : "UTF-8"),
					CSVFormat.RFC4180.withFirstRecordAsHeader());
			int parseSuccess = 0;
			int parseFailed = 0;
			for (CSVRecord csvRecord : csvParser) {
				CarPoolingTripDto carPoolingTrip = new CarPoolingTripDto();
				try {
					carPoolingTrip.setHashedId(csvRecord.get("hashed_id"));
					carPoolingTrip.setStatus(csvRecord.get("status"));
					carPoolingTrip.setRideStartAtUtc(extractZonedDateTime(csvRecord, "ride_start_at_UTC"));
					carPoolingTrip.setRideDistanceKm(extractDouble(csvRecord, "ride_distance_km"));
					carPoolingTrip.setRideDurationMinute(extractDouble(csvRecord, "ride_duration_minutes"));
					carPoolingTrip.setRideCreatedAt(extractZonedDateTime(csvRecord, "ride_created_at_UTC"));
					carPoolingTrip.setRideModifiedAt(extractZonedDateTime(csvRecord, "ride_modified_at_UTC"));
					carPoolingTrip.setSeatsReserved(extractInteger(csvRecord, "seats_reserved"));
					carPoolingTrip.setStartLatApprox(extractDouble(csvRecord, "start_lat_approx"));
					carPoolingTrip.setStartLonApprox(extractDouble(csvRecord, "start_lon_approx"));
					carPoolingTrip.setStartPostCode(csvRecord.get("start_post_code"));
					carPoolingTrip.setEndLatApprox(extractDouble(csvRecord, "end_lat_approx"));
					carPoolingTrip.setEndLonApprox(extractDouble(csvRecord, "end_lon_approx"));
					carPoolingTrip.setEndPostCode(csvRecord.get("end_post_code"));

					tripsList.add(carPoolingTrip);
					LOG.debug("trip as JSON : {}", carPoolingTrip.toJson());
					parseSuccess++;
				} catch (Exception e) {
					LOG.warn("parsing csv record to car pooling trip failed: " + e.getMessage(), e);
					parseFailed++;
				}
			}
			LOG.info("got {} csv records, {} successfully parsed, {} failed to parse", parseSuccess + parseFailed,
					parseSuccess, parseFailed);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return tripsList;
	}

	private static ZonedDateTime extractZonedDateTime(CSVRecord csvRecord, String csvFieldName) {
		String csvFieldValue = csvRecord.get(csvFieldName);
		if (csvFieldValue == null || csvFieldValue.isEmpty())
			return null;
		return LocalDateTime.parse(csvRecord.get(csvFieldName), DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a"))
				.atZone(ZoneOffset.UTC);
	}

	private static Integer extractInteger(CSVRecord csvRecord, String csvFieldName) {
		String csvFieldValue = csvRecord.get(csvFieldName);
		if (csvFieldValue == null || csvFieldValue.isEmpty())
			return null;
		try {
			return Integer.parseInt(csvFieldValue);
		} catch (NumberFormatException e) {
			LOG.error("Unable to parse the following value of the csv field '{}' to integer: {}", csvFieldName,
					csvFieldValue);
			throw new RuntimeException(e);
		}
	}

	private static Double extractDouble(CSVRecord csvRecord, String csvFieldName) {
		String csvFieldValue = csvRecord.get(csvFieldName);
		if (csvFieldValue == null || csvFieldValue.isEmpty())
			return null;
		try {
			return Double.parseDouble(csvFieldValue);
		} catch (NumberFormatException e) {
			LOG.error("Unable to parse the following value of the csv field '{}' to double: {}", csvFieldName,
					csvFieldValue);
			throw new RuntimeException(e);
		}
	}
}
