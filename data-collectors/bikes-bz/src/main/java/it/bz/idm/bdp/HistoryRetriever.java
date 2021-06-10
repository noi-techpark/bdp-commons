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

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationDto;

@Component
@PropertySource("classpath:/META-INF/spring/application.properties")
public class HistoryRetriever {

	private static final int TIME_INTERVAL = 1000*60*60*24;

	@Autowired
	public DataParser parser;

	@Autowired
	private BikeCountPusher bdpClient;

	@Autowired
	public Environment environment;
	private Logger logger = LogManager.getLogger(HistoryRetriever.class);

	/*retrieve all history data from a given point in time and push it to the bdp */
	public void getHistory(LocalDateTime newestDateMidnight) {
		DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String startDate = environment.getProperty("history.startDate");
		LocalDateTime manualDate = LocalDate.parse(startDate, format).atStartOfDay();
		if (newestDateMidnight == null || manualDate.isAfter(newestDateMidnight))
			newestDateMidnight = manualDate;
		try {
			XMLGregorianCalendar from = null, to = null;
			GregorianCalendar cal = new GregorianCalendar();
			Duration duration = DatatypeFactory.newInstance().newDuration(TIME_INTERVAL);
			while (newestDateMidnight.isBefore(LocalDateTime.now())) {
				cal.setTimeInMillis(newestDateMidnight.toInstant(ZoneOffset.UTC).toEpochMilli());
				from = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
				to = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
				to.add(duration);
				logger.debug("About To request Data");
				Long now = new Date().getTime();
				DataMapDto<RecordDtoImpl> dataMapDto = parser.retrieveHistoricData(from, to);
				logger.debug("3rd party took" + (new Date().getTime() - now) / 1000 + " s");
				bdpClient.pushData(dataMapDto);
				logger.debug("Data sent");
				newestDateMidnight = LocalDateTime
						.ofInstant(Instant.ofEpochMilli(to.toGregorianCalendar().getTimeInMillis()), ZoneOffset.UTC);
			}
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
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
		List<StationDto> fetchStations = bdpClient.fetchStations(BikeCountPusher.STATIONTYPE_IDENTIFIER,
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
		return dateList.isEmpty() ? null : dateList.get(dateList.size() - 1);
	}

}
