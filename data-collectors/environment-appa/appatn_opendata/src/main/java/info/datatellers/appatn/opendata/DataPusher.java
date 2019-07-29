package info.datatellers.appatn.opendata;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import info.datatellers.appatn.helpers.CSVHandler;
import info.datatellers.appatn.helpers.DateHelper;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.ProvenanceDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;
import it.bz.idm.bdp.json.JSONPusher;

/**
 * @author Nicolò Molinari, Datatellers.
 *
 * This class will map stations, sensors and measurements data into a many levels deep DataMapDto map and return it.
 */
public class DataPusher extends JSONPusher {
    public static final String SEPARATOR = "_";
    private final ResourceBundle rb = ResourceBundle.getBundle("config");
    private static final Logger LOG = LogManager.getLogger(DataPusher.class.getName());
    private ArrayList<JsonElement> stations;
    private ArrayList<String> pollutersNames = new CSVHandler().getPollutersNames();
    public final String origin = rb.getString("odh.station.origin");
    private static int dayCounter = 0;

    public DataPusher() {}

    /**
     * This method uses the CSVHandler class to retrieve stations metadata from a provided csv file,
     * in a very similar way as done in the mapTypes() method, which are then set as attributes of
     * newly initialized StationDto objects inside the for loop.
     *
     * @return mappedStations, a StationList object filled with stations metadata.
     */
    public StationList mapStations()
    {
        LOG.info("Starting to map stations.");
        StationList mappedStations = new StationList();
        int upperBound = new CSVHandler().getStationsIDsSize();
        HashMap<String, ArrayList<String>> metadata = new CSVHandler().parseStationsCSV();
        String[] keys = metadata.keySet().toArray(new String[0]);

        for (int index = 0; index < upperBound; index++)
        {
            ArrayList<String> stationMetadata = metadata.get(keys[index]);
            StationDto station = new StationDto();

            station.setId(origin + SEPARATOR + stationMetadata.get(0));
            station.setName(stationMetadata.get(1));
            station.setLatitude(Double.valueOf(stationMetadata.get(2)));
            station.setLongitude(Double.valueOf(stationMetadata.get(3)));
            station.getMetaData().put("crs",rb.getString("odh.station.projection"));
            station.setOrigin(origin);
            station.getMetaData().put("municipality",stationMetadata.get(4));
            station.setStationType(rb.getString("odh.station.type"));

            mappedStations.add(station);
        }
        LOG.info("Mapping process correctly completed. Syncing stations...");
        this.syncStations(mappedStations);
        LOG.debug("Syncing completed. Returning as StationList...");
        return mappedStations;
    }

    /**
     * This method uses the CSVHandler class to retrieve polluters metadata from a provided csv file,
     * in a very similar way as done in the mapStation() method, which are then set as attributes of
     * newly initialized DataTypeDto objects inside the for loop.
     *
     * @return typesMap, a LinkedHashMap object filled with DataTypeDto object. The key to access
     * such objects inside the map is the polluter acronym.
     */
    public HashMap<String, DataTypeDto> mapTypes()
    {
        LOG.info("Starting to map data types.");
        CSVHandler csvHandler = new CSVHandler();
        LinkedHashMap<String, DataTypeDto> typesMap = new LinkedHashMap<>();
        LOG.debug("Initializing a csv helper and retrieving required data...");
        ArrayList<String> types = csvHandler.parseTypesCSV();
        LOG.debug("Data retrieved. Mapping data...");
        List<DataTypeDto> typesDto = new ArrayList<>();

        for (String type : types) {
            String[] metaData = type.split(",");

            DataTypeDto typeDto = new DataTypeDto();
            typeDto.setName(metaData[1]);
            typeDto.setDescription(rb.getString("odh.station.description"));
            typeDto.setPeriod(3600);
            typeDto.setRtype(rb.getString("odh.station.rtype"));
            typeDto.setUnit(metaData[3]);

            typesMap.put(metaData[2], typeDto);
            typesDto.add(typeDto);
        }

        LOG.info("Data types correctly mapped. Syncing types...");
        this.syncDataTypes(typesDto);
        LOG.debug("Syncing completed. Returning as LinkedHashMap...");
        return typesMap;
    }

