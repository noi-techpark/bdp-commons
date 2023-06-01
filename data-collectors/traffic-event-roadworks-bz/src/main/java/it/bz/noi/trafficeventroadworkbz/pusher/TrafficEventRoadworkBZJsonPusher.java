// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.noi.trafficeventroadworkbz.pusher;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.ProvenanceDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.json.NonBlockingJSONPusher;
import it.bz.noi.trafficeventroadworkbz.configuration.TrafficEventRoadworkBZConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class TrafficEventRoadworkBZJsonPusher extends NonBlockingJSONPusher {

    private static final Logger LOG = LoggerFactory.getLogger(TrafficEventRoadworkBZJsonPusher.class);

    private String integreenTypology;
    private String origin;
	private String provenanceName;
	private String provenanceVersion;

    @Autowired
    protected TrafficEventRoadworkBZConfiguration configuration;

    public <T> DataMapDto<RecordDtoImpl> mapData(T arg0) {
        throw new IllegalStateException("it is used by who?");
    }

    @Override
    @PostConstruct
    public void init() {
        LOG.info("start init");
        integreenTypology = configuration.getIntegreenTypology();
        origin = configuration.getOrigin();
		provenanceName = configuration.getProvenanceName();
		provenanceVersion = configuration.getProvenanceVersion();
        super.init();
        LOG.info("end init");
    }

    @Override
    public String initIntegreenTypology() {
        return integreenTypology;
    }

    @Override
    public ProvenanceDto defineProvenance() {
        return new ProvenanceDto(null, provenanceName, provenanceVersion, origin);
    }
}
