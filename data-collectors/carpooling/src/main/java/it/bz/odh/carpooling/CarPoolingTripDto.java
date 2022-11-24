package it.bz.odh.carpooling;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

public class CarPoolingTripDto {

	private String hashedId;
	private String status;
	private ZonedDateTime rideStartAtUtc;
	private Double rideDistanceKm;
	private Double rideDurationMinute;
	private ZonedDateTime rideCreatedAt;
	private ZonedDateTime rideModifiedAt;
	private Integer seatsReserved;
	private Double startLatApprox;
	private Double startLonApprox;
	private String startPostCode;
	private Double endLatApprox;
	private Double endLonApprox;
	private String endPostCode;

	public String getHashedId() {
		return hashedId;
	}

	public void setHashedId(String hashedId) {
		this.hashedId = hashedId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ZonedDateTime getRideStartAtUtc() {
		return rideStartAtUtc;
	}

	public void setRideStartAtUtc(ZonedDateTime rideStartAtUtc) {
		this.rideStartAtUtc = rideStartAtUtc;
	}

	public Double getRideDistanceKm() {
		return rideDistanceKm;
	}

	public void setRideDistanceKm(Double rideDistanceKm) {
		this.rideDistanceKm = rideDistanceKm;
	}

	public Double getRideDurationMinute() {
		return rideDurationMinute;
	}

	public void setRideDurationMinute(Double rideDurationMinute) {
		this.rideDurationMinute = rideDurationMinute;
	}

	public ZonedDateTime getRideCreatedAt() {
		return rideCreatedAt;
	}

	public void setRideCreatedAt(ZonedDateTime rideCreatedAt) {
		this.rideCreatedAt = rideCreatedAt;
	}

	public ZonedDateTime getRideModifiedAt() {
		return rideModifiedAt;
	}

	public void setRideModifiedAt(ZonedDateTime rideModifiedAt) {
		this.rideModifiedAt = rideModifiedAt;
	}

	public Integer getSeatsReserved() {
		return seatsReserved;
	}

	public void setSeatsReserved(Integer seatsReserved) {
		this.seatsReserved = seatsReserved;
	}

	public Double getStartLatApprox() {
		return startLatApprox;
	}

	public void setStartLatApprox(Double startLatApprox) {
		this.startLatApprox = startLatApprox;
	}

	public Double getStartLonApprox() {
		return startLonApprox;
	}

	public void setStartLonApprox(Double startLonApprox) {
		this.startLonApprox = startLonApprox;
	}

	public String getStartPostCode() {
		return startPostCode;
	}

	public void setStartPostCode(String startPostCode) {
		this.startPostCode = startPostCode;
	}

	public Double getEndLatApprox() {
		return endLatApprox;
	}

	public void setEndLatApprox(Double endLatApprox) {
		this.endLatApprox = endLatApprox;
	}

	public Double getEndLonApprox() {
		return endLonApprox;
	}

	public void setEndLonApprox(Double endLonApprox) {
		this.endLonApprox = endLonApprox;
	}

	public String getEndPostCode() {
		return endPostCode;
	}

	public void setEndPostCode(String endPostCode) {
		this.endPostCode = endPostCode;
	}

	public Map<String, Object> toJson() {
		Map<String, Object> itineraryMap = new HashMap<>();
		itineraryMap.put("status", status);
		itineraryMap.put("ride_start_at_UTC", rideStartAtUtc.toString());
		itineraryMap.put("ride_distance_km", rideDistanceKm);
		itineraryMap.put("ride_duration_minutes", rideDurationMinute);
		itineraryMap.put("ride_created_at_UTC", rideCreatedAt.toString());
		itineraryMap.put("seats_reserved", seatsReserved);
		itineraryMap.put("start_post_code", startPostCode);
		itineraryMap.put("end_post_code", endPostCode);
		itineraryMap.put("start_lat_approx", startLatApprox);
		itineraryMap.put("start_lon_approx", startLonApprox);
		return itineraryMap;
	}
}