    /**
     * This method maps the actual measurements into rootMap with the following structure:
     * --rootMap
     *   |-- Station (key = stationId e.g. "2")
     *       |-- Polluter (key = polluterId e.g."no2")
     *           |-- Record (timestamp = __; value =__; period = ____;)
     *
     * If from and to are null in the method calling, the last measured data will
     * be retrieved and mapped into rootMap.
     *
     * @param rootMap A DataMapDto object which is already structured and ready to be filled.
     * @param from If specified, the starting date that will be appended in the endpoint request
     *             in order to retrieve data that are going to be mapped into rootMap.
     * @param to If specified, the last date that will be appended in the endpoint request
     *           in order to retrieve data that are going to be mapped into rootMap.
     */
    private void fillRootMap(DataMapDto<RecordDtoImpl> rootMap, Date from, Date to, boolean test)
    {
        ArrayList<String> pollutersAcronyms = new CSVHandler().getPollutersAcronyms();

        if (from != null && to != null && from != to) // historic function is invoked
        {
            LOG.info("Historic function called. Starting to fill historic map with data. Interval requested: " + new DateHelper().formatDate(from.toString())
                    + " - " + new DateHelper().formatDate(to.toString()));
            int[] stationIds = getStationsIDsArray(rootMap);

            //Sorting stationIds in order to correctly fill station branches
            Arrays.sort(stationIds);
            DateHelper dateHelper = new DateHelper();
            Date currentDate = from;
            Date maximumSpan = dateHelper.getSecurityInterval(from, to);

            //Fetching data for the desired interval. This is done just once for every station, thus the performance is better
            stations = new DataFetcher().fetchStations(dateHelper.formatDate(from.toString()) + "," + dateHelper.formatDate(maximumSpan.toString()));

            while (currentDate.compareTo(to) <= 0)
            {
                for (int looper = 0; looper < stationIds.length; looper++)
                {
                    //Getting polluters codes as array
                    LOG.debug("Station selected. Filling branches...");
                    fillSensorsBranches(rootMap, dateHelper.formatDate(currentDate.toString()), pollutersAcronyms, stationIds, looper);
                    LOG.debug("Station branch filled correctly. Moving to next branch...");
                }
                LOG.info("Data correctly collected for date: " + dateHelper.formatDate(currentDate.toString()) + ".");
                currentDate = dateHelper.getNextDay(currentDate);
                dayCounter++;

                if (dayCounter == 30)
                {
                      safePush(rootMap);
                    dayCounter = 0;
                }

                //Control structure for interval validity
                if (currentDate.compareTo(maximumSpan) == 0 && currentDate.compareTo(to) < 0)
                {
                    LOG.info("Data correctly collected from " + dateHelper.formatDate(from.toString()) + " to " + dateHelper.formatDate(maximumSpan.toString()) + ". Moving to next interval.");
                    from = maximumSpan;
                    maximumSpan = dateHelper.getSecurityInterval(from, to);
                    //If the inputted interval is bigger than the web-serviced allowed one, it is broken down into several call to the endpoint
                    stations = new DataFetcher().fetchStations(dateHelper.formatDate(from.toString()) + "," + dateHelper.formatDate(maximumSpan.toString()));
                }
            }

            if (!test){
                safePush(rootMap);
            }
        }

        else // last measurement retrieval function is invoked
            {
                int[] stationIds = getStationsIDsArray(rootMap);

                //Sorting stationIds in order to correctly fill station branches
                Arrays.sort(stationIds);

                stations = new DataFetcher().fetchStations(null);
                for (int looper = 0; looper < stationIds.length; looper++)
                {
                    //Getting polluters codes as array
                    LOG.debug("Station selected. Filling branches...");
                    String date = getLastRetrievedDate((JsonObject)stations.get(looper));
                    LOG.info("Starting to fill station branch with data for station: " + rootMap.getBranch().get(origin + SEPARATOR + stationIds[looper]).getName()
                        + ". Date requested: " + date);
                    fillSensorsBranches(rootMap, date, pollutersAcronyms, stationIds, looper);
                    LOG.info("Station branch filled correctly. Moving to next station...");
                }
            }
        checkBranchConsistency(rootMap);
        LOG.info("Map filled correctly. Syncing map...");
        if (!test){
            safePush(rootMap);
            this.pushData(rb.getString("odh.station.type"), rootMap);
        }
        LOG.info("Syncing completed.");
    }

