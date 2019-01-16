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

    /**
     * This method constructs the rootMap, giving the structure specified
     * inside fillRootMap(DataMapDto rootMap, String from, String to) javaDoc.
     * Each station branch is filled with a branch for each polluter this may
     * seem to be useless, since no station collects each one of the said polluters,
     * but the json returned from the endpoint call sometimes has empty portions
     * for certain stations/polluters/dates/hours, or hasn't collect data for a
     * certain time interval. Therefore this is necessary to guarantee data consistency.
     * @return A DataMapDto structured as said above, ready to be filled with data.
     */
    @SuppressWarnings("Duplicates")
    private static DataMapDto<RecordDtoImpl> constructRootMap()
    {
        LOG.info("Starting to construct rootMap.");
        DataPusher pusher = new DataPusher();
        StationList stationList = pusher.mapStations(false);
        String[] pollutersName = pusher.mapTypes(false).keySet().toArray(new String[0]);

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