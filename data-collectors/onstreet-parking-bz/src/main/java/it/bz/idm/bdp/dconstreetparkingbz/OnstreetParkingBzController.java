package it.bz.idm.bdp.dconstreetparkingbz;

import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import it.bz.idm.bdp.dconstreetparkingbz.dto.OnstreetParkingBzSensorDto;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;

/**
 * This class is a Spring Controller that implements methods that are are exposed as JSON REST services.
 *
 */
@RequestMapping("/json")
@Controller
@PropertySource({ "classpath:/META-INF/spring/application.properties" })
public class OnstreetParkingBzController {

    private static final Logger LOG = LogManager.getLogger(OnstreetParkingBzController.class.getName());

    @Autowired
    private OnstreetParkingBzDataConverter converter;

    @Autowired
    private OnstreetParkingBzDataPusher pusher;

    /**
     * This method is invoked by AXIANS platform that sends measurements regarding the status of the Onstreet Parking Sensors.
     * The data is a JSON String that will be parsed, converted in DataMapDto<RecordDtoImpl> and sent to the OpenDataHub
     * calling JSONPusher#pushData method.
     * 
     * @param jsonStr
     * @return
     */
    @RequestMapping(value = "/pushRecords", method = {RequestMethod.POST,RequestMethod.GET})
    public @ResponseBody Object pushRecords(@RequestBody(required = false) String jsonStr) {
        try {
            LOG.debug("jsonStr="+jsonStr);

            OnstreetParkingBzSensorDto sensorDto = converter.convertSensorResponseToInternalDTO(jsonStr);

            if ( sensorDto == null ) {
                return ResponseEntity.ok("OK");
            }

            //If CheckMissingStations is true, before pushing data we check if Station exists with current id
            //If Station does not exist (it is missing in Google Spreadsheet), we create a default StationDto
            if ( converter.isCheckMissingStations() ) {
                String stationId = sensorDto.getValueId();
                String stationType = converter.getStationType();

                //We must do a fetchStations, check if the station is missing, add it if necessary and do a syncStations
                //If we send only one new station using syncStations, all stations not included in the list are set to not active!!!
                List<StationDto> fetchStations = pusher.fetchStations(null, converter.getOrigin());
                boolean found = false;
                for (int i = 0; !found && i < fetchStations.size() ; i++ ) {
                    StationDto existingStationDto = fetchStations.get(i);
                    String existingId = existingStationDto.getId();
                    //String existingType = existingStationDto.getStationType();
                    if ( stationId.equals(existingId) ) {
                        found = true;
                    }
                }
                if ( !found ) {
                    LOG.warn("NOT FOUND station type='"+stationType+"'  id='"+stationId+"': CREATING A DEFAULT STATION!");
                    StationList stationList = converter.getStation(stationId, "DEFAULT "+stationId, 46.494825D, 11.339989D);
                    stationList.addAll(fetchStations);
                    Object syncStations = pusher.syncStations(stationList);
                    LOG.warn("CREATED STATION. Return value="+syncStations);
                }
            }

            DataMapDto<RecordDtoImpl> stationRec = pusher.mapData(sensorDto);
            if (stationRec != null){
                pusher.pushData(stationRec);
            }

            return ResponseEntity.ok("OK");
        } catch (Throwable tx) {
            LOG.error("Exception in pushRecords!", tx);
            return ResponseEntity.ok("Exception: "+tx);
        }
    }

}
