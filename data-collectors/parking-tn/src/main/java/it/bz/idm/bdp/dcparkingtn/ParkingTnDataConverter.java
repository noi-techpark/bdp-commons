package it.bz.idm.bdp.dcparkingtn;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import it.bz.idm.bdp.dcparkingtn.dto.ParkingAreaServiceDto;
import it.bz.idm.bdp.dcparkingtn.dto.ParkingTnDto;
import it.bz.idm.bdp.dto.parking.ParkingStationDto;

@Service
public class ParkingTnDataConverter {

    private static final Logger LOG = LogManager.getLogger(ParkingTnDataConverter.class.getName());

    public static final String ORIGIN_KEY                = "app.origin";
    public static final String PERIOD_KEY                = "app.period";

    public static final String STATION_TYPE_KEY          = "app.station.type";

    public static final String STATION_KEY_PARAM         = "key";
    public static final String STATION_CODE_PREFIX_PARAM = "code-prefix";

    public static final String ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789";

    @Autowired
    private Environment env;

    private String origin;
    private String stationType;

/*
    {
        "name": "Autosilo Buonconsiglio - P3",
        "description": "Via F. Petrarca, 1/5",
        "slotsTotal": 669,
        "slotsAvailable": -1,
        "position": [
            46.073612,
            11.124208
        ],
        "monitored": true,
        "extra": {
            "parkAndRide": false
        }
    }
 */

    public String getOrigin() {
        if ( this.origin == null ) {
            this.origin = env.getProperty(ORIGIN_KEY);
        }
        return this.origin;
    }
    public String getStationType() {
        if ( this.stationType == null ) {
            this.stationType = env.getProperty(STATION_TYPE_KEY);
        }
        return this.stationType;
    }

    public List<ParkingTnDto> convertToInternalDTO(List<ParkingAreaServiceDto> dataList, String municipality, String codePrefix) throws Exception {
        try {
            LOG.debug("dataList: "+dataList);
            if ( dataList == null ) {
                return null;
            }
            List<ParkingTnDto> fetchedData = new ArrayList<ParkingTnDto>();
            for (ParkingAreaServiceDto extDto : dataList) {
                ParkingStationDto stationDto = convertExternalDtoToStationDto(extDto, municipality, codePrefix);
                ParkingTnDto intDto = new ParkingTnDto(extDto, stationDto, municipality);
                fetchedData.add(intDto);
            }
            LOG.debug("fetchedData: "+fetchedData);
            return fetchedData;
        } catch (Exception ex) {
            LOG.error("ERROR: " + ex.getMessage(), ex);
            throw ex;
        }
    }

    public ParkingStationDto convertExternalDtoToStationDto(ParkingAreaServiceDto extDto, String municipality, String codePrefix) {
        ParkingStationDto station = null;
        if ( extDto!=null ) {
            station = new ParkingStationDto();

            //From StationDTO
            String id = calculateId(extDto.getName(), codePrefix);
            Double longitude = null;
            Double latitude = null;
            if ( extDto.getPosition() != null ) {
                List<Double> position = extDto.getPosition();
                if ( position.size() > 0 ) {
                    latitude = position.get(0);
                }
                if ( position.size() > 1 ) {
                    longitude = position.get(1);
                }
            }
            station.setId(id);
            station.setName(DCUtils.trunc(extDto.getName(), 255));
            station.setLongitude(longitude);
            station.setLatitude(latitude);
            //OMITTED: protected String crs;
            station.setOrigin(DCUtils.trunc(getOrigin(), 255));
            station.setMunicipality(DCUtils.trunc(municipality, 255));
            station.setStationType(getStationType());

            //From ParkingStationDto
            station.setSlots(extDto.getSlotsTotal());
            station.setAddress(extDto.getDescription());
            //OMITTED: station.setPhone(phone);
        }

        return station;
    }

//    public ParkingStationDto convertParkingTnDtoToStationDto(ParkingTnDto ptnDto) {
//        ParkingStationDto station = null;
//        if ( ptnDto!=null ) {
//            station = new ParkingStationDto();
//            ParkingAreaServiceDto extDto = ptnDto.getParkingArea();
//            String municipality = ptnDto.getMunicipality();
//
//            //From StationDTO
//            String id = toHexString(extDto.getName());
//            Double longitude = null;
//            Double latitude = null;
//            if ( extDto.getPosition() != null ) {
//                List<Double> position = extDto.getPosition();
//                if ( position.size() > 0 ) {
//                    longitude = position.get(0);
//                }
//                if ( position.size() > 1 ) {
//                    latitude = position.get(1);
//                }
//            }
//            station.setId(id);
//            station.setName(DCUtils.trunc(extDto.getName(), 255));
//            station.setLongitude(longitude);
//            station.setLatitude(latitude);
//            //OMITTED: protected String crs;
//            station.setOrigin(DCUtils.trunc(getOrigin(), 255));
//            station.setMunicipality(DCUtils.trunc(municipality, 255));
//            station.setStationType(getStationType());
//
//            //From ParkingStationDto
//            station.setSlots(extDto.getSlotsTotal());
//            station.setAddress(extDto.getDescription());
//            //OMITTED: station.setPhone(phone);
//        }
//
//        return station;
//    }

    private String calculateId(String name, String prefix) {
        String retval = null;
        if ( name != null ) {
            String tmpName = DCUtils.removeUnexpectedChars(name.toLowerCase().trim(), ALLOWED_CHARS);
            retval = DCUtils.allowNulls(prefix).trim() + tmpName;
        }
        return retval;
    }

}
