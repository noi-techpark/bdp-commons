package it.bz.noi.sta.parkingforecast.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Configuration
@PropertySource("classpath:datatypes.properties")
public class DatatypesConfiguration {

	@Value("#{${parkingForecast30}}")
	private Map<String, String> parkingForecast30Map;

	@Value("#{${parkingForecast60}}")
	private Map<String, String> parkingForecast60Map;

	@Value("#{${parkingForecast90}}")
	private Map<String, String> parkingForecast90Map;

	@Value("#{${parkingForecast120}}")
	private Map<String, String> parkingForecast120Map;

	@Value("#{${parkingForecast150}}")
	private Map<String, String> parkingForecast150Map;

	@Value("#{${parkingForecast180}}")
	private Map<String, String> parkingForecast180Map;

	@Value("#{${parkingForecast210}}")
	private Map<String, String> parkingForecast210Map;

	@Value("#{${parkingForecast240}}")
	private Map<String, String> parkingForecast240Map;

	private DatatypeConfiguration parkingForecast30;
	private DatatypeConfiguration parkingForecast60;
	private DatatypeConfiguration parkingForecast90;
	private DatatypeConfiguration parkingForecast120;
	private DatatypeConfiguration parkingForecast150;
	private DatatypeConfiguration parkingForecast180;
	private DatatypeConfiguration parkingForecast210;
	private DatatypeConfiguration parkingForecast240;

	@PostConstruct
	private void init() {
		ObjectMapper mapper = new ObjectMapper();
		parkingForecast30 = mapper.convertValue(parkingForecast30Map, DatatypeConfiguration.class);
		parkingForecast60 = mapper.convertValue(parkingForecast60Map, DatatypeConfiguration.class);
		parkingForecast90 = mapper.convertValue(parkingForecast90Map, DatatypeConfiguration.class);
		parkingForecast120 = mapper.convertValue(parkingForecast120Map, DatatypeConfiguration.class);
		parkingForecast150 = mapper.convertValue(parkingForecast150Map, DatatypeConfiguration.class);
		parkingForecast180 = mapper.convertValue(parkingForecast180Map, DatatypeConfiguration.class);
		parkingForecast210 = mapper.convertValue(parkingForecast210Map, DatatypeConfiguration.class);
		parkingForecast240 = mapper.convertValue(parkingForecast240Map, DatatypeConfiguration.class);
	}

	public DatatypeConfiguration getParkingForecast30() {
		return parkingForecast30;
	}

	public DatatypeConfiguration getParkingForecast60() {
		return parkingForecast60;
	}

	public DatatypeConfiguration getParkingForecast90() {
		return parkingForecast90;
	}

	public DatatypeConfiguration getParkingForecast120() {
		return parkingForecast120;
	}

	public DatatypeConfiguration getParkingForecast150() {
		return parkingForecast150;
	}

	public DatatypeConfiguration getParkingForecast180() {
		return parkingForecast180;
	}

	public DatatypeConfiguration getParkingForecast210() {
		return parkingForecast210;
	}

	public DatatypeConfiguration getParkingForecast240() {
		return parkingForecast240;
	}

	public List<DatatypeConfiguration> getAllDataTypes() {
		return Arrays.asList(parkingForecast30, parkingForecast60, parkingForecast90, parkingForecast120, parkingForecast150, parkingForecast180, parkingForecast210, parkingForecast240);
	}
}
