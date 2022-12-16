package it.fos.noibz.skyalps.dto.json;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * @author Thierry BODHUIN, bodhuin@gmail.com
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AeroCRSFlight implements Serializable {

	private String fltnumber; // Flight number
	private String airlinename; // Airline name
	private String airlineid; // Number Number of airline ID
	private String fromdestination; // Destination FROM code
	private String todestination; // Destination TO code
	private String std; // HH:MM departure time
	private String sta; // HH:MM arrival time
	private String date; // DDMMMYY actual date 22NOV22
	private Long departureTimestamp; // unix timestamp
	private Long arrivalTimestamp; // unix timestamp
	private Boolean weekdaysun; // Sunday operating (true/false)
	private Boolean weekdaymon; // Monday operating (true/false)
	private Boolean weekdaytue; // Tuesday operating (true/false)
	private Boolean weekdaywed; // Wednesday operating (true/false)
	private Boolean weekdaythu; // Thursday operating (true/false)
	private Boolean weekdayfri; // Friday operating (true/false)
	private Boolean weekdaysat; // Saturday operating (true/false)
	private String accode; // Aircraft code (might not appear to all airlines)
	private String fltsfromperiod; // Start period of the flight active
	private String fltstoperiod; // End period of the flight active
	private String ssimMessage; // Whole SSIM message line for metadata

	public AeroCRSFlight() {
	}

	public AeroCRSFlight(String fltnumber, String airlinename, String airlineid, String fromdestination,
			String todestination, String std, String sta, Boolean weekdaysun, Boolean weekdaymon, Boolean weekdaytue,
			Boolean weekdaywed, Boolean weekdaythu, Boolean weekdayfri, Boolean weekdaysat, String accode,
			String fltsfromperiod, String fltstoperiod) {
		this.fltnumber = fltnumber;
		this.airlinename = airlinename;
		this.airlineid = airlineid;
		this.fromdestination = fromdestination;
		this.todestination = todestination;
		this.std = std;
		this.sta = sta;
		this.weekdaysun = weekdaysun;
		this.weekdaymon = weekdaymon;
		this.weekdaytue = weekdaytue;
		this.weekdaywed = weekdaywed;
		this.weekdaythu = weekdaythu;
		this.weekdayfri = weekdayfri;
		this.weekdaysat = weekdaysat;
		this.accode = accode;
		this.fltsfromperiod = fltsfromperiod;
		this.fltstoperiod = fltstoperiod;
	}

	public String getFltnumber() {
		return fltnumber;
	}

	public void setFltnumber(String fltnumber) {
		this.fltnumber = fltnumber;
	}

	public String getAirlinename() {
		return airlinename;
	}

	public void setAirlinename(String airlinename) {
		this.airlinename = airlinename;
	}

	public String getAirlineid() {
		return airlineid;
	}

	public void setAirlineid(String airlineid) {
		this.airlineid = airlineid;
	}

	public String getFromdestination() {
		return fromdestination;
	}

	public void setFromdestination(String fromdestination) {
		this.fromdestination = fromdestination;
	}

	public String getTodestination() {
		return todestination;
	}

	public void setTodestination(String todestination) {
		this.todestination = todestination;
	}

	public String getStd() {
		return std;
	}

	public void setStd(String std) {
		this.std = std;
	}

	public String getSta() {
		return sta;
	}

	public void setSta(String sta) {
		this.sta = sta;
	}

	public Boolean getWeekdaysun() {
		return weekdaysun;
	}

	public void setWeekdaysun(Boolean weekdaysun) {
		this.weekdaysun = weekdaysun;
	}

	public Boolean getWeekdaymon() {
		return weekdaymon;
	}

	public void setWeekdaymon(Boolean weekdaymon) {
		this.weekdaymon = weekdaymon;
	}

	public Boolean getWeekdaytue() {
		return weekdaytue;
	}

	public void setWeekdaytue(Boolean weekdaytue) {
		this.weekdaytue = weekdaytue;
	}

	public Boolean getWeekdaywed() {
		return weekdaywed;
	}

	public void setWeekdaywed(Boolean weekdaywed) {
		this.weekdaywed = weekdaywed;
	}

	public Boolean getWeekdaythu() {
		return weekdaythu;
	}

	public void setWeekdaythu(Boolean weekdaythu) {
		this.weekdaythu = weekdaythu;
	}

	public Boolean getWeekdayfri() {
		return weekdayfri;
	}

	public void setWeekdayfri(Boolean weekdayfri) {
		this.weekdayfri = weekdayfri;
	}

	public Boolean getWeekdaysat() {
		return weekdaysat;
	}

	public void setWeekdaysat(Boolean weekdaysat) {
		this.weekdaysat = weekdaysat;
	}

	public String getAccode() {
		return accode;
	}

	public void setAccode(String accode) {
		this.accode = accode;
	}

	public String getFltsfromperiod() {
		return fltsfromperiod;
	}

	public void setFltsfromperiod(String fltsfromperiod) {
		this.fltsfromperiod = fltsfromperiod;
	}

	public String getFltstoperiod() {
		return fltstoperiod;
	}

	public void setFltstoperiod(String fltstoperiod) {
		this.fltstoperiod = fltstoperiod;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getSsimMessage() {
		return ssimMessage;
	}

	public void setSsimMessage(String ssimMessage) {
		this.ssimMessage = ssimMessage;
	}

	public Long getDepartureTimestamp() {
		return departureTimestamp;
	}

	public void setDepartureTimestamp(Long departureTimestamp) {
		this.departureTimestamp = departureTimestamp;
	}

	public Long getArrivalTimestamp() {
		return arrivalTimestamp;
	}

	public void setArrivalTimestamp(Long arrivalTimestamp) {
		this.arrivalTimestamp = arrivalTimestamp;
	}

	@Override
	public String toString() {
		return "AeroCRSFlight{" + "fltnumber=" + fltnumber + ", airlinename=" + airlinename + ", airlineid=" + airlineid
				+ ", fromdestination=" + fromdestination + ", todestination=" + todestination + ", std=" + std
				+ ", sta=" + sta + ", weekdaysun=" + weekdaysun + ", weekdaymon=" + weekdaymon + ", weekdaytue="
				+ weekdaytue + ", weekdaywed=" + weekdaywed + ", weekdaythu=" + weekdaythu + ", weekdayfri="
				+ weekdayfri + ", weekdaysat=" + weekdaysat + ", accode=" + accode + ", fltsfromperiod="
				+ fltsfromperiod + ", fltstoperiod=" + fltstoperiod + '}';
	}

}
