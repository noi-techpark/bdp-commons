package it.bz.tis.integreen.carsharingbzit.api;

public class Point {
	private Double lon;
	private Double lat;
	public Point() {
	}
	public Point(double lon, double lat) {
		this.lon = lon;
		this.lat = lat;
	}
	public Double getLon() {
		return lon;
	}
	public void setLon(Double lon) {
		this.lon = lon;
	}
	public Double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}

}
