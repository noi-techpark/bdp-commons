package it.bz.noi.trafficeventroadworkbz.pusher;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.ProvenanceDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.json.NonBlockingJSONPusher;
import it.bz.noi.trafficeventroadworkbz.configuration.TrafficEventRoadworkBZConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class TrafficEventRoadworkBZJsonPusher extends NonBlockingJSONPusher {

    private static final Logger LOG = LogManager.getLogger(TrafficEventRoadworkBZJsonPusher.class);

    private String integreenTypology;
    private String origin;

    @Autowired
    private Environment env;
    @Autowired
    protected TrafficEventRoadworkBZConfiguration configuration;

    public <T> DataMapDto<RecordDtoImpl> mapData(T arg0) {
        throw new IllegalStateException("it is used by who?");
    }

    @PostConstruct
    public void init() {
        LOG.info("start init");
        integreenTypology = configuration.getIntegreenTypology();
        origin = configuration.getOrigin();
        super.init();
        LOG.info("end init");
    }

    @Override
    public String initIntegreenTypology() {
        return integreenTypology;
    }

    @Override
    public ProvenanceDto defineProvenance() {
        return new ProvenanceDto(null, env.getProperty("provenance_name"), env.getProperty("provenance_version"), origin);
    }
}