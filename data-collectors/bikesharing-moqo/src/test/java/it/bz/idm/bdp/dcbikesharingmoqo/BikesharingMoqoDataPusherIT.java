package it.bz.idm.bdp.dcbikesharingmoqo;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import it.bz.idm.bdp.dcbikesharingmoqo.dto.AvailabilityDto;
import it.bz.idm.bdp.dcbikesharingmoqo.dto.BikeDto;
import it.bz.idm.bdp.dcbikesharingmoqo.dto.BikesharingMoqoPageDto;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationList;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class BikesharingMoqoDataPusherIT extends AbstractJUnit4SpringContextTests {

    private static final Logger LOG = LoggerFactory.getLogger(BikesharingMoqoDataPusherIT.class.getName());

    @Autowired
    private BikesharingMoqoJobScheduler scheduler;

    @Autowired
    private BikesharingMoqoDataPusher pusher;

    @Autowired
    private BikesharingMoqoDataConverter converter;

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
        List<BikeDto> dataStations = null;
        BikeDto dataMeasurements = null;

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

    private void pushStations(List<BikeDto> data, List<String> errors) {
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

    private void pushDataTypes(List<String> errors) {
        try {
            List<DataTypeDto> dataTypeList = pusher.mapDataTypes2Bdp();
            LOG.debug(dataTypeList);
            if (dataTypeList != null) {
                pusher.syncDataTypes(dataTypeList);
            }
        } catch (Exception e) {
            errors.add("DATA-TYPE-REC: "+e);
        }

    }

    private void pushStationData(BikeDto data, List<String> errors) {
        try {
            List<BikeDto> list = new ArrayList<BikeDto>();
            list.add(data);
            DataMapDto<RecordDtoImpl> stationRec = pusher.mapData(list);
            if (stationRec != null) {
                pusher.pushData(stationRec);
            }
        } catch (Exception e) {
            errors.add("MEASUREMENTS-REC: "+e);
        }
    }

    private List<BikeDto> readPushData() throws Exception {
        //Convert station data
        String responseString = BikesharingMoqoDataRetrieverTestIT.getTestData(BikesharingMoqoDataRetrieverTestIT.DATA_FETCH_STATIONS, ServiceCallParam.FUNCTION_NAME_PAGE_NUM, "1");
        BikesharingMoqoPageDto bikesharingMoqoPageDto = converter.convertCarsResponseToInternalDTO(responseString);
        List<BikeDto> data = bikesharingMoqoPageDto.getBikeList();

        //Convert availability data
        for (BikeDto bikeDto : data) {
            String bikeId = bikeDto.getId();
            String responseStringAvail = BikesharingMoqoDataRetrieverTestIT.getTestData(BikesharingMoqoDataRetrieverTestIT.DATA_FETCH_MEASUREMENTS, ServiceCallParam.FUNCTION_NAME_STATION_ID, bikeId);
            if ( DCUtils.paramNotNull(responseStringAvail) ) {
                List<AvailabilityDto> availDtoList = converter.convertAvailabilityResponseToInternalDTO(responseStringAvail);
                bikeDto.setAvailabilityList(availDtoList);
                //Evaluate attributes available, until and from for the bike, looking into the Availability slots
                converter.calculateBikeAvailability_FromUntil(bikeDto, availDtoList);
                converter.calculateBikeAvailability(bikeDto, availDtoList);
            }
        }

        return data;
    }
}
