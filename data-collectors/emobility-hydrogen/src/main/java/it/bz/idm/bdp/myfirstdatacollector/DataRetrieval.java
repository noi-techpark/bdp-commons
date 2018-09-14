package it.bz.idm.bdp.myfirstdatacollector;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import it.bz.idm.bdp.myfirstdatacollector.dto.CoolDataDto;

@Component
@PropertySource({ "classpath:/META-INF/spring/application.properties" })
public class DataRetrieval {

	/** Logging your efforts */
	private static final Logger LOG = LogManager.getLogger(DataRetrieval.class.getName());

	/** If you need to fetch application property values, otherwise please delete */
	@Autowired
	private Environment env;

	/**
	 * Fetch data from where you want, to be integrated into the Open Data Hub.
	 * Insert logging for debugging and errors if needed, but do not prevent
	 * exceptions from being thrown to not hide any malfunctioning.
	 *
	 * @throws Exception
	 *             on error explode!
	 */
	public List<CoolDataDto> fetchData() throws Exception {
		try {
			String prefix = env.getProperty("station.prefix");

			/* Eliminate quotes if present */
			if (prefix.startsWith("\"") && prefix.endsWith("\""))
				prefix = prefix.substring(1, prefix.length() - 1);

			List<CoolDataDto> result = new ArrayList<CoolDataDto>();
			result.add(new CoolDataDto(prefix + "A", "temperature", "°C", 23.0, true, new Date()));
			LOG.debug("Fetching: " + result.get(0));
			result.add(new CoolDataDto(prefix + "A", "pressure", "bar", 1.2, true, new Date()));
			LOG.debug("Fetching: " + result.get(1));
			result.add(new CoolDataDto(prefix + "B", "temperature", "°C", 21.0, true, new Date()));
			LOG.debug("Fetching: " + result.get(2));
			result.add(new CoolDataDto(prefix + "B", "pressure", "bar", 1.5, true, new Date()));
			LOG.debug("Fetching: " + result.get(3));
			return result;
		} catch (Exception e) {
			LOG.error("ERROR: " + e.getMessage());
			e.printStackTrace();
			throw e; // always throw errors, we do not want to fail silently!
		}
	}
}
