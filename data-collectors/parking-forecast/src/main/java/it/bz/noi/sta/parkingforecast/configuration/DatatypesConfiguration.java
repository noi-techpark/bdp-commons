package it.bz.noi.sta.parkingforecast.configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

@Component
public class DatatypesConfiguration {

	private List<DatatypeConfiguration> allDataTypes;

	@PostConstruct
	private void init() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			allDataTypes = mapper.readValue(getClass().getResourceAsStream("/datatypes.json"), new TypeReference<List<DatatypeConfiguration>>() {});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public List<DatatypeConfiguration> getAllDataTypes() {
		return allDataTypes;
	}
}
