/*
 *  Parking Forecast Data Point
 *
 *  (C) 2021 NOI Techpark Südtirol / Alto Adige
 *
 *  changelog:
 *  2022-02-23  1.0 - thomas.nocker@catch-solve.tech
 */

package it.bz.noi.sta.parkingforecast.dto;

import java.time.ZonedDateTime;

public class ParkingForecastDataPoint {

	private ZonedDateTime ts;
	private Double lo;
	private Double mean;
	private Double hi;

	public ZonedDateTime getTs() {
		return ts;
	}

	public void setTs(ZonedDateTime ts) {
		this.ts = ts;
	}

	public Double getLo() {
		return lo;
	}

	public void setLo(Double lo) {
		this.lo = lo;
	}

	public Double getMean() {
		return mean;
	}

	public void setMean(Double mean) {
		this.mean = mean;
	}

	public Double getHi() {
		return hi;
	}

	public void setHi(Double hi) {
		this.hi = hi;
	}
}
