package it.bz.noi.onstreetparking.dto;

import java.time.ZonedDateTime;

public class ParkingData {
	private String type;
	private String guid;
	private String name;
	private Position position;
	private String state;
	private ZonedDateTime lastChange;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public ZonedDateTime getLastChange() {
		return lastChange;
	}

	public void setLastChange(ZonedDateTime lastChange) {
		this.lastChange = lastChange;
	}
}
