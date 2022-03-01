package it.bz.noi.sta.parkingforecast.dto;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParkingForecastResult {

	private ZonedDateTime publishTimestamp;
	private ZonedDateTime forecastStartTimestamp;
	private Integer forecastPeriodSeconds;
	private Integer forecastDurationHours;

	private Map<String, List<ParkingForecastDataPoint>> stationTimeseriesMap = new HashMap<>();

	public ZonedDateTime getPublishTimestamp() {
		return publishTimestamp;
	}

	public void setPublishTimestamp(ZonedDateTime publishTimestamp) {
		this.publishTimestamp = publishTimestamp;
	}

	public ZonedDateTime getForecastStartTimestamp() {
		return forecastStartTimestamp;
	}

	public void setForecastStartTimestamp(ZonedDateTime forecastStartTimestamp) {
		this.forecastStartTimestamp = forecastStartTimestamp;
	}

	public Integer getForecastPeriodSeconds() {
		return forecastPeriodSeconds;
	}

	public void setForecastPeriodSeconds(Integer forecastPeriodSeconds) {
		this.forecastPeriodSeconds = forecastPeriodSeconds;
	}

	public Integer getForecastDurationHours() {
		return forecastDurationHours;
	}

	public void setForecastDurationHours(Integer forecastDurationHours) {
		this.forecastDurationHours = forecastDurationHours;
	}

	public Map<String, List<ParkingForecastDataPoint>> getStationTimeseriesMap() {
		return stationTimeseriesMap;
	}

	public void addStationTimeseries(String scode, List<ParkingForecastDataPoint> parkingForecastData) {
		this.stationTimeseriesMap.put(scode, parkingForecastData);
	}
}
