package it.bz.idm.bdp.dcmeteotn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import it.bz.idm.bdp.dcmeteotn.dto.MeteoTnDto;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationList;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class MeteoTnDataPusherIT extends AbstractJUnit4SpringContextTests {

    private static final Logger LOG = LogManager.getLogger(MeteoTnDataPusherIT.class.getName());

    @Autowired
    private MeteoTnJobScheduler scheduler;

    @Autowired
    private MeteoTnDataPusher pusher;

    @Autowired
    private MeteoTnDataRetriever reader;

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
            scheduler.pushDataTypes();
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
        List<MeteoTnDto> dataStations = null;
        MeteoTnDto dataMeasurements = null;

        try {
            String responseStringStations = MeteoTnDataRetrieverTestIT.getTestData(MeteoTnDataRetrieverTestIT.DATA_PUSH_STATIONS);
            dataStations = reader.convertStationsResponseToInternalDTO(responseStringStations);

            Map<String, String> stationAttrs = dataStations.get(0).getStationAttributes();
            String responseStringMeasurements = MeteoTnDataRetrieverTestIT.getTestData(MeteoTnDataRetrieverTestIT.DATA_PUSH_MEASUREMENTS);
            dataMeasurements = reader.convertMeasurementsResponseToInternalDTO(responseStringMeasurements, stationAttrs);
        } catch (Exception e) {
            LOG.error("Exception in testPush: "+e, e);
            Assert.fail();
        }

        pushStations(dataStations, errors);
        pushDataTypes(dataStations, errors);
        pushStationData(dataMeasurements, errors);

        if ( errors.size() > 0 ) {
            for (String err : errors) {
                LOG.error(err);
            }
            Assert.fail();
        }
        LOG.debug("END.testPush");
    }

    private void pushStations(List<MeteoTnDto> data, List<String> errors) {
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

    private void pushDataTypes(List<MeteoTnDto> data, List<String> errors) {
        try {
//            DataMapDto<RecordDtoImpl> stationRec = pusher.mapData(data);
//            List<DataTypeDto> dataTypeList = pusher.mapDataTypes2Bdp_OLD(stationRec);
            List<DataTypeDto> dataTypeList = pusher.mapDataTypes2Bdp(data);
            LOG.debug(dataTypeList);
            if (dataTypeList != null) {
                pusher.syncDataTypes(dataTypeList);
            }
        } catch (Exception e) {
            errors.add("DATA-TYPE-REC: "+e);
        }

    }

    private void pushStationData(MeteoTnDto data, List<String> errors) {
        try {
            DataMapDto<RecordDtoImpl> stationRec = pusher.mapSingleStationData2Bdp(data);
            if (stationRec != null) {
                pusher.pushData(stationRec);
            }
        } catch (Exception e) {
            errors.add("STATION-REC: "+e);
        }
    }

}
