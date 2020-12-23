package info.datatellers.appabz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;

import Helpers.DateHelper;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.ProvenanceDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;
import it.bz.idm.bdp.json.NonBlockingJSONPusher;

/**
 * @author Nicolò Molinari, Datatellers.
 *
 * This class will map stations, sensors and measurements data into a many levels deep DataMapDto map and return it.
 */

@Component
public class DataPusher extends NonBlockingJSONPusher {
    @Autowired
    private Environment env;
    private static final Logger LOG = LogManager.getLogger(DataPusher.class.getName());
    private int stationMeasurements = 0;
    private int totalMeasurements = 0;

    /**
     * This method uses the DataFetcher class to retrieve stations metadata from the endpoint-provided
     * json, in a very similar way as done in the mapTypes() method, which are then set as attributes of
     * newly initialized StationDto objects inside the for loop.
     *
     * @return stations, a StationList object filled with stations metadata.
     */
    public StationList mapStations(boolean test) {
        LOG.info("Mapping stations.");
        ArrayList<JsonElement> rawStations = new DataFetcher().fetchStations();
        StationList stations = new StationList();

        for (JsonElement rawStation : rawStations) {
            StationDto station = new StationDto();
            try {
                station.setId(rawStation.getAsJsonObject().get("SCODE").getAsString());
                station.setName(rawStation.getAsJsonObject().get("NAME_I").getAsString());
                station.setCoordinateReferenceSystem(env.getProperty("odh.station.projection"));
                station.setOrigin(env.getProperty("odh.station.origin"));
                station.getMetaData().put("municipality", this.guessMunicipality(station.getId()));
                station.setStationType(env.getProperty("odh.station.type"));

                station.setLongitude(Double.valueOf(rawStation.getAsJsonObject().get("LONG").getAsString()));
                station.setLatitude(Double.valueOf(rawStation.getAsJsonObject().get("LAT").getAsString()));

            } catch (UnsupportedOperationException e) {
                station.setLongitude(Double.valueOf("0"));
                station.setLatitude(Double.valueOf("0"));
            }
            stations.add(station);
        }
        LOG.info("Stations mapped.");
        if(!test)
        {
            this.syncStations(stations);
        }
        LOG.debug("Station synced into database.");
        return stations;
    }

    /**
     * This method uses the DataFetcher class to retrieve polluters metadata from the endpoint-provided
     * json, in a very similar way as done in the mapStation() method, which are then set as attributes of
     * newly initialized DataTypeDto objects inside the for loop.
     *
     * @return typesMap, an HashMap object filled with DataTypeDto object. The key to access
     * such objects inside the map is the polluter acronym.
     */
    public HashMap<String, DataTypeDto> mapTypes(boolean test) {
        LOG.info("Mapping polluters.");

        DataFetcher fetcher = new DataFetcher();
        LinkedHashMap<String, DataTypeDto> typesMap = new LinkedHashMap<>();
        HashMap<String, String> types = fetcher.fetchPolluters();
        List<DataTypeDto> typesDto = new ArrayList<>();
        LOG.debug("Data retrieved. Mapping data...");

        for (int looper = 0; looper < types.keySet().size(); looper++)
        {
            DataTypeDto typeDto = new DataTypeDto();

            typeDto.setName(types.keySet().toArray()[looper].toString().replace("\"", ""));
            typeDto.setUnit(types.get(types.keySet().toArray()[looper].toString()).replace("\"", ""));
            typeDto.setRtype(env.getProperty("odh.station.rtype"));
            typeDto.setPeriod(3600);

            typesDto.add(typeDto);
            typesMap.put(types.keySet().toArray(new String[0])[looper], typeDto);
        }

        LOG.info("Polluters mapped.");
        if (!test)
        {
            this.syncDataTypes(typesDto);
        }
        LOG.debug("Polluters synced into database.");
        return typesMap;
    }

