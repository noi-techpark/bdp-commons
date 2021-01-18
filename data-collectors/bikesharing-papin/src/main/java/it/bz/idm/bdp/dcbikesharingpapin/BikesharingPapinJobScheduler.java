package it.bz.idm.bdp.dcbikesharingpapin;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import it.bz.idm.bdp.dcbikesharingpapin.dto.BikesharingPapinDto;
import it.bz.idm.bdp.dcbikesharingpapin.dto.BikesharingPapinStationDto;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationList;

/**
 * Cronjob configuration can be found under src/main/resources/META-INF/spring/applicationContext.xml
 * XXX Do not forget to configure it!
 */
@Component
public class BikesharingPapinJobScheduler {

    private static final Logger LOG = LogManager.getLogger(BikesharingPapinJobScheduler.class.getName());

    @Lazy
    @Autowired
    private BikesharingPapinDataPusher pusher;

    @Autowired
    private BikesharingMappingUtil mapper;

    @Autowired
    private BikesharingPapinDataRetriever retriever;

    /** JOB 1 */
    public void pushData() throws Exception {
        LOG.info("START.pushData");

        try {
            BikesharingPapinDto bzDto = retriever.fetchData();
            List<BikesharingPapinStationDto> data = bzDto.getStationList();

            //Push station data, we have three Station Types: Station
            StationList stations = mapper.mapStations2Bdp(data);
            if (stations != null) {
                pusher.syncStations(BikesharingPapinDataConverter.STATION_TYPE_STATION, stations);
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
}