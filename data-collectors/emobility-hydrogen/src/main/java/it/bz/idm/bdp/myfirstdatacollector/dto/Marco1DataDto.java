package it.bz.idm.bdp.myfirstdatacollector.dto;

import java.io.Serializable;
import java.util.Date;

public class Marco1DataDto implements Serializable {
	private static final long serialVersionUID = 8642860252556395832L;

	/*
	 * We define some values that we want to gather from a data source.
	 */
	private String station;
	private String name;
	private String unit;
	private Double value;
	private boolean active;
	private Date date;

	private String attr_DATE;
    private String attr_SCODE;
    private String attr_UNIT;
    private String attr_MCODE;
    private String attr_DESC_I;
    private String attr_CAT;
    private String attr_DESC_D;
    private String attr_VALUE;
    private String attr_TYPE;

    public Marco1DataDto(String station, String attr_DATE, String attr_SCODE, String attr_UNIT, String attr_MCODE,
			String attr_DESC_I, String attr_CAT, String attr_DESC_D, String attr_VALUE, String attr_TYPE) {
		super();
		this.station = station;
		this.attr_DATE = attr_DATE;
		this.attr_SCODE = attr_SCODE;
		this.attr_UNIT = attr_UNIT;
		this.attr_MCODE = attr_MCODE;
		this.attr_DESC_I = attr_DESC_I;
		this.attr_CAT = attr_CAT;
		this.attr_DESC_D = attr_DESC_D;
		this.attr_VALUE = attr_VALUE;
		this.attr_TYPE = attr_TYPE;
	}

	public String getStation() {
		return station;
	}

	public void setStation(String station) {
		this.station = station;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getAttr_DATE() {
		return attr_DATE;
	}

	public void setAttr_DATE(String attr_DATE) {
		this.attr_DATE = attr_DATE;
		try {
			setDate(new Date(Date.parse(attr_DATE)));
		} catch (Exception e) {
			System.out.println("Exception setting date '"+attr_DATE+"' - "+e);
		}
	}

	public String getAttr_SCODE() {
		return attr_SCODE;
	}

	public void setAttr_SCODE(String attr_SCODE) {
		this.attr_SCODE = attr_SCODE;
	}

	public String getAttr_UNIT() {
		return attr_UNIT;
	}

	public void setAttr_UNIT(String attr_UNIT) {
		this.attr_UNIT = attr_UNIT;
	}

	public String getAttr_MCODE() {
		return attr_MCODE;
	}

	public void setAttr_MCODE(String attr_MCODE) {
		this.attr_MCODE = attr_MCODE;
	}

	public String getAttr_DESC_I() {
		return attr_DESC_I;
	}

	public void setAttr_DESC_I(String attr_DESC_I) {
		this.attr_DESC_I = attr_DESC_I;
	}

	public String getAttr_CAT() {
		return attr_CAT;
	}

	public void setAttr_CAT(String attr_CAT) {
		this.attr_CAT = attr_CAT;
	}

	public String getAttr_DESC_D() {
		return attr_DESC_D;
	}

	public void setAttr_DESC_D(String attr_DESC_D) {
		this.attr_DESC_D = attr_DESC_D;
	}

	public String getAttr_VALUE() {
		return attr_VALUE;
	}

	public void setAttr_VALUE(String attr_VALUE) {
		this.attr_VALUE = attr_VALUE;
		try {
			setValue(Double.valueOf(attr_VALUE));
		} catch (Exception e) {
			System.out.println("Exception setting value '"+attr_VALUE+"' - "+e);
		}
	}

	public String getAttr_TYPE() {
		return attr_TYPE;
	}

	public void setAttr_TYPE(String attr_TYPE) {
		this.attr_TYPE = attr_TYPE;
	}

}
