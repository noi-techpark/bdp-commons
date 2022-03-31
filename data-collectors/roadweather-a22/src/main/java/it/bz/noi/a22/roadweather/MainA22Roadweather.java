package it.bz.noi.a22.roadweather;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;

@Component
public class MainA22Roadweather{

    private static Logger log = LoggerFactory.getLogger(MainA22Roadweather.class);

    private final A22Properties datatypesProperties;
    private final A22Properties a22RoadweatherProperties;
    private HashMap<String, Long> stationIdLastTimestampMap;
    @Autowired
    private A22RoadweatherJSONPusher pusher;
    private List<String> datatypeKeys;
    private StationList stationList;

    public MainA22Roadweather() {
        this.datatypesProperties = new A22Properties("a22roadweatherdatatypes.properties");
        this.a22RoadweatherProperties = new A22Properties("a22roadweather.properties");

    }

    public void execute(){
        long startTime = System.currentTimeMillis();
        try {
            log.info("Start MainA22Roadweather");


            setupDataType();

            // step 1
            // create a Connector instance: this will perform authentication and store the session
            //
            // the session will last 24 hours unless de-authenticated before - however, if a user
            // de-authenticates one session, all sessions of the same user will be de-authenticated
            Connector a22Service = setupA22ServiceConnector();


            // step 2
            // get the list of weather stations
            stationList = new StationList();
            try {
                ArrayList<HashMap<String, String>> stations = a22Service.getStations();
                log.debug("got " + stations.size() + " stations");
                if (stations.size() > 0) {
                    System.out.println("the first station is:");
                    System.out.println(stations.get(0));
                    stations.forEach(station -> {
                        StationDto stationDto = new StationDto(station.get("idcabina"),
                                station.get("descrizione"),
                                Double.parseDouble(station.get("latitudine")),
                                Double.parseDouble(station.get("longitudine")));
                        stationDto.setOrigin(a22RoadweatherProperties.getProperty("origin"));
                        stationDto.setStationType(a22RoadweatherProperties.getProperty("stationtype"));
                        // add other metadata
                        String idDirezione = "";
                        switch (Integer.parseInt(station.get("iddirezione"))) {
                            case 1:
                                idDirezione = "Sud";
                                break;
                            case 2:
                                idDirezione = "Nord";
                                break;
                            case 3:
                                idDirezione = "Entrmbe";
                                break;
                            default:
                                idDirezione = "Non definito";
                                break;
                        }
                        stationDto.getMetaData().put("iddirezione", idDirezione);
                        stationDto.getMetaData().put("metro", Integer.parseInt(station.get("metro")));
                        stationList.add(stationDto);
                    });
                    pusher.syncStations(pusher.initIntegreenTypology(), stationList);
                }
            } catch (Exception e) {
                log.error("step 2 failed, continuing anyway to de-auth...", e);
            }

            // step 3
            // get the list of weather data records
            try {
                long scanWindowSeconds = Long.parseLong(a22RoadweatherProperties.getProperty("scanWindowSeconds"));
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                for (int i = 0; i < stationList.size(); i++) {
                    String idCabina = stationList.get(i).getId();

                    long lastTimeStamp = getLastTimestampOfStationInSeconds(idCabina);

                    do {
                        DataMapDto<RecordDtoImpl> stationMap = new DataMapDto<>();
                        ArrayList<HashMap<String, String>> weatherdata_list = a22Service.getWeatherData(lastTimeStamp, lastTimeStamp + scanWindowSeconds, Long.valueOf(stationList.get(i).getId()));

                        log.debug("got " + weatherdata_list.size() + " weather data records for " + simpleDateFormat.format(new Date(lastTimeStamp * 1000)) + ", " + simpleDateFormat.format(new Date((lastTimeStamp + scanWindowSeconds) * 1000)) + ", " + idCabina + ":");
                        if (weatherdata_list.size() > 0) {
                            log.debug("the first weather data record is: ");
                            log.debug(weatherdata_list.get(0).toString());
                            weatherdata_list.forEach(weatherdata -> {
                                datatypeKeys.forEach(cname -> {
                                    if (!weatherdata.get(cname).equals("null")) {
                                    	stationMap.addRecord(idCabina, cname, 
                                    	new SimpleRecordDto(Long.parseLong(weatherdata.get("data")) * 1000, Double.parseDouble(weatherdata.get(cname)),1));
                                        if (datatypesProperties.getProperty("a22roadweather.datatype." + cname + ".mapping").equals("true")) {
                                        	stationMap.addRecord(idCabina, cname + "_desc", new SimpleRecordDto(Long.parseLong(weatherdata.get("data")) * 1000,
                                            datatypesProperties.getProperty("a22roadweather.datatype." + cname + ".mapping." + weatherdata.get(cname)),1));
                                        }
                                    }
                                });
                            });
                        }
                        stationMap.clean();
                        if (!stationMap.getBranch().isEmpty())
                        	pusher.pushData(stationMap);
                        lastTimeStamp += scanWindowSeconds;
                    } while (lastTimeStamp < System.currentTimeMillis() / 1000);
                }
            } catch (Exception e) {
                log.error("step 3 failed, continuing anyway to read de-auth...", e);
            }


            // step 4
            // de-authentication
            a22Service.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            long stopTime = System.currentTimeMillis();
            log.debug("elaboration time (millis): " + (stopTime - startTime));
        }
    }