    /**
     * This method fills @rootMap at sensors level. A branch is added to  rootMap below station
     * level with its key being the date. For every polluter contained inside polluters
     * array, the corresponding json portion is selected. Inside another for loop iterating from 1 to 24
     * (the hours in a day) a branch is added below date level, one for each hour. From 1AM to 9AM a "0"
     * is appended at String beginning as it is needed for correct data retrieval from the json.
     * setRecord(JsonObject rawStation, String date, String measurementHour, String polluter) method is
     * then called. Should the return value of such method be null, variable consistencyChecker is
     * incremented: it means no measurement exists for the specified station, polluter, date and hour.
     * If its value gets to 24 it means no measurements exists for a whole day, and the date branch
     * is removed from rootMap; otherwise the record is set inside the said branch.
     *
     * @param rootMap A DataMapDto object which is already structured and ready to be filled.
     * @param date A String used to specify the desired json portion which contains measurements
     *            for the desired date.
     * @param polluters A String[] containing polluters acronym, iterating through it the
     *                  desired json portion containing the corresponding polluter measurements
     *                  is specified.
     * @param stationIds An int[] containing station IDs used to specify the desired json portion
     *                   containing the corresponding station data.
     * @param looper An int used inside fillRootMap method to iterate through all station IDs.
     */
    private void fillSensorsBranches(DataMapDto<RecordDtoImpl> rootMap, String date, ArrayList<String> polluters, int[] stationIds, int looper)
    {
        //Filling each sensor values for for each station for the selected day
        for (int index = 0; index < polluters.size(); index++) {
            String polluter = polluters.get(index);
            StringBuilder missingValuesInfo = new StringBuilder();
            LOG.debug("Polluter selected: " + polluter + ", for stationID: " + stationIds[looper] + ". Filling branch...");
            JsonObject rawStation = (JsonObject) stations.get(looper);
            int consistencyChecker = 0;

            for (int hour = 1; hour <= 24; hour++) {
                LOG.debug("Hour of measurement selected. Filling branch...");
                if (hour < 10) {
                    SimpleRecordDto recordDto = setRecord(rawStation, date, "0" + (hour), polluter);
                    consistencyChecker = secureSetRecord(rootMap, date, stationIds, looper, index, polluter, missingValuesInfo, consistencyChecker, hour, recordDto);
                } else {
                    SimpleRecordDto recordDto = setRecord(rawStation, date, String.valueOf(hour), polluter);
                    consistencyChecker = secureSetRecord(rootMap, date, stationIds, looper, index, polluter, missingValuesInfo, consistencyChecker, hour, recordDto);
                }
                LOG.debug("Hour branch filled correctly. Moving to next branch...");
            }
            if (consistencyChecker != 0 && consistencyChecker != 24) {
                /*
                In case specific values are not collected on specific hours, the hour branch is not removed (in order to guarantee structure testing consistency).
                The missing value info are logged instead.
                */
                JobScheduler.numberOfInvalidRecord++;
                LOG.debug("Missing  values. The listed hours branches has been removed: " + missingValuesInfo);
            }
            if (consistencyChecker == 24) {
                LOG.debug("Zero records added: station doesn't collect polluter. Removing polluter branch...");
                LOG.debug("Unused polluter: " + polluter + " for day " + date + ". Date branch removed. On to next polluter...");
            } else {
                LOG.debug("Polluter branch filled correctly. Moving to next branch...");
            }
        }
    }

    /**
     * Checks whether the extracted record is valid, if so puts it into the map.
     * @param rootMap DataMapDto object, structured, ready to be filled.
     * @param date String representing the date of the wanted record.
     * @param stationIds Array containing stations IDs.
     * @param looper Integer used to retrieve the correct station ID.
     * @param index Integer used to retrieve the correct polluter name.
     * @param polluter String representing a polluter.
     * @param missingValuesInfo StringBuilder used to construct missing values information to be logged.
     * @param consistencyChecker Integer used to verify the consistency of day's data.
     * @param hour Integer representing the hour of the wanted record.
     * @param recordDto RecordDtoImpl object, contains actual data.
     * @return ConsistencyChecker, incremented if record is null and untouched otherwise.
     */
    private int secureSetRecord(DataMapDto<RecordDtoImpl> rootMap, String date, int[] stationIds, int looper, int index, String polluter, StringBuilder missingValuesInfo, int consistencyChecker, int hour, SimpleRecordDto recordDto) {
        if (recordDto == null) {
            consistencyChecker++;
            missingValuesInfo.append("\n").append("No ").append(polluter).append(" data were collected at station ")
                    .append(stationIds[looper]).append(" for ").append(date).append(" at hour ").append(hour).append(".");
        } else {
            rootMap.getBranch().get(origin + "_" + (stationIds[looper])).getBranch().get(pollutersNames.get(index)).getData().add(recordDto);
            LOG.debug("Record added correctly.");
        }
        return consistencyChecker;
    }

