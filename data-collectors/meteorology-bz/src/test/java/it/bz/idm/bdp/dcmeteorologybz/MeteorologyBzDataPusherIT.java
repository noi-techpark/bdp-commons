// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.dcmeteorologybz;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import it.bz.idm.bdp.dcmeteorologybz.dto.MeteorologyBzDto;
import it.bz.idm.bdp.dcmeteorologybz.dto.TimeSerieDto;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationList;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class MeteorologyBzDataPusherIT extends AbstractJUnit4SpringContextTests {

    private static final Logger LOG = LoggerFactory.getLogger(MeteorologyBzDataPusherIT.class.getName());

    @Autowired
    private MeteorologyBzJobScheduler scheduler;

    @Autowired
    private MeteorologyBzDataPusher pusher;

    @Autowired
    private MeteorologyBzDataRetriever reader;

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
        List<MeteorologyBzDto> dataStations = null;
        List<DataTypeDto> dataTypes = null;
        MeteorologyBzDto dataMeasurements = null;

        try {
            String responseStringStations = MeteorologyBzDataRetrieverTestIT.getTestData(MeteorologyBzDataRetrieverTestIT.DATA_PUSH_STATIONS);
            dataStations = reader.convertStationsResponseToInternalDTO(responseStringStations);

            String responseStringDataTypes = MeteorologyBzDataRetrieverTestIT.getTestData(MeteorologyBzDataRetrieverTestIT.DATA_PUSH_DATA_TYPES);
            dataTypes = reader.convertSensorsResponseToInternalDTO(responseStringDataTypes, dataStations);

            String responseStringMeasurements = MeteorologyBzDataRetrieverTestIT.getTestData(MeteorologyBzDataRetrieverTestIT.DATA_PUSH_MEASUREMENTS);
            List<TimeSerieDto> timeSeries = reader.convertMeasurementsResponseToInternalDTO(responseStringMeasurements);

            dataMeasurements = dataStations.get(0);
            for (String typeCode : MeteorologyBzDataRetrieverTestIT.DATA_PUSH_TYPE_CODES) {
                dataMeasurements.getTimeSeriesMap().put(typeCode, timeSeries);
            }
        } catch (Exception e) {
            LOG.error("Exception in testPush: "+e, e);
            Assert.fail();
        }

        pushStations(dataStations, errors);
        pushDataTypes(dataTypes, errors);
        pushStationData(dataMeasurements, errors);

        if ( errors.size() > 0 ) {
            for (String err : errors) {
                LOG.error(err);
            }
            Assert.fail();
        }
        LOG.debug("END.testPush");
    }

    private void pushStations(List<MeteorologyBzDto> data, List<String> errors) {
        try {
            StationList stations = pusher.mapStations2Bdp(data);
            LOG.debug(stations.toString());
            if (stations != null) {
                pusher.syncStations(stations);
            }
        } catch (Exception e) {
            errors.add("STATIONS: "+e);
        }
    }

    private void pushDataTypes(List<DataTypeDto> dataTypes, List<String> errors) {
        try {
            List<DataTypeDto> dataTypeList = dataTypes;
            LOG.debug(dataTypeList.toString());
            if (dataTypeList != null) {
                pusher.syncDataTypes(dataTypeList);
            }
        } catch (Exception e) {
            errors.add("DATA-TYPE-REC: "+e);
        }

    }

    private void pushStationData(MeteorologyBzDto data, List<String> errors) {
        try {
            DataMapDto<RecordDtoImpl> stationRec = pusher.mapSingleStationData2Bdp(data);
            if (stationRec != null) {
                pusher.pushData(stationRec);
            }
        } catch (Exception e) {
            errors.add("MEASUREMENTS-REC: "+e);
        }
    }

}