    private Connector setupA22ServiceConnector() throws IOException {
        String url;
        String user;
        String password;

        // read connector auth informations
        A22Properties prop = new A22Properties("a22connector.properties");
        url = prop.getProperty("url");
        user = prop.getProperty("user");
        password = prop.getProperty("password");

        Connector a22Service = new Connector(url, user, password);

        return a22Service;
    }

    private void setupDataType() {
        List<DataTypeDto> dataTypeDtoList = new ArrayList<>();
        getDatatypeKeys().forEach(cname -> {

            dataTypeDtoList.add(
                    new DataTypeDto(cname,
                            datatypesProperties.getProperty("a22roadweather.datatype." + cname + ".unit"),
                            datatypesProperties.getProperty("a22roadweather.datatype." + cname + ".description"),
                            datatypesProperties.getProperty("a22roadweather.datatype." + cname + ".rtype"))
            );

            if (datatypesProperties.getProperty("a22roadweather.datatype." + cname + ".mapping").equals("true")) {
                dataTypeDtoList.add(
                        new DataTypeDto(cname + "_desc",
                                datatypesProperties.getProperty("a22roadweather.datatype." + cname + ".unit"),
                                datatypesProperties.getProperty("a22roadweather.datatype." + cname + ".description"),
                                datatypesProperties.getProperty("a22roadweather.datatype." + cname + ".rtype"))
                );
            }
        });
        pusher.syncDataTypes(dataTypeDtoList);
    }

    public List<String> getDatatypeKeys() {
        if (datatypeKeys == null) {
            datatypeKeys = new ArrayList<>();
            datatypesProperties.keySet().stream().filter(o -> o.toString().matches("^a22roadweather\\..*\\.key$"))
                    .iterator().forEachRemaining(key -> {
                datatypeKeys.add( datatypesProperties.getProperty(key.toString()));
            });
        }
        return datatypeKeys;
    }

    private long getLastTimestampOfStationInSeconds(String idCabina) {

        if (stationIdLastTimestampMap == null) {
            readLastTimestampsForAllStations();
        }
        try {
            long ret = stationIdLastTimestampMap.getOrDefault(idCabina,
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(a22RoadweatherProperties.getProperty("lastTimestamp")).getTime());

            log.debug("getLastTimestampOfSignInSeconds(" + idCabina + "): " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ret));

            return ret / 1000;
        } catch (ParseException e) {
            log.error("Invalid lastTimestamp: " + a22RoadweatherProperties.getProperty("lastTimestamp"), e);
            throw new RuntimeException("Invalid lastTimestamp: " + a22RoadweatherProperties.getProperty("lastTimestamp"), e);
        }
    }

    private void readLastTimestampsForAllStations() {
        stationIdLastTimestampMap = new HashMap<>();

        for (StationDto stationDto : this.stationList) {
            String stationCode = stationDto.getId();
            long lastTimestamp = ((Date) pusher.getDateOfLastRecord(stationCode, null, null)).getTime();
            log.debug("Station Code: " + stationCode + ", lastTimestamp: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(lastTimestamp));
            if (stationIdLastTimestampMap.getOrDefault(stationCode, 0L) < lastTimestamp) {
                stationIdLastTimestampMap.put(stationCode, lastTimestamp);
            }
        }
    }

    /*
     * Method used only for development/debugging
     */
    public static void main(String[] args) throws JobExecutionException {
        new MainA22Roadweather().execute();
    }

}
