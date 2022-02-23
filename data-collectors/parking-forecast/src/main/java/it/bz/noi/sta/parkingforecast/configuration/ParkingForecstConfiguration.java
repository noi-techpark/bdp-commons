package it.bz.noi.sta.parkingforecast.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:parkingforecast.properties")
public class ParkingForecstConfiguration {

	@Value( "${connector.endpoint}" )
    private String endpoint;

	@Value( "${origin}" )
	private String origin;

	@Value("${stationtype.parkingStation}")
	private String stationtypeParkingStation;

	@Value("${stationtype.parkingSensor}")
	private String stationtypeParkingSensor;

	public String getEndpoint() {
		return endpoint;
	}

	public String getOrigin() {
		return origin;
	}

	public String getStationtypeParkingStation() {
		return stationtypeParkingStation;
	}

	public String getStationtypeParkingSensor() {
		return stationtypeParkingSensor;
	}
}
