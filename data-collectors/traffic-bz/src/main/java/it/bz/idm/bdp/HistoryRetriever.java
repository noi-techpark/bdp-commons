// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationDto;

@Component
@PropertySource("classpath:/META-INF/spring/application.properties")
public class HistoryRetriever {

	@Value("${timeInterval}")
	private int timeInterval;

	@Value("${history.startDate}")
	private String startDate;


	@Autowired
	public DataParser parser;

	@Autowired
	private TrafficPusher bdpClient;

	@Autowired
	public Environment environment;
	private Logger logger = LoggerFactory.getLogger(HistoryRetriever.class);

	/*
	 * retrieve all history data from a given point in time and push it to the bdp
	 */
	public void getHistory(){

		DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDateTime newestDateMidnight = LocalDate.parse(startDate,format).atStartOfDay();
		try {
			XMLGregorianCalendar from = null,to = null;
			GregorianCalendar cal = new GregorianCalendar();
			Duration duration = DatatypeFactory.newInstance().newDuration(timeInterval);
			while (newestDateMidnight.isBefore(LocalDateTime.now())){
				cal.setTimeInMillis(newestDateMidnight.toInstant(ZoneOffset.UTC).toEpochMilli());
				from = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
				to = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
				to.add(duration);
				logger.debug("About To request Data");
				Long now = new Date().getTime();
				Map<String, DataMapDto<RecordDtoImpl>> retrieveHistoricData = parser.retrieveHistoricData(from,to);
				logger.debug("3rd party took" + (new Date().getTime()-now)/1000 +" s");

				DataMapDto<RecordDtoImpl> dataMapDto = retrieveHistoricData.get(TrafficPusher.TRAFFIC_SENSOR_IDENTIFIER);
				if (retrieveHistoricData.containsKey(TrafficPusher.TRAFFIC_SENSOR_IDENTIFIER)){
					bdpClient.pushData(dataMapDto);
					logger.debug("traffic data sent amount: {}",dataMapDto.getData().size());
				}
				if (retrieveHistoricData.containsKey(TrafficPusher.METEOSTATION_IDENTIFIER)){
					DataMapDto<RecordDtoImpl> dataMapDtoMeteo = retrieveHistoricData.get(TrafficPusher.METEOSTATION_IDENTIFIER);
					bdpClient.pushData(TrafficPusher.METEOSTATION_IDENTIFIER,dataMapDtoMeteo);
					logger.debug("meteo data sent amount: {}", dataMapDtoMeteo.getData().size());
				}
				if (retrieveHistoricData.containsKey(TrafficPusher.ENVIRONMENTSTATION_IDENTIFIER)){
					DataMapDto<RecordDtoImpl> dataMapDtoEnvironment = retrieveHistoricData.get(TrafficPusher.ENVIRONMENTSTATION_IDENTIFIER);
					bdpClient.pushData(TrafficPusher.ENVIRONMENTSTATION_IDENTIFIER,dataMapDtoEnvironment);
					logger.debug("environment data sent amount: {}", dataMapDtoEnvironment.getData().size());
				}
				newestDateMidnight = LocalDateTime.ofInstant(Instant.ofEpochMilli(to.toGregorianCalendar().getTimeInMillis()),ZoneOffset.UTC);
			}
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}finally{
		}
	}

	public void getLatestHistory() {
		Date newestDate = fetchNewestExistingDate();
		LocalDateTime newestDateMidnight = LocalDateTime.of(
				LocalDateTime.ofInstant(Instant.ofEpochMilli(newestDate.getTime()), ZoneOffset.UTC).toLocalDate(),
				LocalTime.MIDNIGHT);
		getHistory(newestDateMidnight);
	}

	public Date fetchNewestExistingDate() {
		logger.debug("Start fetching newest records for all stations");
		List<Date> dateList = new ArrayList<>();
		List<StationDto> fetchStations = bdpClient.fetchStations(TrafficPusher.TRAFFIC_SENSOR_IDENTIFIER,
				DataParser.DATA_ORIGIN);
		logger.debug("Number of fetched stations" + fetchStations.size());
		for (StationDto dto : fetchStations) {
			Date hui = new Date();
			Date dateOfLastRecord = (Date) bdpClient.getDateOfLastRecord(dto.getId(), null, null);
			if (dateOfLastRecord != null)
				dateList.add(dateOfLastRecord);
			logger.debug("Querry took" + (new Date().getTime() - hui.getTime()));
		}
		Collections.sort(dateList);
		return dateList.get(dateList.size() - 1);
	}

}
