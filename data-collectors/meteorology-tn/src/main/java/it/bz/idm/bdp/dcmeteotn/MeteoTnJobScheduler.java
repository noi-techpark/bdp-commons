package it.bz.idm.bdp.dcmeteotn;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import it.bz.idm.bdp.dcmeteotn.dto.MeteoTnDto;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationList;

/**
 * Cronjob configuration can be found under src/main/resources/META-INF/spring/applicationContext.xml
 * XXX Do not forget to configure it!
 */
@Component
public class MeteoTnJobScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(MeteoTnJobScheduler.class.getName());

    @Autowired
    private MeteoTnDataPusher pusher;

    @Autowired
    private MeteoTnDataRetriever retriever;

    @Autowired
    private MeteoTnDataConverter converter;

    /** JOB 1 */
    public void pushStations() throws Exception {
        LOG.info("START.pushStations");

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
        LOG.info("END.pushStations");
    }

    /** JOB 2 */
    public void pushDataTypes() throws Exception {
        LOG.info("START.pushDataTypes");

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
        LOG.info("END.pushDataTypes");
    }

    /** JOB 3 */
    public void pushData() throws Exception {
        LOG.info("START.pushData");

        try {

            boolean pushDataSingleStation = converter.isPushDataSingleStation();
            boolean checkDateOfLastRecord = converter.isCheckDateOfLastRecord();
            LOG.info("  pushData:  checkDateOfLastRecord="+checkDateOfLastRecord+"  pushDataSingleStation="+pushDataSingleStation);

            if ( pushDataSingleStation ) {

                //Fetch station data
                List<MeteoTnDto> data = retriever.fetchStations();

                //Send measurements separately for each station
                for (MeteoTnDto meteoTnDto : data) {
                    //Consider only the valid stations, in this way we also avoid to fetch data for invalid stations
                    boolean valid = meteoTnDto.isValid();
                    //MeteoStationDto station = meteoTnDto.getStation();
                    if ( valid ) {
                        Map<String, String> stationAttributes = meteoTnDto.getStationAttributes();
                        MeteoTnDto stationData = retriever.fetchDataByStation(stationAttributes);

                        DataMapDto<RecordDtoImpl> stationRec = pusher.mapSingleStationData2Bdp(stationData);
                        if (stationRec != null){
                            pusher.pushData(stationRec);
                        }
                    }
                }

            } else {

                //Fetch all measurements
                List<MeteoTnDto> data = retriever.fetchData();

                //Push all measurements in a single call
                DataMapDto<RecordDtoImpl> stationRec = pusher.mapData(data);

                if (stationRec != null){
                    pusher.pushData(stationRec);
                }

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