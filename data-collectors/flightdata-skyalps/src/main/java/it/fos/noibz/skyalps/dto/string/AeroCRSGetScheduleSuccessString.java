package it.fos.noibz.skyalps.dto.string;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

	private boolean success;
	private String flight;
	private List<AeroCRSFlight> flightsList;
	private static final int FIRSTSTRINGTOIGNORE = 0;
	private static final int SSIMSTRINGLENGHT = 200;
	private static final int MONTHFROMFIRST = 16;
	private static final int MONTHFROM = 17;
	private static final int MONTHFROMFINAL = 19;
	private static final int MONTHTOFIST = 23;
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
	private static final int TUESDTACHAR = 29;
	private static final int WEDCHAR = 30;
	private static final int THUCHAR = 31;
	private static final int FRICHAR = 32;
	private static final int SATCHAR = 33;
	private static final int SUNCHAR = 34;
	private static final String MONDAY = "1";
	private static final String TUESDAY = "2";
	private static final String WEDNESDAY = "3";
	private static final String THURSDAY = "4";
	private static final String FRIDAY = "5";
	private static final String SATURDAY = "6";
	private static final String SUNDAY = "7";
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
		AeroCRSGetScheduleSuccess flights = new AeroCRSGetScheduleSuccess();
		AeroCRSGetScheduleSuccessResponse flightFinal = new AeroCRSGetScheduleSuccessResponse();
		String[] flightArray = flight.trim().split("\\R");
		flightsList = new ArrayList<>();
		DateTimeFormatter parser = DateTimeFormatter.ofPattern("MMM").withLocale(Locale.ENGLISH);

		for (int i = 0; i < flightArray.length; i++) {

			AeroCRSFlight aeroFlight = new AeroCRSFlight();
			int flightSSIMLenght = flightArray[i].length();
			String flightIgnore = flightArray[FIRSTSTRINGTOIGNORE];
			//It won't retrieve strings with only Zeros, the first string that is not related to flights, and it will retrieve strings with the same lengths
			if (flightArray[i].contains(" ") && flightSSIMLenght == SSIMSTRINGLENGHT
					&& flightArray[i] != flightIgnore) {
				try {
					aeroFlight.setWeekdaymon(false);
					aeroFlight.setWeekdaytue(false);
					aeroFlight.setWeekdaywed(false);
					aeroFlight.setWeekdaythu(false);
					aeroFlight.setWeekdayfri(false);
					aeroFlight.setWeekdaysat(false);
					aeroFlight.setWeekdaysun(false);

					String fltnumber = flightArray[i].substring(FLTNUMBERSTART, FLTNUMBEREND);
					String monthfromComplete = flightArray[i].substring(MONTHFROM, MONTHFROMFINAL);
					String monthToComplete = flightArray[i].substring(MONTHTO, MONTHTOFINAL);
					String fromDestination = flightArray[i].substring(FROMDESTINATIONSTART, FROMDESTINATIONEND);
					String toDestination = flightArray[i].substring(TODESTINATIONSTART, TODESTINATIONEND);
					String airlineID = flightArray[i].substring(AIRLINEIDSTART, AIRLINEIDEND);
					String accode = flightArray[i].substring(ACCODESTART, ACCODEND);
					String yearFromComplete = flightArray[i].substring(YEAFROMSTART, YEARFROMEND);
					String yearToComplete = flightArray[i].substring(YEARTOSTART, YEARTOEND);
					String dayFromComplete = flightArray[i].substring(DAYFROMSTART, DAYFROMEND);
					String dayToComplete = flightArray[i].substring(DAYTOSTART, DAYTOEND);
					String stdHour = flightArray[i].substring(STDHOURSTART, STDHOUREND);
					String stdMinute = flightArray[i].substring(STDMINUTESTART, STDMINUTEEND);
					String staHour = flightArray[i].substring(STAHOURSTART, STAHOUREND);
					String staMinute = flightArray[i].substring(STAMINUTESTART, STAMINUTEND);

					TemporalAccessor accessorFrom = parser
							.parse(flightArray[i].charAt(MONTHFROMFIRST) + monthfromComplete.toLowerCase());
					TemporalAccessor accessorTo = parser
							.parse(flightArray[i].charAt(MONTHTOFIST) + monthToComplete.toLowerCase());

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
					if (String.valueOf(flightArray[i].charAt(MONDAYCHAR)).equals(MONDAY)) {
						aeroFlight.setWeekdaymon(true);
					}
					if (String.valueOf(flightArray[i].charAt(TUESDTACHAR)).equals(TUESDAY)) {
						aeroFlight.setWeekdaytue(true);
					}
					if (String.valueOf(flightArray[i].charAt(WEDCHAR)).equals(WEDNESDAY)) {
						aeroFlight.setWeekdaywed(true);
					}
					if (String.valueOf(flightArray[i].charAt(THUCHAR)).equals(THURSDAY)) {
						aeroFlight.setWeekdaythu(true);
					}
					if (String.valueOf(flightArray[i].charAt(FRICHAR)).equals(FRIDAY)) {
						aeroFlight.setWeekdayfri(true);
					}
					if (String.valueOf(flightArray[i].charAt(SATCHAR)).equals(SATURDAY)) {
						aeroFlight.setWeekdaysat(true);
					}
					if (String.valueOf(flightArray[i].charAt(SUNCHAR)).equals(SUNDAY)) {
						aeroFlight.setWeekdaysun(true);
					}
					flightsList.add(aeroFlight);

				} catch (Exception e) {

				}
			}
		}
		flights.setSuccess(true);
		flights.setFlight(flightsList);
		flightFinal.setAerocrs(flights);
		List<AeroCRSFlight> flightList = flightFinal.getAerocrs().getFlight();
		//Showing flights list
		/*for (AeroCRSFlight f : flightsList) {
			System.out.println(f);
		}*/
		return flightFinal;
	}
}
