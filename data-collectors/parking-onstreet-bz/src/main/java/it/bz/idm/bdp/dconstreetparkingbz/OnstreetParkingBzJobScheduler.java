// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.dconstreetparkingbz;

import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.StationList;

/**
 * Cronjob configuration can be found under src/main/resources/META-INF/spring/applicationContext.xml
 * XXX Do not forget to configure it!
 */
@Component
@PropertySource({ "classpath:/META-INF/spring/application.properties" })
public class OnstreetParkingBzJobScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(OnstreetParkingBzJobScheduler.class.getName());

    @Autowired
    private OnstreetParkingBzDataPusher pusher;

    @Autowired
    private OnstreetParkingBzDataRetriever retriever;

    /** JOB 1 */
    public void pushDataTypes() throws Exception {
        LOG.info("START.pushDataTypes");

        try {
            List<DataTypeDto> dataTypes = pusher.mapDataTypes2Bdp();

            if (dataTypes != null){
                pusher.syncDataTypes(dataTypes);
            }

        } catch (HttpClientErrorException e) {
            LOG.error(pusher + " - " + e + " - " + e.getResponseBodyAsString(), e);
            throw e;
        } catch (Exception e) {
            LOG.error(pusher + " - " + e, e);
            throw e;
        }
        LOG.info("END.pushDataTypes");
    }

    /** JOB 2 */
    public void pushStations() throws Exception {
        LOG.info("START.pushStations");

        try {
            StationList stations = retriever.fetchStations();
            if (stations != null) {
                pusher.syncStations(OnstreetParkingBzDataConverter.STATION_TYPE_SENSOR, stations);
            }

        } catch (HttpClientErrorException e) {
            LOG.error(pusher + " - " + e + " - " + e.getResponseBodyAsString(), e);
            throw e;
        } catch (Exception e) {
            LOG.error(pusher + " - " + e, e);
            throw e;
        }

        LOG.info("END.pushStations");
    }

//    /** JOB 3 */
//    public void pushData() throws Exception {
//        LOG.info("START.pushData");
//
//
//        LOG.info("END.pushData");
//    }

}