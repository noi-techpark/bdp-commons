// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.dcparkingtn;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import it.bz.idm.bdp.dcparkingtn.dto.ParkingAreaServiceDto;
import it.bz.idm.bdp.dcparkingtn.dto.ParkingTnDto;
import it.bz.idm.bdp.dto.StationDto;

@Service
public class ParkingTnDataConverter {

    private static final Logger LOG = LoggerFactory.getLogger(ParkingTnDataConverter.class.getName());

    public static final String ORIGIN_KEY                = "app.origin";
    public static final String PERIOD_KEY                = "app.period";

    public static final String STATION_TYPE_KEY               = "app.station.type";
    public static final String STATION_CODE_ALLOWED_CHARS_KEY = "app.station.code.allowed_chars";

    public static final String STATION_KEY_PARAM         = "key";
    public static final String STATION_CODE_PREFIX_PARAM = "code-prefix";

    @Autowired
    private Environment env;

    //This must be initialized in application.properties file (for example FBK)
    private String origin;
    //This must be initialized in application.properties file (for example ParkingStation)
    private String stationType;
    //This must be initialized in application.properties file (for example abcdefghijklmnopqrstuvwxyz0123456789)
    private String stationCodeAllowedChars;
 // TODO Auto-generated method

/*
 *  Example of json provided by the service
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
    public String getStationCodeAllowedChars() {
        if ( this.stationCodeAllowedChars == null ) {
            this.stationCodeAllowedChars = env.getProperty(STATION_CODE_ALLOWED_CHARS_KEY);
        }
        return this.stationCodeAllowedChars;
    }

    public List<ParkingTnDto> convertToInternalDTO(List<ParkingAreaServiceDto> dataList, String municipality, String codePrefix) throws Exception {
        try {
            LOG.debug("dataList: "+dataList);
            if ( dataList == null ) {
                return null;
            }
            List<ParkingTnDto> fetchedData = new ArrayList<ParkingTnDto>();
            for (ParkingAreaServiceDto extDto : dataList) {
                StationDto stationDto = convertExternalDtoToStationDto(extDto, municipality, codePrefix);
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

    public StationDto convertExternalDtoToStationDto(ParkingAreaServiceDto extDto, String municipality, String codePrefix) {
        StationDto station = null;
        if ( extDto!=null ) {
            station = new StationDto();

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

            station.getMetaData().put("municipality",  municipality);
            station.setStationType(getStationType());

            //From ParkingStationDto
            station.getMetaData().put("capacity",extDto.getSlotsTotal());
            station.getMetaData().put("mainaddress",extDto.getDescription());
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
            String tmpName = DCUtils.removeUnexpectedChars(name.toLowerCase().trim(), getStationCodeAllowedChars());
            retval = DCUtils.allowNulls(prefix).trim() + tmpName;
        }
        return retval;
    }

}
