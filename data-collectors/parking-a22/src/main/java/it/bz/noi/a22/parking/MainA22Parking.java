/*
 *  A22 Parking Data Collector - Main Application
 *
 *  (C) 2021 NOI Techpark Südtirol / Alto Adige
 *
 *  changelog:
 *  2021-06-01  1.0 - thomas.nocker@catch-solve.tech
 */

package it.bz.noi.a22.parking;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;

@Component
public class MainA22Parking {

    private static final String IDENTIFIER_NAMESPACE = "parking-a22";
	private static final String FREE_TYPE = "free";
    private static final String OCCUPIED_TYPE = "occupied";
    private static final String STATE_TYPE = "state";

    private static final String STATION_METADATA_IDDIREZIONE = "iddirezione";
    private static final String STATION_METADATA_CAPACITA = "capacità";
    private static final String STATION_METADATA_METRO = "metro";
    private static final String STATION_METADATA_AUTOSTRADA = "autostrada";

    private static final Logger LOG = LogManager.getLogger(MainA22Parking.class);

    private final A22Properties a22ParkingProperties;
    private final A22Properties datatypesProperties;
    private final A22Properties valuesMappingProperties;
    @Autowired
    private A22ParkingJSONPusher pusher;

    public MainA22Parking() {
        this.a22ParkingProperties = new A22Properties("a22parking.properties");
        this.datatypesProperties = new A22Properties("a22parkingdatatypes.properties");
        this.valuesMappingProperties = new A22Properties("a22parkingvaluesmapping.properties");
    }

    public void execute() {
        long startTime = System.currentTimeMillis();
        try {
            LOG.info("Start MainA22Parking");

            long delaySeconds = 3600; // 2019-06-21 d@vide.bz: a22 data realtime delay

            setupDataType();

            // step 1
            // create a Connector instance: this will perform authentication and store the session
            //
            // the session will last 24 hours unless de-authenticated before - however, if a user
            // deauthenticates one session, all sessions of the same user will be de-authenticated;
            // this means each running application neeeds their own username
            A22ParkingConnector A22Service = setupA22ServiceConnector();

            // step 2
            // fetch and print info about all car parks ("/parcheggi/anagrafica")
            LOG.info("step 2: fetch and print info about all car parks (\"/parcheggi/anagrafica\")");
            HashMap<String, StationDto> stationDtoMap = new HashMap<>();
            try {
                ArrayList<A22CarParkInfo> parks = A22Service.getInfo();
                LOG.debug("got info about " + parks.size() + " car parks");
                for (A22CarParkInfo parkInfo : parks) {
                    StationDto stationDto = new StationDto(IDENTIFIER_NAMESPACE +":"+ parkInfo.getId().toString(),
                            parkInfo.getDescrizione(),
                            parkInfo.getLatitudine(),
                            parkInfo.getLongitudine());
                    stationDto.setOrigin(a22ParkingProperties.getProperty("origin"));
                    stationDto.setStationType(a22ParkingProperties.getProperty("stationtype"));
                    String idDirezione =
                            valuesMappingProperties.getProperty("a22parking.metadata." + STATION_METADATA_IDDIREZIONE + "." + parkInfo.getIddirezione(),
                                    valuesMappingProperties.getProperty("a22parking.metadata." + STATION_METADATA_IDDIREZIONE + ".*"));
                    stationDto.getMetaData().put(STATION_METADATA_IDDIREZIONE, idDirezione);
                    stationDto.getMetaData().put(STATION_METADATA_METRO, parkInfo.getMetro());
                    stationDto.getMetaData().put(STATION_METADATA_AUTOSTRADA, parkInfo.getAutostrada());

                    stationDtoMap.put(IDENTIFIER_NAMESPACE +":"+ parkInfo.getId().toString(), stationDto);
                }
            } catch (Exception e) {
                LOG.error("step 2 failed, continuing anyway to read de-auth...", e);
            }

            // step 3
            // fetch and print capacity for car parks ("/parcheggi/stato")
            LOG.info("step 3: fetch and print capacity for car parks (\"/parcheggi/stato\")");
            try {
                DataMapDto<RecordDtoImpl> dataMap = new DataMapDto<>();

                Integer period = Integer.parseInt(a22ParkingProperties.getProperty("period"));
                Long timestamp = System.currentTimeMillis();

                ArrayList<A22CarParkCapacity> caps = A22Service.getCapacity();
                LOG.debug("got capacity for " + caps.size() + " parks");
                for (A22CarParkCapacity parkCapacity : caps) {
                    String stationId = IDENTIFIER_NAMESPACE +":"+ parkCapacity.getId().toString();
                    Long free = parkCapacity.getPosti_liberi();
                    Long capacita = parkCapacity.getCapienza();
                    Long occupaid = capacita != null && free != null ? capacita - free : null;
                    Long stato = parkCapacity.getStato();

                    StationDto stationDto = stationDtoMap.get(stationId);

                    dataMap.addRecord(stationId, FREE_TYPE,
                            new SimpleRecordDto(timestamp, free, period));
                    dataMap.addRecord(stationId, OCCUPIED_TYPE,
                            new SimpleRecordDto(timestamp, occupaid, period));
                    if (stato != null)
                        dataMap.addRecord(stationId, STATE_TYPE,
                                new SimpleRecordDto(timestamp,
                                        valuesMappingProperties.getProperty("a22parking.datatype." + STATE_TYPE + "." + stato),
                                        period));

                    stationDto.getMetaData().put(STATION_METADATA_CAPACITA, capacita);
                }
                StationList stationList = new StationList(stationDtoMap.values());
                pusher.syncStations(stationList);
                pusher.pushData(dataMap);
            } catch (Exception e) {
                LOG.error("step 3 failed, continuing anyway to read de-auth...", e);
            }

            // step 4
            A22Service.close();
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(e);
        } finally {
            long stopTime = System.currentTimeMillis();
            LOG.debug("elaboration time (millis): " + (stopTime - startTime));
        }
    }

    private A22ParkingConnector setupA22ServiceConnector() throws IOException {
        String url;
        String user;
        String password;

        // read connector auth informations
        A22Properties prop = new A22Properties("a22connector.properties");
        url = prop.getProperty("url");
        user = prop.getProperty("user");
        password = prop.getProperty("password");

        A22ParkingConnector a22ParkingConnector = new A22ParkingConnector(url, user, password);

        return a22ParkingConnector;
    }

    private void setupDataType() {
        List<DataTypeDto> dataTypeDtoList = new ArrayList<>();
        String[] dataTypes = new String[]{FREE_TYPE, OCCUPIED_TYPE, STATE_TYPE};
        for (String dataType : dataTypes) {
            dataTypeDtoList.add(
                    new DataTypeDto(dataType,
                            datatypesProperties.getProperty("a22parking.datatype." + dataType + ".unit"),
                            datatypesProperties.getProperty("a22parking.datatype." + dataType + ".description"),
                            datatypesProperties.getProperty("a22parking.datatype." + dataType + ".rtype"))
            );
        }
        pusher.syncDataTypes(dataTypeDtoList);
    }

}