    /**
     * This method maps the actual measurements into rootMap with the following structure:
     * --rootMap
     *   |-- Station (key = stationId e.g. "APPABZ_BZ1")
     *       |-- Polluter (key = polluterId e.g."CO - Monossido di Carbonio")
     *           |-- Record (timestamp = __; value =__; period = ____;)
     *
     * @param rootMap A DataMapDto object which is already structured and ready to be filled.
     */
    private void fillRootMap(DataMapDto<RecordDtoImpl> rootMap, boolean test) {
        String[] stationsIDs = rootMap.getBranch().keySet().toArray(new String[0]);
        DataFetcher fetcher = new DataFetcher();
        DateHelper dateHelper = new DateHelper();

        for (String stationID : stationsIDs) {
            String[] pollutersNames = rootMap.getBranch().get(stationID).getBranch().keySet().toArray(new String[0]);
            ArrayList<String> rawIDs = new ArrayList<>();

            for (String polluterName : pollutersNames) {
                if (polluterName.contains("-")) {
                    String polluterID = polluterName.split("-")[0].trim();
                    rawIDs.add(polluterID);
                } else {
                    String polluterID = "GAMMA";
                    rawIDs.add(polluterID);
                }
            }

            for (int looper = 0; looper < pollutersNames.length; looper++) {
                List<RecordDtoImpl> recordDtoList = new ArrayList<>();
                String polluterName = pollutersNames[looper];
                TreeMap<String, ArrayList<String>> dataMap = fetcher.interrogateEndpoint(rawIDs.get(looper), stationID);
                try {
                    String[] dates = dataMap.keySet().toArray(new String[0]);
                    for (String date : dates) {
                        if (!dataMap.get(date).get(1).equals(String.valueOf(-1))){
                            SimpleRecordDto record = new SimpleRecordDto(dateHelper.getTimeStamp(dataMap.get(date).get(0)), Double.valueOf(dataMap.get(date).get(1)), 3600);
                            stationMeasurements++;
                            recordDtoList.add(record);
                            LOG.debug("Record set.");
                        }
                    }
                    Collections.sort(recordDtoList);
                    rootMap.getBranch().get(stationID).getBranch().get(polluterName).setData(recordDtoList);
                    LOG.debug("Polluter " + rawIDs.get(looper) + " branch filled.");
                } catch (NullPointerException e) {
                    rootMap.getBranch().get(stationID).getBranch().remove(polluterName);
                    LOG.debug("Station: " + stationID + " does not collect " + rawIDs.get(looper) + ". Branch removed.");
                }
            }
            totalMeasurements += stationMeasurements;
            LOG.info("Station " + stationID + " data collected: " + stationMeasurements + " records.");
            stationMeasurements = 0;
        }
        LOG.info("RootMap filled: " + totalMeasurements + " records collected.");
        if (!test)
        {
            this.pushData(env.getProperty("odh.station.type"), rootMap);
        }
    }

    /**
     * Method used to guess the municipality based on the the station ID.
     * @param cityAcronym The input station ID.
     * @return The name of the municipality.
     */
    private String guessMunicipality(String cityAcronym) {
        if (cityAcronym.contains("BZ") || cityAcronym.contains("ML6")) {
            return "Bolzano";
        }
        if (cityAcronym.contains("ME")) {
            return "Merano";
        }
        if (cityAcronym.contains("BX") || cityAcronym.contains("AB3")) {
            return "Bressanone";
        }
        if (cityAcronym.contains("BR")) {
            return "Brunico";
        }
        if (cityAcronym.contains("RE")) {
            return "Renon";
        }
        if (cityAcronym.contains("LA")) {
            return "Laces";
        }
        if (cityAcronym.contains("AB2") || cityAcronym.contains("ML5")) {
            return "Egna";
        }
        if (cityAcronym.contains("LS")) {
            return "Laives";
        }
        if (cityAcronym.contains("ML2")) {
            return "Laghetti";
        }
        if (cityAcronym.contains("CR1")) {
            return "Cortina ssdV";
        }
        if (cityAcronym.contains("ST1")) {
            return "Vipiteno";
        }
        if (cityAcronym.contains("AB1")) {
            return "Chiusa";
        } else {
            return "null";
        }
    }

    @Override
    public String initIntegreenTypology() {
        return env.getProperty("odh.station.type");
    }

    @Override
    public <T> DataMapDto<RecordDtoImpl> mapData(T t) {
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> void mapData(T data, boolean test){
        this.fillRootMap((DataMapDto<RecordDtoImpl>) data, test);
    }

	@Override
	public ProvenanceDto defineProvenance() {
		return new ProvenanceDto(null,env.getProperty("provenance_name"), env.getProperty("provenance_version"), env.getProperty("odh.station.origin"));
	}
}