    /**
     * This method, called every time a 30 days span is reached,
     * pushes data to the database and empties the rootMap in
     * order to unload the communication.
     * @param rootMap DataMapDto object which is filled with 30 days data.
     */
    @SuppressWarnings("Duplicates")
    private void safePush(DataMapDto<RecordDtoImpl> rootMap)
    {
        LOG.info("Syncing data.");
        this.pushData(rb.getString("odh.station.type"), rootMap);

        String[] rawStationIds = rootMap.getBranch().keySet().toString().replace("[", "").replace("]", "").replace(" ", "").split(",");
        int[] stationIds = new int[rawStationIds.length];

        for (int looper = 0; looper < stationIds.length; looper++) {
            int stationId = Integer.valueOf(rawStationIds[looper].substring(origin.length()+SEPARATOR.length()));
            stationIds[looper] = stationId;

            for (String polluterName : pollutersNames)
            {
                //rootMap is emptied every 30 days so that the data syncing doesn't take too much time.
                JobScheduler.numberOfRecords += rootMap.getBranch().get(origin + SEPARATOR + stationIds[looper]).getBranch().get(polluterName).getData().size();
                List<RecordDtoImpl> measurements = new ArrayList<>();
                try {
                    rootMap.getBranch().get(origin + SEPARATOR + stationIds[looper]).getBranch().get(polluterName).setData(measurements);
                } catch (NullPointerException e)
                {
                    LOG.debug("Non existing branch hasn't been emptied. " + stationIds[looper] + " has no data for the given period for " + polluterName + ".");
                }
            }
        }

        LOG.info("Data synced. " + JobScheduler.numberOfRecords + " records have been pushed to database.");
    }

    /**
     * This method initialize a SimpleRecordDto containing, if available inside the specified
     * json portion, the measurement specified through the input parameters; otherwise the
     * unexpected behaviour is handled clearly inside the code.
     *
     * @param rawStation A JsonObject containing measurements regarding a single station.
     * @param date A Date used to specify the wanted date for measurements retrieval from
     *             the correct, and so reduced, json portion.
     * @param measurementHour A String used to specify the wanted hour for measurements
     *                        retrieval from the correct, and so reduced, json portion.
     * @param polluter A String used to specify the wanted polluter for measurements retrieval
     *                 from the correct, and so reduced, json portion.
     * @return recordDto, a SimpleRecordDto containing a timeStamp corresponding to the retrieval
     *          date and hour, the measurement value and period set to 3600 seconds (one per hour).
     */
    private SimpleRecordDto setRecord(JsonObject rawStation, String date, String measurementHour, String polluter)
    {
        LOG.debug("Initializing record...");
        SimpleRecordDto recordDto;
        LOG.debug("Parsing timestamp...");

        try {
            if (((JsonObject) ((JsonArray) rawStation.get("stazione")).get(0)).get("dati").toString().equals("[]"))
            {
                LOG.debug("Web-service returned an empty json section: no measurement were collected on date: " + date + ".");
                return null;
            }else
                {
                String measurement = (((JsonObject) ((JsonObject) ((JsonObject) ((JsonObject) ((JsonArray)
                    rawStation.get("stazione")).get(0)).get("dati")).get(date)).get(measurementHour)).get(polluter)).toString();
                recordDto = new SimpleRecordDto(new DateHelper().getTimeStamp(date, measurementHour), Double.valueOf(measurement), 3600);
                LOG.debug("Record set correctly.");
            }
        } catch (NullPointerException e) {
            LOG.debug("Missing polluter value: " + date + ", " + "at hour: " + measurementHour + ", desired polluter: " + polluter + " at station: " + (((JsonObject) ((JsonArray)
                    rawStation.get("stazione")).get(0)).get("nome")) + ".");
            return null;
        }
        return recordDto;
    }

    @Override
    public String initIntegreenTypology() {
        ResourceBundle bundle = ResourceBundle.getBundle("config");
        return bundle.getString("odh.station.type");
    }

