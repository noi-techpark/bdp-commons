package it.bz.odh.trafficprovbz.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class AggregatedDataDto {

	@JsonProperty("IdPostazione")
	private int id;

	@JsonProperty("Data")
	private String date;

	@JsonProperty("Corsia")
	private String lane;

	@JsonProperty("TotaleVeicoli")
	private String totalTransits ;

	@JsonProperty("MediaArmonicaVelocita")
	private String averageVehicleSpeed ;

	@JsonProperty("HeadwayMedioSecondi")
	private String headway;

	@JsonProperty("VarianzaHeadwayMedioSecondi")
	private String headwayVariance;

	@JsonProperty("GapMedioSecondi")
	private String gap;

	@JsonProperty("VarianzaGapMedioSecondi")
	private String gapVariance;

	private final Map<String, Object> otherFields = new HashMap<>();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getLane() {
		return lane;
	}

	public void setLane(String lane) {
		this.lane = lane;
	}

	public String getTotalTransits() {
		return totalTransits;
	}

	public void setTotalTransits(String totalTransits) {
		this.totalTransits = totalTransits;
	}

	public String getAverageVehicleSpeed() {
		return averageVehicleSpeed;
	}

	public void setAverageVehicleSpeed(String averageVehicleSpeed) {
		this.averageVehicleSpeed = averageVehicleSpeed;
	}

	public String getHeadway() {
		return headway;
	}

	public void setHeadway(String headway) {
		this.headway = headway;
	}

	public String getHeadwayVariance() {
		return headwayVariance;
	}

	public void setHeadwayVariance(String headwayVariance) {
		this.headwayVariance = headwayVariance;
	}

	public String getGap() {
		return gap;
	}

	public void setGap(String gap) {
		this.gap = gap;
	}

	public String getGapVariance() {
		return gapVariance;
	}

	public void setGapVariance(String gapVariance) {
		this.gapVariance = gapVariance;
	}

	// Capture all other fields that Jackson do not match
	@JsonAnyGetter
	public Map<String, Object> getOtherFields() {
		return otherFields;
	}

	@JsonAnySetter
	public void setOtherField(String name, Object value) {
		otherFields.put(name, value);
	}
}
