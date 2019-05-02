package it.bz.idm.bdp.augeg4.fun.retrieve;

import it.bz.idm.bdp.augeg4.face.DataRetrieverFace;
import it.bz.idm.bdp.augeg4.face.DataServiceFace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

//@Component
public class DataRetriever implements DataRetrieverFace {

	/** Logging your efforts */
	private static final Logger LOG = LogManager.getLogger(DataRetriever.class.getName());

	/** If you need to fetch application property values, otherwise please delete */
	@Autowired
	private Environment env;


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


	@Override
	public void setDataService(DataServiceFace dataServiceFace) {

	}
}
