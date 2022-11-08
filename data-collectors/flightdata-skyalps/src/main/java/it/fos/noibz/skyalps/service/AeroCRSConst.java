package it.fos.noibz.skyalps.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class AeroCRSConst {
//This class contains all constant variables used in the SynchScheduler class
	private static final String AIRLINE_ID = "airlineid";
	private static final String AIRLINENAME = "airlinename";
	private static final String ACCODE = "accode";
	private static final String FLTNUMBER = "fltnumber";
	private static final String FLTSTOPERIOD = "fltstoperiod";
	private static final String FLTSFROMPERIOD = "fltsfromperiod";
	private static final String STA = "sta";
	private static final String STD = "std";
	private static final String WEEKDAYMON = "weekdaymon";
	private static final String WEEKDAYTUE = "weekdaytue";
	private static final String WEEKDAYWED = "weekdaywed";
	private static final String WEEKDAYTHU = "weekdaythu";
	private static final String WEEKDAYFRI = "weekdayfri";
	private static final String WEEKDAYSAT = "weekdaysat";
	private static final String WEEKDAYSUN = "weekdaysun";
	private static final String IATACODE = "IATA";
	private static final String COMPANYCODE = "BN";
	private static final String FROMTODESTINATION = "fromdestionation-todestination";
	private static final String ENVPERIOD = "period";
	private static final String FROMDESTINATION = "fromdestination";
	private static final String TODESTINATION = "todestination";
	@Value("${auth.id}")
	private String authid;
	@Value("${auth.password}")
	private String authpassword;
	
	private static String AUTHID_STATIC;
	private static String AUTHPASSWORD_STATIC;

	public static String getFromdestination() {
		return FROMDESTINATION;
	}

	public static String getTodestination() {
		return TODESTINATION;
	}

	public static String getAirlineId() {
		return AIRLINE_ID;
	}

	public static String getAirlinename() {
		return AIRLINENAME;
	}

	public static String getAccode() {
		return ACCODE;
	}

	public static String getFltnumber() {
		return FLTNUMBER;
	}

	public static String getFltstoperiod() {
		return FLTSTOPERIOD;
	}

	public static String getFltsfromperiod() {
		return FLTSFROMPERIOD;
	}

	public static String getSta() {
		return STA;
	}

	public static String getStd() {
		return STD;
	}

	public static String getWeekdaymon() {
		return WEEKDAYMON;
	}

	public static String getWeekdaytue() {
		return WEEKDAYTUE;
	}

	public static String getWeekdaywed() {
		return WEEKDAYWED;
	}

	public static String getWeekdaythu() {
		return WEEKDAYTHU;
	}

	public static String getWeekdayfri() {
		return WEEKDAYFRI;
	}

	public static String getWeekdaysat() {
		return WEEKDAYSAT;
	}

	public static String getWeekdaysun() {
		return WEEKDAYSUN;
	}

	public static String getIatacode() {
		return IATACODE;
	}

	public static String getCompanycode() {
		return COMPANYCODE;
	}

	public static String getFromtodestination() {
		return FROMTODESTINATION;
	}

	public static String getEnvperiod() {
		return ENVPERIOD;
	}
	
	 @Value("${auth.id}")
	    public void setAUTHIDStatic(String authid){
	        AeroCRSConst.AUTHID_STATIC = authid;
	    }
	 
	 @Value("${auth.password}")
	    public void setPASSWORDIDStatic(String authpassword){
	        AeroCRSConst.AUTHPASSWORD_STATIC = authpassword;
	    }

	public String getAuthid() {
		return authid;
	}

	public String getAuthpassword() {
		return authpassword;
	}

	public static String getAUTHID_STATIC() {
		return AUTHID_STATIC;
	}
	public static String getAUTHPASSWORD_STATIC() {
		return AUTHPASSWORD_STATIC;
	}
	 
}
