// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.dcbikesharingmoqo;

import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import it.bz.idm.bdp.dcbikesharingmoqo.dto.BikeDto;
import it.bz.idm.bdp.dcbikesharingmoqo.dto.BikesharingMoqoDto;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationList;

/**
 * Cronjob configuration can be found under src/main/resources/META-INF/spring/applicationContext.xml
 * XXX Do not forget to configure it!
 */
@Component
public class BikesharingMoqoJobScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(BikesharingMoqoJobScheduler.class.getName());

    @Autowired
    private BikesharingMoqoDataPusher pusher;

    @Autowired
    private BikesharingMoqoDataRetriever retriever;

    /** JOB 1 */
    public void pushData() throws Exception {
        LOG.info("START.pushData");

        try {
            BikesharingMoqoDto moqoDto = retriever.fetchData();
            List<BikeDto> data = moqoDto.getBikeList();

            StationList stations = pusher.mapStations2Bdp(data);
            if (stations != null) {
                pusher.syncStations(stations);
            }

            DataMapDto<RecordDtoImpl> stationRec = pusher.mapData(data);
            if (stationRec != null){
                pusher.pushData(stationRec);
            }

        } catch (HttpClientErrorException e) {
            LOG.error(pusher + " - " + e + " - " + e.getResponseBodyAsString(), e);
            throw e;
        } catch (Exception e) {
            LOG.error(pusher + " - " + e, e);
            throw e;
        }
        LOG.info("END.pushData");
    }

    /** JOB 2 */
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

//    /** JOB 3 */
//    public void pushData() throws Exception {
//        LOG.info("START.pushData");
//
//        try {
//
//            //Fetch all measurements
//            List<BikeDto> data = retriever.fetchData();
//
//            //Push all measurements in a single call
//            DataMapDto<RecordDtoImpl> stationRec = pusher.mapData(data);
//
//            if (stationRec != null){
//                pusher.pushData(stationRec);
//            }
//
//        } catch (HttpClientErrorException e) {
//            LOG.error(pusher + " - " + e + " - " + e.getResponseBodyAsString(), e);
//            throw e;
//        } catch (Exception e) {
//            LOG.error(pusher + " - " + e, e);
//            throw e;
//        }
//        LOG.info("END.pushData");
//    }
}