// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.noi.onstreetparking.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:onstreetparking.properties")
public class OnStreetParkingConfiguration {

	@Value( "${origin}" )
	private String origin;

	@Value("${stationtype}")
	private String stationtype;

	@Value("${period}")
	private Integer period;

	@Value("${maxTimeSinceLastMeasurementSeconds}")
	private Integer maxTimeSinceLastMeasurementSeconds;

	public String getOrigin() {
		return origin;
	}

	public String getStationtype() {
		return stationtype;
	}

	public Integer getPeriod() {
		return period;
	}

	public Integer getMaxTimeSinceLastMeasurementSeconds() {
		return maxTimeSinceLastMeasurementSeconds;
	}
}
