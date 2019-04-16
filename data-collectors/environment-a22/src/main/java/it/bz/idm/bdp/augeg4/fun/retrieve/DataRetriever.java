package it.bz.idm.bdp.augeg4.fun.retrieve;

import it.bz.idm.bdp.augeg4.dto.fromauge.AugeG4FromAlgorabDataDto;
import it.bz.idm.bdp.augeg4.face.DataRetrieverFace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@PropertySource({ "classpath:/META-INF/spring/application.properties" })
public class DataRetriever implements DataRetrieverFace {

	/** Logging your efforts */
	private static final Logger LOG = LogManager.getLogger(DataRetriever.class.getName());

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
	@Override
	public List<AugeG4FromAlgorabDataDto> fetchData() throws Exception {
		LOG.info("DataRetriever.fetchData()");
		String prefix = getPrefix();
		// TODO fetchData() body
		return new ArrayList<>();
	}


	private String getPrefix() {
		String prefix = "";
		try {
			prefix = env.getProperty("station.prefix");

			if (prefix.startsWith("\"") && prefix.endsWith("\""))
				prefix = prefix.substring(1, prefix.length() - 1);

		} catch (Exception e) {
			LOG.error("ERROR: {}", e.getMessage());
			e.printStackTrace();
			throw e; // always throw errors, we do not want to fail silently!
		}
		return prefix;
	}


}
