package it.bz.idm.bdp.augeg4.fun.retrieve;

import it.bz.idm.bdp.augeg4.ConnectorConfig;
import it.bz.idm.bdp.augeg4.dto.fromauge.AugeG4ElaboratedDataDto;
import it.bz.idm.bdp.augeg4.face.DataRetrieverFace;
import it.bz.idm.bdp.augeg4.util.AugeMqttConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataRetriever implements DataRetrieverFace {

	@Autowired
	ConnectorConfig config;

	/** Logging your efforts */
	private static final Logger LOG = LogManager.getLogger(DataRetriever.class.getName());

	private AugeSubscriber augeSubscriber;

	private AugeCallback augeCallback;

	public DataRetriever(ConnectorConfig config) {
		this.config = config;
		augeSubscriber = new AugeSubscriber();
		augeCallback = augeSubscriber.listen(AugeMqttConfiguration.buildMqttSubscriberConfiguration(config));
	}

    @Override
	public List<AugeG4ElaboratedDataDto> fetchData() {
		return augeCallback.fetchData();
	}

    @Override
    public void stop() {
        augeSubscriber.stop();
    }
}
