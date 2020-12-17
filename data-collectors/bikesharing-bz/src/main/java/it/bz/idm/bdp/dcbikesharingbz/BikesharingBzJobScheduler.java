package it.bz.idm.bdp.dcbikesharingbz;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import it.bz.idm.bdp.dcbikesharingbz.dto.BikesharingBzDto;
import it.bz.idm.bdp.dcbikesharingbz.dto.BikesharingBzStationDto;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationList;

/**
 * Cronjob configuration can be found under src/main/resources/META-INF/spring/applicationContext.xml
 * XXX Do not forget to configure it!
 */
@Component
public class BikesharingBzJobScheduler {

    private static final Logger LOG = LogManager.getLogger(BikesharingBzJobScheduler.class.getName());

    @Lazy
    @Autowired
    private BikesharingBzDataPusher pusher;

    @Autowired
    private BikesharingMappingUtil mapper;

    @Autowired
    private BikesharingBzDataRetriever retriever;

    /** JOB 1 */
    public void pushData() throws Exception {
        LOG.info("START.pushData");

        try {
            BikesharingBzDto bzDto = retriever.fetchData();
            List<BikesharingBzStationDto> data = bzDto.getStationList();

            //Push station data, we have three Station Types: Station, Bay, Bicycle
            StationList stations = mapper.mapStations2Bdp(data);
            if (stations != null) {
                pusher.syncStations(BikesharingBzDataConverter.STATION_TYPE_STATION, stations);
            }
            StationList bays = mapper.mapBays2Bdp(data);
            if (bays != null) {
                pusher.syncStations(BikesharingBzDataConverter.STATION_TYPE_BAY, bays);
            }
            StationList bicycles = mapper.mapBicycles2Bdp(data);
            if (bicycles != null) {
                pusher.syncStations(BikesharingBzDataConverter.STATION_TYPE_BICYCLE, bicycles);
            }

            //Push measurements, we have three Station Types: Station, Bay, Bicycle
            DataMapDto<RecordDtoImpl> stationRec = mapper.mapStationData(data);
            if (stationRec != null){
                pusher.pushData(BikesharingBzDataConverter.STATION_TYPE_STATION, stationRec);
            }
            DataMapDto<RecordDtoImpl> bayRec = mapper.mapBayData(data);
            if (bayRec != null){
                pusher.pushData(BikesharingBzDataConverter.STATION_TYPE_BAY, bayRec);
            }
            DataMapDto<RecordDtoImpl> bicycleRec = mapper.mapBicycleData(data);
            if (bicycleRec != null){
                pusher.pushData(BikesharingBzDataConverter.STATION_TYPE_BICYCLE, bicycleRec);
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
            List<DataTypeDto> dataTypes = mapper.mapDataTypes2Bdp();

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