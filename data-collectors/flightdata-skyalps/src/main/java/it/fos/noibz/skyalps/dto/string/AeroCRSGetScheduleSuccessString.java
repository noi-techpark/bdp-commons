package it.fos.noibz.skyalps.dto.string;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.datetime.DateFormatter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ch.qos.logback.classic.db.names.SimpleDBNameResolver;
import it.fos.noibz.skyalps.dto.json.AeroCRSFlight;
import it.fos.noibz.skyalps.dto.json.AeroCRSGetScheduleSuccess;
import it.fos.noibz.skyalps.dto.json.AeroCRSGetScheduleSuccessResponse;
import net.logstash.logback.encoder.org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Thierry BODHUIN, bodhuin@gmail.com
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AeroCRSGetScheduleSuccessString {

	private static final Logger LOG = LoggerFactory.getLogger(AeroCRSGetScheduleSuccessString.class);

	private boolean success;
	private String flight;
	private List<AeroCRSFlight> flightsList;
	private static final int SSIMSTRINGLENGHT = 200;
	private static final int MONTHFROMFIRST = 16;
	private static final int MONTHFROM = 17;
	private static final int MONTHFROMFINAL = 19;
	private static final int MONTHTOFIRST = 23;
	private static final int MONTHTO = 24;
	private static final int MONTHTOFINAL = 26;
	private static final int FLTNUMBERSTART = 2;
	private static final int FLTNUMBEREND = 9;
	private static final String AIRLINENAME = "Sky Alps";
	private static final int FROMDESTINATIONSTART = 36;
	private static final int FROMDESTINATIONEND = 39;
	private static final int TODESTINATIONSTART = 54;
	private static final int TODESTINATIONEND = 57;
	private static final int AIRLINEIDSTART = 10;
	private static final int AIRLINEIDEND = 13;
	private static final int ACCODESTART = 72;
	private static final int ACCODEND = 75;
	private static final int YEAR = 2000;
	private static final int YEAFROMSTART = 19;
	private static final int YEARFROMEND = 21;
	private static final int YEARTOSTART = 26;
	private static final int YEARTOEND = 28;
	private static final int DAYFROMSTART = 14;
	private static final int DAYFROMEND = 16;
	private static final int DAYTOSTART = 21;
	private static final int DAYTOEND = 23;
	private static final int STDHOURSTART = 43;
	private static final int STDHOUREND = 45;
	private static final int STDMINUTESTART = 46;
	private static final int STDMINUTEEND = 52;
	private static final int STAHOURSTART = 61;
	private static final int STAHOUREND = 63;
	private static final int STAMINUTESTART = 64;
	private static final int STAMINUTEND = 71;
	private static final int MONDAYCHAR = 28;
	private static final int SUNCHAR = 34;
	private static final int MONDAY = 1;
	private static final int TUESDAY = 2;
	private static final int WEDNESDAY = 3;
	private static final int THURSDAY = 4;
	private static final int FRIDAY = 5;
	private static final int SATURDAY = 6;
	private static final int SUNDAY = 7;
	private static final SimpleDateFormat DFFROMTO = new SimpleDateFormat("yyyy/MM/dd");
	private static final SimpleDateFormat DFSTDA = new SimpleDateFormat("yyyy/MM/dd HH:mmZZZZ");

	public AeroCRSGetScheduleSuccessString() {
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getFlight() {
		return flight;
	}

	public void setFlight(String flight) throws ParseException {
		this.flight = flight;
	}

	public AeroCRSGetScheduleSuccessResponse decodeFlights() throws ParseException {
		LOG.info("Start parsing...");
		AeroCRSGetScheduleSuccess flights = new AeroCRSGetScheduleSuccess();
		AeroCRSGetScheduleSuccessResponse flightFinal = new AeroCRSGetScheduleSuccessResponse();
		String[] flightArray = flight.trim().split("\\R");
		flightsList = new ArrayList<>();

		for (int i = 1; i < flightArray.length; i++) {
			int flightSSIMLenght = flightArray[i].length();
			if (flightArray[i].contains(" ") && flightSSIMLenght == SSIMSTRINGLENGHT) {
				try {
					String singleFlight = flightArray[i];

					DateTimeFormatter monthParser = DateTimeFormatter.ofPattern("MMM").withLocale(Locale.ENGLISH);
					SimpleDateFormat dateFormatter = new SimpleDateFormat("ddMMMyy", Locale.ENGLISH);

					String yearFromComplete = singleFlight.substring(YEAFROMSTART, YEARFROMEND);
					String yearToComplete = singleFlight.substring(YEARTOSTART, YEARTOEND);
					String monthfromComplete = singleFlight.substring(MONTHFROM, MONTHFROMFINAL);
					String monthToComplete = singleFlight.substring(MONTHTO, MONTHTOFINAL);
					String dayFromComplete = singleFlight.substring(DAYFROMSTART, DAYFROMEND);
					String dayToComplete = singleFlight.substring(DAYTOSTART, DAYTOEND);
					String monthFromFull = singleFlight.charAt(MONTHFROMFIRST) + monthfromComplete.toLowerCase();
					String monthToFull = singleFlight.charAt(MONTHTOFIRST) + monthToComplete.toLowerCase();

					TemporalAccessor accessorFrom = monthParser
							.parse(monthFromFull);
					TemporalAccessor accessorTo = monthParser
							.parse(monthToFull);

					int yearFrom = YEAR + Integer.parseInt(yearFromComplete);
					int yearTo = YEAR + Integer.parseInt(yearToComplete);
					int monthFrom = accessorFrom.get(ChronoField.MONTH_OF_YEAR);
					int monthTo = accessorTo.get(ChronoField.MONTH_OF_YEAR);
					int dayFrom = Integer.parseInt(dayFromComplete);
					int dayTo = Integer.parseInt(dayToComplete);

					Instant instantFrom = Instant
							.parse(String.format("%04d-%02d-%02dT00:00:00.00Z", yearFrom, monthFrom, dayFrom));
					Instant instantTo = Instant
							.parse(String.format("%04d-%02d-%02dT00:00:00.00Z", yearTo, monthTo, dayTo));

					int weekdayFrom = instantFrom.atZone(ZoneId.of("UTC")).getDayOfWeek().getValue();

					// extract weekdays and convert to int
					int[] weekdays = flightArray[i].substring(MONDAYCHAR, SUNCHAR + 1).replace(" ", "").chars()
							.map(x -> x - '0')
							.toArray();

					// put weekday of from date to first position
					// special case: first flight would be on friday but the next one on monday
					// int shiftCounter = 0;
					// while (weekdays[0] != weekdayFrom && shiftCounter < 7) {
					// int last = weekdays[weekdays.length - 1];
					// System.arraycopy(weekdays, 0, weekdays, 1, weekdays.length - 1);
					// weekdays[0] = last;
					// shiftCounter++;
					// }

					LOG.debug("Extracting flights...");
					// iterate over weekdays and the add up 7 days until to date is reached
					for (int weekday : weekdays) {

						Instant currentInstant = instantFrom;

						int weekdayCurrent = currentInstant.atZone(ZoneId.of("UTC")).getDayOfWeek().getValue();

						// increase by days difference for other weekdays
						// example flight goes on Monday (1) and Friday (5)
						// first loop over monday flight then add difference 5 - 1 = 4 to loop over
						// friday flights
						if (weekday - weekdayCurrent >= 0)
							currentInstant = currentInstant.plus(weekday - weekdayCurrent, ChronoUnit.DAYS);
						else
							// in case the first flight of the period is not on the first weekday of
							// schedule
							// example: flight is on mondays and fridays from but first flight of period is
							// on friday
							// so first monday is out of range and next monday is used as start
							currentInstant = currentInstant.plus(7, ChronoUnit.DAYS);

						do {
							// actual flight date
							AeroCRSFlight aeroFlight = createAereoFlight(singleFlight, monthParser, dateFormatter,
									currentInstant,
									weekday);
							flightsList.add(aeroFlight);
							// go to next week
							currentInstant = currentInstant.plus(7, ChronoUnit.DAYS);
						} while (!currentInstant.isAfter(instantTo));
					}
					LOG.debug("Extracting flights done.");

				} catch (Exception e) {
					LOG.debug("error while parsing line {} with exception {}", flightArray[i], e.getMessage());
				}
			}
		}
		flights.setSuccess(true);
		flights.setFlight(flightsList);
		flightFinal.setAerocrs(flights);
		LOG.info("Parsing done.");
		return flightFinal;
	}

	private AeroCRSFlight createAereoFlight(String singleFlight, DateTimeFormatter monthParser,
			SimpleDateFormat dateFormatter, Instant currentInstant,
			int weekday)
			throws ParseException {
		AeroCRSFlight aeroFlight = new AeroCRSFlight();

		String fltnumber = singleFlight.substring(FLTNUMBERSTART, FLTNUMBEREND);
		String fromDestination = singleFlight.substring(FROMDESTINATIONSTART, FROMDESTINATIONEND);
		String toDestination = singleFlight.substring(TODESTINATIONSTART, TODESTINATIONEND);
		String airlineID = singleFlight.substring(AIRLINEIDSTART, AIRLINEIDEND);
		String accode = singleFlight.substring(ACCODESTART, ACCODEND);
		String yearFromComplete = singleFlight.substring(YEAFROMSTART, YEARFROMEND);
		String yearToComplete = singleFlight.substring(YEARTOSTART, YEARTOEND);
		String monthfromComplete = singleFlight.substring(MONTHFROM, MONTHFROMFINAL);
		String monthToComplete = singleFlight.substring(MONTHTO, MONTHTOFINAL);
		String dayFromComplete = singleFlight.substring(DAYFROMSTART, DAYFROMEND);
		String dayToComplete = singleFlight.substring(DAYTOSTART, DAYTOEND);
		String stdHour = singleFlight.substring(STDHOURSTART, STDHOUREND);
		String stdMinute = singleFlight.substring(STDMINUTESTART, STDMINUTEEND);
		String staHour = singleFlight.substring(STAHOURSTART, STAHOUREND);
		String staMinute = singleFlight.substring(STAMINUTESTART, STAMINUTEND);
		String monthFromFull = singleFlight.charAt(MONTHFROMFIRST) + monthfromComplete.toLowerCase();
		String monthToFull = singleFlight.charAt(MONTHTOFIRST) + monthToComplete.toLowerCase();

		TemporalAccessor accessorFrom = monthParser
				.parse(monthFromFull);
		TemporalAccessor accessorTo = monthParser
				.parse(monthToFull);

		int yearFrom = YEAR + Integer.parseInt(yearFromComplete);
		int yearTo = YEAR + Integer.parseInt(yearToComplete);
		int monthFrom = accessorFrom.get(ChronoField.MONTH_OF_YEAR);
		int monthTo = accessorTo.get(ChronoField.MONTH_OF_YEAR);
		int dayFrom = Integer.parseInt(dayFromComplete);
		int dayTo = Integer.parseInt(dayToComplete);
		Calendar calFrom = Calendar.getInstance();
		calFrom.set(Calendar.YEAR, yearFrom);
		calFrom.set(Calendar.MONTH, monthFrom);
		calFrom.set(Calendar.DAY_OF_MONTH, dayFrom);
		Calendar calTo = Calendar.getInstance();
		calTo.set(Calendar.YEAR, yearTo);
		calTo.set(Calendar.MONTH, monthTo);
		calTo.set(Calendar.DAY_OF_MONTH, dayTo);

		Calendar caSTD = Calendar.getInstance();
		Calendar caSTA = Calendar.getInstance();
		Date paredDateSTD = DFSTDA
				.parse(DFFROMTO.format(calFrom.getTime()) + " " + stdHour + ":" + stdMinute);
		caSTD.setTime(paredDateSTD);
		Date paredDateSTA = DFSTDA
				.parse(DFFROMTO.format(calTo.getTime()) + " " + staHour + ":" + staMinute);
		SimpleDateFormat dfs = new SimpleDateFormat("HH:mm");
		caSTA.setTime(paredDateSTA);

		Calendar currentCalendar = Calendar.getInstance();
		currentCalendar.setTimeInMillis(currentInstant.toEpochMilli());

		String date = dateFormatter.format(Date.from(currentInstant)).toUpperCase();

		aeroFlight.setFltnumber(StringUtils.deleteWhitespace(fltnumber));
		aeroFlight.setAirlinename(AIRLINENAME);
		aeroFlight.setFromdestination(fromDestination);
		aeroFlight.setTodestination(toDestination);
		aeroFlight.setAirlineid(airlineID);
		aeroFlight.setAccode(accode);
		aeroFlight.setFltsfromperiod(DFFROMTO.format(calFrom.getTime()));
		aeroFlight.setFltstoperiod(DFFROMTO.format(calTo.getTime()));
		aeroFlight.setStd(dfs.format(caSTD.getTime()));
		aeroFlight.setSta(dfs.format(caSTA.getTime()));
		aeroFlight.setDate(date);
		aeroFlight.setSsimMessage(singleFlight);

		aeroFlight.setWeekdaymon(weekday == MONDAY);
		aeroFlight.setWeekdaytue(weekday == TUESDAY);
		aeroFlight.setWeekdaywed(weekday == WEDNESDAY);
		aeroFlight.setWeekdaythu(weekday == THURSDAY);
		aeroFlight.setWeekdayfri(weekday == FRIDAY);
		aeroFlight.setWeekdaysat(weekday == SATURDAY);
		aeroFlight.setWeekdaysun(weekday == SUNDAY);

		return aeroFlight;
	}
}
