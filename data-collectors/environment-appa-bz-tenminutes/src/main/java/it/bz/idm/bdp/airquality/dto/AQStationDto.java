package it.bz.idm.bdp.airquality.dto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class AQStationDto {
	int station;
	int hour; // We use a 24-h clock
	int minute;
	int second;
	int day;
	int month;
	int year;
	List<AQBlockDto> blocks;

	/*
	 * The following station details are not used, because they are either static,
	 * or do not correspond to right values after filter-steps:
	 * - arteryType: dynamically structured artery (always 6)
	 * - datasetType: statistics (always 0)
	 * - blockCount: we filter blocks, hence the actual count differs from the input count
	 */

	public AQStationDto() {
		blocks = new ArrayList<AQBlockDto>();
	}

	public int getStation() {
		return station;
	}

	public void setStation(int station) {
		this.station = station;
	}
	public int getHour() {
		return hour;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}
	public int getMinute() {
		return minute;
	}
	public void setMinute(int minute) {
		this.minute = minute;
	}
	public int getSecond() {
		return second;
	}
	public void setSecond(int second) {
		this.second = second;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}

	public long getTimestamp() {
		if (year < 100)
			year += 2000;
		LocalDateTime dt = LocalDateTime.of(year, month, day, hour, minute, second);
		ZonedDateTime zdt = dt.atZone(ZoneId.of("Europe/Rome"));
		return zdt.toEpochSecond() * 1000;
	}

	public void addBlock(AQBlockDto block) {
		if (block == null)
			return;
		blocks.add(block);
	}
	public List<AQBlockDto> getBlocks() {
		return blocks;
	}

	@Override
	public String toString() {
		return "AQStationDto [station=" + station + ", hour=" + hour + ", minute=" + minute + ", second=" + second
				+ ", day=" + day + ", month=" + month + ", year=" + year + ", blocks=" + blocks + "]";
	}
}
