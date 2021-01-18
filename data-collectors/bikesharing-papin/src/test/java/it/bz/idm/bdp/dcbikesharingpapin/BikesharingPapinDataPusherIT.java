package it.bz.idm.bdp.dcbikesharingpapin;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import it.bz.idm.bdp.dcbikesharingpapin.dto.BikesharingPapinDto;
import it.bz.idm.bdp.dcbikesharingpapin.dto.BikesharingPapinStationDto;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationList;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class BikesharingPapinDataPusherIT extends AbstractJUnit4SpringContextTests {

    private static final Logger LOG = LogManager.getLogger(BikesharingPapinDataPusherIT.class.getName());

    @Autowired
    private BikesharingPapinJobScheduler scheduler;

    @Autowired
    private BikesharingMappingUtil mappingUtil;
    @Autowired
    private BikesharingPapinDataPusher pusher;

    @Autowired
    private BikesharingPapinDataConverter converter;

    private boolean doPush = true;

    @Test
    public void testSchedulerPush() {
        LOG.debug("START.testSchedulerPush");
        if ( !doPush ) {
            LOG.debug("Exit, doPush="+doPush);
            return;
        }
        try {
            scheduler.pushData();
        } catch (Exception e) {
            LOG.error("Exception in testSchedulerPush: "+e, e);
            Assert.fail();
        }
        LOG.debug("END.testSchedulerPush");
    }

    @Test
    public void testPush() {
        LOG.debug("START.testPush");
        if ( !doPush ) {
            LOG.debug("Exit, doPush="+doPush);
            return;
        }

        List<String> errors = new ArrayList<String>();
        List<BikesharingPapinStationDto> dataStations = null;

        try {
            dataStations = readPushData();
        } catch (Exception e) {
            LOG.error("Exception in testPush: "+e, e);
            Assert.fail();
        }

        pushStations(dataStations, errors);

        if ( errors.size() > 0 ) {
            for (String err : errors) {
                LOG.error(err);
            }
            Assert.fail();
        }
        LOG.debug("END.testPush");
    }

    private void pushStations(List<BikesharingPapinStationDto> data, List<String> errors) {
        try {
            //Push station data, we have three Station Types: Station
            StationList stations = mappingUtil.mapStations2Bdp(data);
            LOG.debug(stations);
            if (stations != null) {
            	pusher.syncStations(BikesharingPapinDataConverter.STATION_TYPE_STATION, stations);
            }

        } catch (Exception e) {
            errors.add("STATIONS: "+e);
        }
    }

    private void pushStationData(BikesharingPapinStationDto data, List<String> errors) {
        try {
            List<BikesharingPapinStationDto> list = new ArrayList<BikesharingPapinStationDto>();
            list.add(data);
            DataMapDto<RecordDtoImpl> stationRec = mappingUtil.mapData(list);
            if (stationRec != null) {
            	pusher.pushData(stationRec);
            }
        } catch (Exception e) {
            errors.add("MEASUREMENTS-REC: "+e);
        }
    }

    private List<BikesharingPapinStationDto> readPushData() throws Exception {
        //Convert station data
        String responseString = BikesharingPapinDataRetrieverAuthIT.getTestData(BikesharingPapinDataRetrieverAuthIT.DATA_FETCH_STATIONS, null, null);
        BikesharingPapinDto BikesharingPapinDto = converter.convertStationsResponseToInternalDTO(responseString);
        List<BikesharingPapinStationDto> data = BikesharingPapinDto.getStationList();

        return data;
    }
}
