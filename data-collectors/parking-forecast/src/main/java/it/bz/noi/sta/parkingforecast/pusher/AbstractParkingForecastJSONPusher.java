/*
 *  Abstract Parking Forecast Json Pusher
 *
 *  (C) 2021 NOI Techpark Südtirol / Alto Adige
 *
 *  changelog:
 *  2022-02-23  1.0 - thomas.nocker@catch-solve.tech
 */

package it.bz.noi.sta.parkingforecast.pusher;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.ProvenanceDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.json.NonBlockingJSONPusher;
import it.bz.noi.sta.parkingforecast.configuration.ParkingForecstConfiguration;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;

@PropertySource("classpath:odh-writer.properties")
public abstract class AbstractParkingForecastJSONPusher extends NonBlockingJSONPusher {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractParkingForecastJSONPusher.class);

    protected String origin;

    @Autowired
    protected Environment env;
    @Autowired
    protected ParkingForecstConfiguration connectorConfiguration;

    public <T> DataMapDto<RecordDtoImpl> mapData(T arg0) {
        throw new IllegalStateException("it is used by who?");
    }

    @PostConstruct
    public void init() {
        LOG.info("start init");
        origin = connectorConfiguration.getOrigin();
        super.init();
        LOG.info("end init");
    }

    @Override
    public ProvenanceDto defineProvenance() {
        return new ProvenanceDto(null, env.getProperty("provenance_name"), env.getProperty("provenance_version"), origin);
    }
}
