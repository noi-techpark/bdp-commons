package it.bz.idm.bdp.dcparkingtn;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import it.bz.idm.bdp.dcparkingtn.dto.ParkingTnDto;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationList;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class ParkingTnDataPusherIT extends AbstractJUnit4SpringContextTests {

    private static final Logger LOG = LoggerFactory.getLogger(ParkingTnDataPusherIT.class.getName());

    @Autowired
    private ParkingTnJobScheduler scheduler;

    @Autowired
    private ParkingTnDataPusher pusher;

    @Autowired
    private ParkingTnDataRetriever reader;

    private boolean doPush = true;

    @Test
    public void testSchedulerPush() {
        LOG.debug("START.testSchedulerPush");
        if ( !doPush ) {
            LOG.debug("Exit, doPush="+doPush);
            return;
        }
        try {
            scheduler.pushStations();
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
        List<ParkingTnDto> data = null;

        try {
            String responseString = ParkingTnDataRetrieverIT.getTestData(ParkingTnDataRetrieverIT.DATA_PUSH);
            data = reader.convertResponseToInternalDTO(responseString, ParkingTnDataRetrieverIT.MUNICIPALITY, ParkingTnDataRetrieverIT.CODE_PREFIX);
        } catch (Exception e) {
            LOG.error("Exception in testPush: "+e, e);
            Assert.fail();
        }

        pushStations(data, errors);
        pushDataTypes(data, errors);
        pushStationData(data, errors);

        if ( errors.size() > 0 ) {
            for (String err : errors) {
                LOG.error(err);
            }
            Assert.fail();
        }
        LOG.debug("END.testPush");
    }

    private void pushStations(List<ParkingTnDto> data, List<String> errors) {
        try {
            StationList stations = pusher.mapStations2Bdp(data);
            LOG.debug(stations);
            if (stations != null) {
                pusher.syncStations(stations);
            }
        } catch (Exception e) {
            errors.add("STATIONS: "+e);
        }
    }

    private void pushDataTypes(List<ParkingTnDto> data, List<String> errors) {
        try {
            DataMapDto<RecordDtoImpl> stationRec = pusher.mapData(data);
            List<DataTypeDto> dataTypeList = pusher.mapDataTypes2Bdp(stationRec);
            LOG.debug(dataTypeList);
            if (dataTypeList != null) {
                pusher.syncDataTypes(dataTypeList);
            }
        } catch (Exception e) {
            errors.add("DATA-TYPE-REC: "+e);
        }

    }

    private void pushStationData(List<ParkingTnDto> data, List<String> errors) {
        try {
            DataMapDto<RecordDtoImpl> stationRec = pusher.mapData(data);
            if (stationRec != null) {
                pusher.pushData(stationRec);
            }
        } catch (Exception e) {
            errors.add("STATION-REC: "+e);
        }
    }

}
