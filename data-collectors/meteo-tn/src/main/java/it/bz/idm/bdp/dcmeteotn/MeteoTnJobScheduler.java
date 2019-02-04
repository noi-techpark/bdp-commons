package it.bz.idm.bdp.dcmeteotn;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import it.bz.idm.bdp.dcmeteotn.dto.MeteoTnDto;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationList;
import it.bz.idm.bdp.dto.meteo.MeteoStationDto;

/**
 * Cronjob configuration can be found under src/main/resources/META-INF/spring/applicationContext.xml
 * XXX Do not forget to configure it!
 */
@Component
public class MeteoTnJobScheduler {

    private static final Logger LOG = LogManager.getLogger(MeteoTnJobScheduler.class.getName());

    @Autowired
    private MeteoTnDataPusher pusher;

    @Autowired
    private MeteoTnDataRetriever retriever;

    /** JOB 1 */
    public void pushStations() throws Exception {
        LOG.debug("START.pushStations");

        try {
            List<MeteoTnDto> data = retriever.fetchStations();

            StationList stations = pusher.mapStations2Bdp(data);
            if (stations != null) {
                pusher.syncStations(stations);
            }
        } catch (HttpClientErrorException e) {
            LOG.error(pusher + " - " + e + " - " + e.getResponseBodyAsString(), e);
            throw e;
        } catch (Exception e) {
            LOG.error(pusher + " - " + e, e);
            throw e;
        }
        LOG.debug("END.pushStations");
    }

    /** JOB 2 */
    public void pushDataTypes() throws Exception {
        LOG.debug("START.pushDataTypes");

        try {
            List<MeteoTnDto> data = retriever.fetchData();

            List<DataTypeDto> dataTypes = pusher.mapDataTypes2Bdp(data);

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
        LOG.debug("END.pushDataTypes");
    }

    /** JOB 3 */
    public void pushData() throws Exception {
        LOG.debug("START.pushData");

        try {

            //Fetch station data
            List<MeteoTnDto> data = retriever.fetchStations();

            //Send measurements separately for each station
            for (MeteoTnDto meteoTnDto : data) {
                //Consider only the valid stations, in this way we also avoid to fetch data for invalid stations
                boolean valid = meteoTnDto.isValid();
                MeteoStationDto station = meteoTnDto.getStation();
                if ( valid ) {
                    Map<String, String> stationAttributes = meteoTnDto.getStationAttributes();
                    MeteoTnDto stationData = retriever.fetchDataByStation(stationAttributes);

                    Date lastSavedRecord = pusher.getLastSavedRecordForStation(station);
                    DataMapDto<RecordDtoImpl> stationRec = pusher.mapSingleStationData2Bdp(stationData, lastSavedRecord);
                    if (stationRec != null){
                        pusher.pushData(stationRec);
                    }
                }
            }

//            List<MeteoTnDto> data = retriever.fetchData();
//
//            DataMapDto<RecordDtoImpl> stationRec = pusher.mapData(data);
//
//            if (stationRec != null){
//                pusher.pushData(stationRec);
//            }

        } catch (HttpClientErrorException e) {
            LOG.error(pusher + " - " + e + " - " + e.getResponseBodyAsString(), e);
            throw e;
        } catch (Exception e) {
            LOG.error(pusher + " - " + e, e);
            throw e;
        }
        LOG.debug("END.pushData");
    }
}