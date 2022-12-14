package it.fos.noibz.skyalps.dto.string;

import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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

	private final Logger LOG = LoggerFactory.getLogger(AeroCRSGetScheduleSuccessString.class);

	// Reference example
	// 3 BN 19510101J29JAN2329JAN23 7 BER09400940+0100 BZO11251125+0100
	private boolean success;
	private String flight;

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
		LOG.debug("Start parsing...");
		AeroCRSGetScheduleSuccess flights = new AeroCRSGetScheduleSuccess();
		AeroCRSGetScheduleSuccessResponse flightFinal = new AeroCRSGetScheduleSuccessResponse();
		String[] flightArray = flight.trim().split("\\R");
		List<AeroCRSFlight> flightsList = new ArrayList<>();

		for (int i = 1; i < flightArray.length; i++) {
			int flightSSIMLenght = flightArray[i].length();
			if (flightArray[i].contains(" ") && flightSSIMLenght == AereoCRSConstants.SSIMSTRINGLENGHT) {
				try {
					String singleFlight = flightArray[i];
					String yearFromComplete = singleFlight.substring(AereoCRSConstants.YEAFROMSTART,
							AereoCRSConstants.YEARFROMEND);
					String yearToComplete = singleFlight.substring(AereoCRSConstants.YEARTOSTART,
							AereoCRSConstants.YEARTOEND);
					String monthfromComplete = singleFlight.substring(AereoCRSConstants.MONTHFROM,
							AereoCRSConstants.MONTHFROMFINAL);
					String monthToComplete = singleFlight.substring(AereoCRSConstants.MONTHTO,
							AereoCRSConstants.MONTHTOFINAL);
					String dayFromComplete = singleFlight.substring(AereoCRSConstants.DAYFROMSTART,
							AereoCRSConstants.DAYFROMEND);
					String dayToComplete = singleFlight.substring(AereoCRSConstants.DAYTOSTART,
							AereoCRSConstants.DAYTOEND);
					String monthFromFull = singleFlight.charAt(AereoCRSConstants.MONTHFROMFIRST)
							+ monthfromComplete.toLowerCase();
					String monthToFull = singleFlight.charAt(AereoCRSConstants.MONTHTOFIRST)
							+ monthToComplete.toLowerCase();

					TemporalAccessor accessorFrom = AereoCRSConstants.monthParser
							.parse(monthFromFull);
					TemporalAccessor accessorTo = AereoCRSConstants.monthParser
							.parse(monthToFull);

					int yearFrom = AereoCRSConstants.YEAR + Integer.parseInt(yearFromComplete);
					int yearTo = AereoCRSConstants.YEAR + Integer.parseInt(yearToComplete);
					int monthFrom = accessorFrom.get(ChronoField.MONTH_OF_YEAR);
					int monthTo = accessorTo.get(ChronoField.MONTH_OF_YEAR);
					int dayFrom = Integer.parseInt(dayFromComplete);
					int dayTo = Integer.parseInt(dayToComplete);

					Instant instantFrom = Instant
							.parse(String.format("%04d-%02d-%02dT00:00:00.00Z", yearFrom, monthFrom, dayFrom));
					Instant instantTo = Instant
							.parse(String.format("%04d-%02d-%02dT00:00:00.00Z", yearTo, monthTo, dayTo));

					// extract weekdays and convert to int
					int[] weekdays = flightArray[i]
							.substring(AereoCRSConstants.MONDAYCHAR, AereoCRSConstants.SUNCHAR + 1).replace(" ", "")
							.chars()
							.map(x -> x - '0')
							.toArray();

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
							AeroCRSFlight aeroFlight = createAereoFlight(singleFlight,
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
		LOG.debug("Parsing done.");
		return flightFinal;
	}

	private AeroCRSFlight createAereoFlight(String singleFlight, Instant currentInstant,
			int weekday)
			throws ParseException {
		AeroCRSFlight aeroFlight = new AeroCRSFlight();

		String fltnumber = singleFlight.substring(AereoCRSConstants.FLTNUMBERSTART, AereoCRSConstants.FLTNUMBEREND);
		String fromDestination = singleFlight.substring(AereoCRSConstants.FROMDESTINATIONSTART,
				AereoCRSConstants.FROMDESTINATIONEND);
		String toDestination = singleFlight.substring(AereoCRSConstants.TODESTINATIONSTART,
				AereoCRSConstants.TODESTINATIONEND);
		String airlineID = singleFlight.substring(AereoCRSConstants.AIRLINEIDSTART, AereoCRSConstants.AIRLINEIDEND);
		String accode = singleFlight.substring(AereoCRSConstants.ACCODESTART, AereoCRSConstants.ACCODEND);
		String yearFromComplete = singleFlight.substring(AereoCRSConstants.YEAFROMSTART, AereoCRSConstants.YEARFROMEND);
		String yearToComplete = singleFlight.substring(AereoCRSConstants.YEARTOSTART, AereoCRSConstants.YEARTOEND);
		String monthfromComplete = singleFlight.substring(AereoCRSConstants.MONTHFROM,
				AereoCRSConstants.MONTHFROMFINAL);
		String monthToComplete = singleFlight.substring(AereoCRSConstants.MONTHTO, AereoCRSConstants.MONTHTOFINAL);
		String dayFromComplete = singleFlight.substring(AereoCRSConstants.DAYFROMSTART, AereoCRSConstants.DAYFROMEND);
		String dayToComplete = singleFlight.substring(AereoCRSConstants.DAYTOSTART, AereoCRSConstants.DAYTOEND);
		String stdHour = singleFlight.substring(AereoCRSConstants.STDHOURSTART, AereoCRSConstants.STDHOUREND);
		String stdMinute = singleFlight.substring(AereoCRSConstants.STDMINUTESTART, AereoCRSConstants.STDMINUTEEND);
		String staHour = singleFlight.substring(AereoCRSConstants.STAHOURSTART, AereoCRSConstants.STAHOUREND);
		String staMinute = singleFlight.substring(AereoCRSConstants.STAMINUTESTART, AereoCRSConstants.STAMINUTEND);
		String monthFromFull = singleFlight.charAt(AereoCRSConstants.MONTHFROMFIRST) + monthfromComplete.toLowerCase();
		String monthToFull = singleFlight.charAt(AereoCRSConstants.MONTHTOFIRST) + monthToComplete.toLowerCase();

		TemporalAccessor accessorFrom = AereoCRSConstants.monthParser
				.parse(monthFromFull);
		TemporalAccessor accessorTo = AereoCRSConstants.monthParser
				.parse(monthToFull);

		int yearFrom = AereoCRSConstants.YEAR + Integer.parseInt(yearFromComplete);
		int yearTo = AereoCRSConstants.YEAR + Integer.parseInt(yearToComplete);
		int monthFrom = accessorFrom.get(ChronoField.MONTH_OF_YEAR) - 1;
		int monthTo = accessorTo.get(ChronoField.MONTH_OF_YEAR) - 1;
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
		Date paredDateSTD = AereoCRSConstants.DFSTDA
				.parse(AereoCRSConstants.DFFROMTO.format(calFrom.getTime()) + " " + stdHour + ":" + stdMinute);
		caSTD.setTime(paredDateSTD);
		Date paredDateSTA = AereoCRSConstants.DFSTDA
				.parse(AereoCRSConstants.DFFROMTO.format(calTo.getTime()) + " " + staHour + ":" + staMinute);
		caSTA.setTime(paredDateSTA);

		Calendar currentCalendar = Calendar.getInstance();
		currentCalendar.setTimeInMillis(currentInstant.toEpochMilli());

		String date = AereoCRSConstants.dateFormatter.format(Date.from(currentInstant)).toUpperCase();

		aeroFlight.setFltnumber(StringUtils.deleteWhitespace(fltnumber));
		aeroFlight.setAirlinename(AereoCRSConstants.AIRLINENAME);
		aeroFlight.setFromdestination(fromDestination);
		aeroFlight.setTodestination(toDestination);
		aeroFlight.setAirlineid(airlineID);
		aeroFlight.setAccode(accode);
		aeroFlight.setFltsfromperiod(AereoCRSConstants.DFFROMTO.format(calFrom.getTime()));
		aeroFlight.setFltstoperiod(AereoCRSConstants.DFFROMTO.format(calTo.getTime()));
		aeroFlight.setStd(AereoCRSConstants.dfs.format(caSTD.getTime()));
		aeroFlight.setSta(AereoCRSConstants.dfs.format(caSTA.getTime()));
		aeroFlight.setDate(date);
		aeroFlight.setSsimMessage(singleFlight);

		aeroFlight.setWeekdaymon(weekday == AereoCRSConstants.MONDAY);
		aeroFlight.setWeekdaytue(weekday == AereoCRSConstants.TUESDAY);
		aeroFlight.setWeekdaywed(weekday == AereoCRSConstants.WEDNESDAY);
		aeroFlight.setWeekdaythu(weekday == AereoCRSConstants.THURSDAY);
		aeroFlight.setWeekdayfri(weekday == AereoCRSConstants.FRIDAY);
		aeroFlight.setWeekdaysat(weekday == AereoCRSConstants.SATURDAY);
		aeroFlight.setWeekdaysun(weekday == AereoCRSConstants.SUNDAY);

		return aeroFlight;
	}
}
