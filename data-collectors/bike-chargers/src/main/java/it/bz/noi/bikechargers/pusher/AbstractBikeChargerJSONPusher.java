package it.bz.noi.bikechargers.pusher;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.ProvenanceDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.json.NonBlockingJSONPusher;
import it.bz.noi.bikechargers.configuration.BikeChargerConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;

public abstract class AbstractBikeChargerJSONPusher extends NonBlockingJSONPusher {

    private static final Logger LOG = LogManager.getLogger(AbstractBikeChargerJSONPusher.class);

    protected String origin;

    @Autowired
    protected Environment env;
    @Autowired
    protected BikeChargerConfiguration bikeChargerConfiguration;

    public <T> DataMapDto<RecordDtoImpl> mapData(T arg0) {
        throw new IllegalStateException("it is used by who?");
    }

    @PostConstruct
    public void init() {
        LOG.info("start init");
        origin = bikeChargerConfiguration.getOrigin();
        super.init();
        LOG.info("end init");
    }

    @Override
    public ProvenanceDto defineProvenance() {
        return new ProvenanceDto(null, env.getProperty("provenance_name"), env.getProperty("provenance_version"), origin);
    }
}
