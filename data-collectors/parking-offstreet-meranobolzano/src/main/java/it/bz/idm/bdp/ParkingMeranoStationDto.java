package it.bz.idm.bdp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;



@JsonIgnoreProperties(ignoreUnknown = true)
public class ParkingMeranoStationDto {
	@JsonProperty("AreaName")
	private String areaName;
	@JsonProperty("CurrentDateTime")
	private String currentDateTime;
	@JsonProperty("FreeParkingSpaces")
	private Integer freeParkingSpaces;
	@JsonProperty("TotalParkingSpaces")
	private Integer totalParkingSpaces;

	public ParkingMeranoStationDto() {

	}

	public ParkingMeranoStationDto(String areaName, String cuurrentDateTime, Integer freeParkingSlots,
			Integer totalParkingSlots) {
		this.areaName = areaName;
		this.currentDateTime = cuurrentDateTime;
		this.freeParkingSpaces = freeParkingSlots;
		this.totalParkingSpaces = totalParkingSlots;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getCurrentDateTime() {
		return currentDateTime;
	}

	public void setCurrentDateTime(String currentDateTime) {
		this.currentDateTime = currentDateTime;
	}

	public Integer getFreeParkingSpaces() {
		return freeParkingSpaces;
	}

	public void setFreeParkingSpaces(Integer freeParkingSpaces) {
		this.freeParkingSpaces = freeParkingSpaces;
	}

	public Integer getTotalParkingSpaces() {
		return totalParkingSpaces;
	}

	public void setTotalParkingSpaces(Integer totalParkingSpaces) {
		this.totalParkingSpaces = totalParkingSpaces;
	}
}