package it.bz.odh.trafficprovbz.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class AggregatedDataDto {

	@JsonProperty("IdPostazione")
	private String id;

	@JsonProperty("Data")
	private String date;

	@JsonProperty("Corsia")
	private String lane;

	@JsonProperty("Direzione")
	private String direction;

	@JsonProperty("TotaleVeicoli")
	private Double totalTransits;

	@JsonProperty("MediaArmonicaVelocita")
	private Double averageVehicleSpeed;

	@JsonProperty("HeadwayMedioSecondi")
	private Double headway;

	@JsonProperty("VarianzaHeadwayMedioSecondi")
	private Double headwayVariance;

	@JsonProperty("GapMedioSecondi")
	private Double gap;

	@JsonProperty("VarianzaGapMedioSecondi")
	private Double gapVariance;

	private final Map<String, Object> otherFields = new HashMap<>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public Double getTotalTransits() {
		return totalTransits;
	}

	public void setTotalTransits(Double totalTransits) {
		this.totalTransits = totalTransits;
	}

	public Double getAverageVehicleSpeed() {
		return averageVehicleSpeed;
	}

	public void setAverageVehicleSpeed(Double averageVehicleSpeed) {
		this.averageVehicleSpeed = averageVehicleSpeed;
	}

	public Double getHeadway() {
		return headway;
	}

	public void setHeadway(Double headway) {
		this.headway = headway;
	}

	public Double getHeadwayVariance() {
		return headwayVariance;
	}

	public void setHeadwayVariance(Double headwayVariance) {
		this.headwayVariance = headwayVariance;
	}

	public Double getGap() {
		return gap;
	}

	public void setGap(Double gap) {
		this.gap = gap;
	}

	public Double getGapVariance() {
		return gapVariance;
	}

	public void setGapVariance(Double gapVariance) {
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
