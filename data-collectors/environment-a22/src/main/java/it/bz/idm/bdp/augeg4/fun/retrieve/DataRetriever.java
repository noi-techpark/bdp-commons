package it.bz.idm.bdp.augeg4.fun.retrieve;

import it.bz.idm.bdp.augeg4.ConnectorConfig;
import it.bz.idm.bdp.augeg4.dto.fromauge.AugeG4ElaboratedDataDto;
import it.bz.idm.bdp.augeg4.face.DataRetrieverFace;
import it.bz.idm.bdp.augeg4.util.AugeMqttConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataRetriever implements DataRetrieverFace, ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	ConnectorConfig config;

	private AugeSubscriber augeSubscriber;


	public DataRetriever(ConnectorConfig config) {
		this.config = config;
		augeSubscriber = new AugeSubscriber();
	}

    @Override
	public List<AugeG4ElaboratedDataDto> fetchData() {
		AugeCallback augeCallback;
		augeCallback = augeSubscriber.listen(AugeMqttConfiguration.buildMqttSubscriberConfiguration(config));
		return augeCallback.fetchData();
	}

    @Override
    public void stop() {
        augeSubscriber.stop();
    }

	@Override
	public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
	}


}
