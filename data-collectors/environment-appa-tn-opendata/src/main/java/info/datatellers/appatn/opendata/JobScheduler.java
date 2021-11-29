package info.datatellers.appatn.opendata;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import info.datatellers.appatn.helpers.DateHelper;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;

/**
 * @author Nicolò Molinari, Datatellers.
 */

@Component
public class JobScheduler {
    private static final ResourceBundle rb = ResourceBundle.getBundle("config");
    private static final Logger LOG = LogManager.getLogger(JobScheduler.class.getName());
    static double numberOfRecords = 0;
    static double numberOfInvalidRecord = 0;
    
    @Autowired
	private DataPusher dataPusher;

    @SuppressWarnings("Duplicates")
    public void pushData()
    {
        LOG.info("Starting Data Collector execution...");
        DataMapDto<RecordDtoImpl> rootMap = constructRootMap();
		String fromDate = String.valueOf(dataPusher.getDateOfLastRecord(rb.getString("odh.station.origin")+DataPusher.SEPARATOR+"2","co2",3600));
        String toDate = String.valueOf(dataPusher.getLastRetrievedDate());
        if (fromDate.contains("1970"))
        {
            //Database doesn't contain data. Historic function calling from 2017-01-01 to last retrieving date (yesterday).
            LOG.info("Historic function called.");
            dataPusher.mapData(rootMap, rb.getString("odh.scheduler.historic.from"), toDate, false);
            LOG.info("Historic function execution terminated.");
        }else{
            //Database contains data. Historic function calling from last date of entry in database to yesterday
            LOG.info("Update function called.");

            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                if (simpleDateFormat.parse(new DateHelper().formatDate(fromDate)).compareTo(simpleDateFormat.parse(toDate)) <= 0)
                {
                	dataPusher.mapData(rootMap, new DateHelper().formatDate(fromDate), toDate, false);
                }
            } catch (ParseException e) {
                LOG.error("Unrecognized date format.");
                LOG.debug(e.getMessage());
            }
            LOG.info("Update function execution terminated.");
        }
        LOG.info("Percentage of invalid data: " + invalidDataPercentage(numberOfRecords, numberOfInvalidRecord) + " --> Change LOG level to \"debug\" for more information.");
        LOG.info("Data Collector execution terminated.");
    }

    /**
     * See Tester class for documentation.
     */
    @SuppressWarnings("Duplicates")
    private DataMapDto<RecordDtoImpl> constructRootMap() {
        LOG.info("Starting to construct rootMap.");
        StationList stationList = dataPusher.mapStations();
        String pollutersRaw;

        DataMapDto<RecordDtoImpl> map = new DataMapDto<>();
        HashMap<String, DataTypeDto> sensorsMap = dataPusher.mapTypes();
        for (StationDto station : stationList)
        {
            DataMapDto<RecordDtoImpl> stationMap = map.upsertBranch(station.getId());
            stationMap.setName(station.getName());
            LOG.debug("First deep branch created.");

            LOG.debug("Starting generic sensors mapping...");
            pollutersRaw = rb.getString("odh.station.polluters");
            String[] polluters = pollutersRaw.replace("{", "").replace("}", "").split(",");
            for (String polluter : polluters)
            {
                LOG.debug("Branch creation...");
                DataMapDto<RecordDtoImpl> measurementBranch = stationMap.upsertBranch(sensorsMap.get(polluter).getName());
                measurementBranch.setName(sensorsMap.get(polluter).getName());
                List<RecordDtoImpl> measurements = new ArrayList<>();
                map.getBranch().get(station.getId()).getBranch().get(sensorsMap.get(polluter).getName()).setData(measurements);
            }
            LOG.debug("Second deep branches created.");
        }
        LOG.info("Returning constructed map...");
        return map;
    }

    @SuppressWarnings("Duplicates")
    private String invalidDataPercentage(double validRecords, double invalidRecords)
    {
        DecimalFormat format = new DecimalFormat("#.#####");
        format.setRoundingMode(RoundingMode.CEILING);
        Number value = ((float)(invalidRecords * 100) / (float)validRecords);
        double percentage = value.doubleValue();
        return (format.format(percentage) + " %");
    }
}
