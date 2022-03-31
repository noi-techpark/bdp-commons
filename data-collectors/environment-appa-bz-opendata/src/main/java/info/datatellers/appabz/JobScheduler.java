package info.datatellers.appabz;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;

/**
 * @author Nicolò Molinari, Datatellers.
 *
 * This is the starting point of the Data Collector.
 * See Tester class for documentation.
 */

@Component("jobScheduler")
public class JobScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(JobScheduler.class.getName());
    
    @Autowired
	private DataPusher dataPusher;

    public void pushData()
    {
        LOG.info("Data Collector execution started.");
        DataMapDto<RecordDtoImpl> rootMap = constructRootMap();
		dataPusher.mapData(rootMap, false);
        LOG.info("Data Collector execution terminated.");
    }

    private DataMapDto<RecordDtoImpl> constructRootMap()
    {
        LOG.info("Starting to construct rootMap.");
        StationList stationList = dataPusher.mapStations(false);
        String[] pollutersName = dataPusher.mapTypes(false).keySet().toArray(new String[0]);

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