    @Override
	public <T> DataMapDto<RecordDtoImpl> mapData(T data)
    {
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> void mapData(T data, String from, String to, boolean test)
    {
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date fromDate = format.parse(from);
            Date toDate = format.parse(to);
            fillRootMap((DataMapDto<RecordDtoImpl>) data, fromDate, toDate, test);
        }catch (ParseException e)
        {
            LOG.fatal("Error while parsing dates.");
        }

    }

    /**
     * This method reads the "data_ultima_acquisizione" field on the DataFetcher returned
     * stations arrayList and returns it back in order to let the
     * fillSensorsBranches(DataMapDto rootMap, String date, String[] polluters, int[] stationIds, int looper)
     * method, given no date interval was provided, retrieve the last measured values. This will
     * be modified as the data retrieval will be based on the last entry inside the database.
     * @param station A JsonObject used to parse the "data_ultima_acquisizione" field.
     * @return A String containing the last date of data retrieval, so formatted: "yyyy-MM-dd".
     */
    private String getLastRetrievedDate(JsonObject station)
    {
        LOG.debug("Date of last retrieved data requested. Parsing json and returning...");
        return ((((JsonObject) ((JsonArray) station.get("stazione")).get(0)).get("data_ultima_acquisizione"))).toString().replace("\"", "");
    }

    /**
     * Overloaded definition of the above method.
     * @return A string containing the least last date of data retrieval, after it has been
     * compared to all the other station's last retrieval dates, so formatted: "yyyy-MM-dd".
     */
    String getLastRetrievedDate()
    {
        LOG.debug("Date of last retrieved data requested. Parsing json and returning...");
        ArrayList<JsonElement> stationList  = new DataFetcher().fetchStations(null);
        String date = stationList.get(0).getAsJsonObject().get("stazione").getAsJsonArray().get(0).getAsJsonObject()
                .get("dati").getAsJsonObject().keySet().toArray()[0].toString();

        for (int looper = 0; looper < stationList.size()-1; looper++)
        {
            if (Integer.valueOf(date.substring(8)) > Integer.valueOf(getLastRetrievedDate(stationList.get(looper + 1).getAsJsonObject()).substring(8)))
            {
                date = getLastRetrievedDate(stationList.get(looper + 1).getAsJsonObject()).substring(8);
            }
        }
        return date;
    }

    /**
     * This method, called once the rootMap has been completely filled,
     * iterates through its station branches, and inside each of their polluter
     * branches, to check whether they have been filled or not. If not, they
     * are removed. See constructRootMap() method inside Tester class for more
     * information.
     * @param rootMap A DataMapDto object which is already filled.
     */
    private void checkBranchConsistency(DataMapDto<RecordDtoImpl> rootMap)
    {
        LOG.debug("Checking branches consistency...");

        int[] stationIds = getStationsIDsArray(rootMap);

        for(int looper = 0; looper < stations.size(); looper++)
        {
            String[] polluters = rootMap.getBranch().get(origin + SEPARATOR + (stationIds[looper])).getBranch().keySet().toString().replace("[", "")
                    .replace("]", "").split(",");

            for (String polluter : polluters)
            {
                if (rootMap.getBranch().get(origin + SEPARATOR + (stationIds[looper])).getBranch().get(polluter.trim()).getData().size() == 0)
                {
                    rootMap.getBranch().get(origin + SEPARATOR + (stationIds[looper])).getBranch().remove(polluter.trim());
                    LOG.debug("Unused branch removal...");
                }
            }
        }
        LOG.debug("Branches consistency check completed.");
    }

    /**
     * Method used to get stations IDs.
     * @param rootMap DataMapDto object, structured.
     * @return an int[] containing stations IDs.
     */
    private int[] getStationsIDsArray(DataMapDto<RecordDtoImpl> rootMap)
    {
        String[] rawStationIds = rootMap.getBranch().keySet().toString().replace("[", "").replace("]", "").replace(" ", "").split(",");
        int[] stationIds = new int[rawStationIds.length];

        for (int index = 0; index < stationIds.length; index++) {
            int stationId = Integer.valueOf(rawStationIds[index].substring(origin.length()+SEPARATOR.length()));
            stationIds[index] = stationId;
        }
        return stationIds;
    }

	@Override
	public ProvenanceDto defineProvenance() {
		return new ProvenanceDto(null, "dc-appatn-opendata", "2.0.0-SNAPSHOT", rb.getString("odh.station.origin"));
	}
}
