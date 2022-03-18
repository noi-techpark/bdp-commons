package it.bz.idm.bdp.dcbikesharingbz;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import it.bz.idm.bdp.dcbikesharingbz.dto.BikesharingBzDto;
import it.bz.idm.bdp.dcbikesharingbz.dto.BikesharingBzStationDto;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationList;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class BikesharingBzDataPusherIT extends AbstractJUnit4SpringContextTests {

    private static final Logger LOG = LoggerFactory.getLogger(BikesharingBzDataPusherIT.class.getName());

    @Autowired
    private BikesharingBzJobScheduler scheduler;

    @Autowired
    private BikesharingMappingUtil mappingUtil;
    @Autowired
    private BikesharingBzDataPusher pusher;

    @Autowired
    private BikesharingBzDataConverter converter;

    private boolean doPush = true;

    @Test
    public void testSchedulerPush() {
        LOG.debug("START.testSchedulerPush");
        if ( !doPush ) {
            LOG.debug("Exit, doPush="+doPush);
            return;
        }
        try {
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
        List<BikesharingBzStationDto> dataStations = null;
        BikesharingBzStationDto dataMeasurements = null;

        try {
            dataStations = readPushData();
            dataMeasurements = dataStations.get(0);
        } catch (Exception e) {
            LOG.error("Exception in testPush: "+e, e);
            Assert.fail();
        }

        pushStations(dataStations, errors);
        pushDataTypes(errors);
        pushStationData(dataMeasurements, errors);

        if ( errors.size() > 0 ) {
            for (String err : errors) {
                LOG.error(err);
            }
            Assert.fail();
        }
        LOG.debug("END.testPush");
    }

    private void pushStations(List<BikesharingBzStationDto> data, List<String> errors) {
        try {
            //Push station data, we have three Station Types: Station, Bay, Bicycle
            StationList stations = mappingUtil.mapStations2Bdp(data);
            LOG.debug("{}", stations);
            if (stations != null) {
            	pusher.syncStations(BikesharingBzDataConverter.STATION_TYPE_STATION, stations);
            }
            StationList bays = mappingUtil.mapBays2Bdp(data);
            LOG.debug("{}", bays);
            if (bays != null) {
            	pusher.syncStations(BikesharingBzDataConverter.STATION_TYPE_BAY, bays);
            }
            StationList bicycles = mappingUtil.mapBicycles2Bdp(data);
            LOG.debug("{}", bicycles);
            if (bicycles != null) {
            	pusher.syncStations(BikesharingBzDataConverter.STATION_TYPE_BICYCLE, bicycles);
            }

        } catch (Exception e) {
            errors.add("STATIONS: "+e);
        }
    }

    private void pushDataTypes(List<String> errors) {
        try {
            List<DataTypeDto> dataTypeList = mappingUtil.mapDataTypes2Bdp();
            LOG.debug("{}", dataTypeList);
            if (dataTypeList != null) {
            	pusher.syncDataTypes(dataTypeList);
            }
        } catch (Exception e) {
            errors.add("DATA-TYPE-REC: "+e);
        }

    }

    private void pushStationData(BikesharingBzStationDto data, List<String> errors) {
        try {
            List<BikesharingBzStationDto> list = new ArrayList<BikesharingBzStationDto>();
            list.add(data);
            DataMapDto<RecordDtoImpl> stationRec = mappingUtil.mapData(list);
            if (stationRec != null) {
            	pusher.pushData(stationRec);
            }
        } catch (Exception e) {
            errors.add("MEASUREMENTS-REC: "+e);
        }
    }

    private List<BikesharingBzStationDto> readPushData() throws Exception {
        //Convert station data
        String responseString = BikesharingBzDataRetrieverAuthIT.getTestData(BikesharingBzDataRetrieverAuthIT.DATA_FETCH_STATIONS, null, null);
        BikesharingBzDto bikesharingBzDto = converter.convertStationsResponseToInternalDTO(responseString);
        List<BikesharingBzStationDto> data = bikesharingBzDto.getStationList();

        List<BikesharingBzStationDto> retval = new ArrayList<>();
        //Convert availability data
        for (BikesharingBzStationDto bikeDto : data) {
            String bikeId = bikeDto.getId();
            String responseStringDetails = BikesharingBzDataRetrieverAuthIT.getTestData(BikesharingBzDataRetrieverAuthIT.DATA_FETCH_MEASUREMENTS, ServiceCallParam.FUNCTION_NAME_STATION_ID, bikeId);
            if ( DCUtils.paramNotNull(responseStringDetails) ) {
                BikesharingBzStationDto bzStationDto = converter.convertStationDetailResponseToInternalDTO(responseStringDetails);
                retval.add(bzStationDto);
            }
        }

        return retval;
    }
}
