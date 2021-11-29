package it.bz.tis.zrx2json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Meteostation {
//	private String SANR;
//	private String SNAME;
//	private String PNP;
//	private String SSNR;
//	private Integer VOLATILE;
//	private String SWATER;
//	private Integer CDASA;
//	private String CDASANAME;
//	private String CCHANNEL;
//	private String CCHANNELNO;
//	private String CKONV;
//	private Integer CMW;
//	private String CNAME;
//	private String CNR;
//	private String CNTYPE;
//	private String CTYPE;
//	private String CUNIT;
//	private Integer CTABLE;
//	private String REXCHANGE;
//	private String REXTR;
//	private Integer RID;
//	private Boolean RIMPORT;
//	private Double RINVAL;
//	private Integer RNR;
//	private String RORPR;
//	private String RSTATE;
//	private String RTIMELVL;
//	private String RTYPE;
//	private Integer XVLID;
//	private String CTAG;
//	private String CTAGKEY;
//	private Boolean XTRUNCATE;
//	private String XCLEAN;
//	private String METCODE;
//	private String EDIS;
//	private String EUNIT;
//	private String TZ;
//	private String ZDATE;
//	private String CINSTANT;
//	private String METERSITE;
//	private String REMDST;
//	private String EQFLAG;
//	private String ZRXPVERSION;
//	private String ZRXPMODE;
//	private String LAYOUT;
//	private String ZRXPCREATOR;
	
	@JsonIgnore
	public static final String[] PARAMETER = new String[] {"SANR",
	"SNAME",
	"PNP",
	"SSNR",
	"VOLATILE",
	"SWATER",
	"CDASA",
	"CDASANAME",
	"CCHANNEL",
	"CCHANNELNO",
	"CKONV",
	"CMW",
	"CNAME",
	"CNR",
	"CNTYPE",
	"CTYPE",
	"CUNIT",
	"CTABLE",
	"REXCHANGE",
	"REXTR",
	"RID",
	"RIMPORT",
	"RINVAL",
	"RNR",
	"RORPR",
	"RSTATE",
	"RTIMELVL",
	"RTYPE",
	"XVLID",
	"CTAG",
	"CTAGKEY",
	"XTRUNCATE",
	"XCLEAN",
	"METCODE",
	"EDIS",
	"EUNIT",
	"TZ",
	"ZDATE",
	"CINSTANT",
	"METERSITE",
	"REMDST",
	"EQFLAG",
	"ZRXPVERSION",
	"ZRXPMODE",
	"LAYOUT",
	"ZRXPCREATOR"};
	
	private Map<String,String> metaData= new HashMap<String,String>();
	
	private List<MeteoStationDataPoint> dataPoints;
	
	private List<String> comments;
	

	public void addParams(String headerLine) {
		String params[];
		params = headerLine.split("(\\|\\*\\|)|(;\\*;)");
		for (String param:params){
			for(String name:PARAMETER){
				if (param.startsWith(name))
					metaData.put(name, param.substring(name.length()));
			}
		}	
		
	}

	public Map<String, String> getMetaData() {
		return metaData;
	}

	public void setMetaData(Map<String, String> metaData) {
		this.metaData = metaData;
	}

	public List<MeteoStationDataPoint> getDataPoints() {
		return dataPoints;
	}

	public void setDataPoints(List<MeteoStationDataPoint> dataPoints) {
		this.dataPoints = dataPoints;
	}

	public List<String> getComments() {
		return comments;
	}

	public void setComments(List<String> comments) {
		this.comments = comments;
	}
	public void addDataPoint(String line) {
		if (this.dataPoints == null)
			dataPoints = new ArrayList<MeteoStationDataPoint>();
		String[] values = line.split(" ");
		MeteoStationDataPoint point = new MeteoStationDataPoint();
			point.setDate(values[0]);
			point.setDataPoint(Double.parseDouble(values[1]));
		if (values.length > 2)
			point.setStatusFlag(Integer.parseInt(values[2]));
		if (values.length > 3)
			point.setComment(values[3]);
		dataPoints.add(point);
	}

	public void addComment(String line) {
		if (!line.isEmpty()){
			if (comments == null)
				comments = new ArrayList<String>();
			comments.add(line);
		}
		
	}
}
