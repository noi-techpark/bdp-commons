package it.bz.idm.bdp.dcparkingtn;

import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import it.bz.idm.bdp.dcparkingtn.dto.ParkingTnDto;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationList;

/**
 * Cronjob configuration can be found under src/main/resources/META-INF/spring/applicationContext.xml
 * XXX Do not forget to configure it!
 */
@Component
public class ParkingTnJobScheduler {

    private static final Logger LOG = LogManager.getLogger(ParkingTnJobScheduler.class.getName());

    @Autowired
    private ParkingTnDataPusher pusher;

    @Autowired
    private ParkingTnDataRetriever retrieval;

    /** JOB 1 */
    public void pushStations() throws Exception {
        LOG.debug("START.pushStations");

        try {
            List<ParkingTnDto> data = retrieval.fetchData();

            StationList stations = pusher.mapStations2Bdp(data);
            if (stations != null) {
                pusher.syncStations(stations);
            }
        } catch (HttpClientErrorException e) {
            LOG.error(pusher + " - " + e + " - " + e.getResponseBodyAsString(), e);
        }
        LOG.debug("END.pushStations");
    }

    /** JOB 2 */
    public void pushData() throws Exception {
        LOG.debug("START.pushData");

        try {
            List<ParkingTnDto> data = retrieval.fetchData();

            DataMapDto<RecordDtoImpl> stationRec = pusher.mapData(data);

            if (stationRec != null){
                pusher.pushData(stationRec);
            }

        } catch (Exception e) {
            LOG.error(pusher + " - " + e, e);
            throw e;
        }
        LOG.debug("END.pushData");
    }
}