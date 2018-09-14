package it.bz.idm.bdp.myfirstdatacollector.dto;

import java.io.Serializable;
import java.util.Date;

public class CoolDataDto implements Serializable {
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

	public CoolDataDto(String station, String name, String unit, Double value, boolean active, Date date) {
		super();
		this.setUnit(unit);
		this.setDate(date);
		this.station = station;
		this.name = name;
		this.value = value;
		this.active = active;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getStation() {
		return station;
	}

	public void setStation(String station) {
		this.station = station;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	@Override
	public String toString() {
		return "CoolDataDto [station=" + station + ", name=" + name + ", unit=" + unit + ", value=" + value + ", active=" + active + ", date=" + date + "]";
	}

}
