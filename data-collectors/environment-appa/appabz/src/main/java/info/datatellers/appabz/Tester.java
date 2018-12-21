package info.datatellers.appabz;

import it.bz.idm.bdp.dto.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Nicolò Molinari, Datatellers.
 *
 * This is the starting point of the Data Collector.
 */

public class Tester {

    private static final Logger LOG = LogManager.getLogger(Tester.class.getName());

    public static void main(String[] args)
    {
        LOG.info("Data Collector execution started.");
        DataMapDto rootMap = constructRootMap();
        new DataPusher().mapData(rootMap, false);
        LOG.info("Data Collector execution terminated.");
    }

    private static DataMapDto<RecordDtoImpl> constructRootMap()
    {
        LOG.info("Starting to construct rootMap.");
        DataPusher pusher = new DataPusher();
        StationList stationList = pusher.mapStations();
        String[] pollutersName = pusher.mapTypes().keySet().toArray(new String[0]);

        DataMapDto<RecordDtoImpl> map = new DataMapDto<>();
        for (StationDto station : stationList)
        {
            DataMapDto<RecordDtoImpl> stationMap = map.upsertBranch(station.getId());
            stationMap.setName(station.getName());
            LOG.debug("First deep branch created.");
            for (String polluterName : pollutersName)
            {
                String key = polluterName.trim().replace("\"", "");
                DataMapDto<RecordDtoImpl> measurementMap = stationMap.upsertBranch(key.trim());
                measurementMap.setName(polluterName.trim().replace("\"", ""));
                LOG.debug("Second deep branch created.");
            }
        }
        LOG.info("Map constructed.");
        return map;
    }
}