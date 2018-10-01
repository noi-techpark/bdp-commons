package it.bz.idm.bdp.dcemobilityh2;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import it.bz.idm.bdp.dcemobilityh2.dto.HydrogenDto;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class HydrogenDataPusherIT extends AbstractJUnit4SpringContextTests {

    private static final Logger LOG = LogManager.getLogger(HydrogenDataPusherTest.class.getName());

    @Autowired
    private HydrogenJobScheduler scheduler;

    @Autowired
    private HydrogenDataPusher pusher;

    @Autowired
    private HydrogenDataRetriever reader;

    private boolean doPush = false;

    @Test
    public void testSchedulerPush() {
        if ( !doPush ) {
            return;
        }
        try {
            scheduler.pushStations();
            scheduler.pushData();
        } catch (Exception e) {
            LOG.error("Exception in testSchedulerPush: "+e, e);
            Assert.fail();
        }
    }

    @Test
    public void testPush() {
        if ( !doPush ) {
            return;
        }

        List<String> errors = new ArrayList<String>();
        List<HydrogenDto> data = null;

        try {
            String responseString = HydrogenDataRetrieverTest.getTestData();
            data = reader.convertResponseToInternalDTO(responseString);
        } catch (Exception e) {
            LOG.error("Exception in testPush: "+e, e);
            Assert.fail();
        }

        pushStations(data, errors);
        pushPlugs(data, errors);
        pushStationData(data, errors);
        pushPlugData(data, errors);

        if ( errors.size() > 0 ) {
            for (String err : errors) {
                LOG.error(err);
            }
            Assert.fail();
        }
    }

    private void pushStations(List<HydrogenDto> data, List<String> errors) {
        try {
            StationList stations = pusher.mapStations2Bdp(data);
            LOG.debug(stations);
            if (stations != null) {
                pusher.syncStations(stations);
            }
        } catch (Exception e) {
            //e.printStackTrace();
            errors.add("STATIONS: "+e);
        }
    }

    private void pushPlugs(List<HydrogenDto> data, List<String> errors) {
        try {
            StationList plugs    = pusher.mapPlugs2Bdp(data);
            StationList tmp = new StationList();
            for (StationDto stationDto : plugs) {
                LOG.debug(stationDto);
                stationDto.setStationType(null);
                if ( tmp.size()==0 ) {
                    tmp.add(stationDto);
                }
            }
            plugs = tmp;
            if (plugs != null) {
                pusher.syncStations("EChargingPlug", plugs);
            }
        } catch (Exception e) {
            //e.printStackTrace();
            errors.add("PLUGS: "+e);
        }
    }

    private void pushStationData(List<HydrogenDto> data, List<String> errors) {
        try {
            DataMapDto<RecordDtoImpl> stationRec = pusher.mapData(data);
            if (stationRec != null) {
                pusher.pushData(stationRec);
            }
        } catch (Exception e) {
            //e.printStackTrace();
            errors.add("STATION-REC: "+e);
        }
    }

    private void pushPlugData(List<HydrogenDto> data, List<String> errors) {
        try {
            DataMapDto<RecordDtoImpl> plugRec = pusher.mapPlugData2Bdp(data);
            if (plugRec != null){
                pusher.pushData("EChargingPlug",plugRec);
            }
        } catch (Exception e) {
            //e.printStackTrace();
            errors.add("PLUG-REC: "+e);
        }
    }

}
