// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.dcbikesharingmoqo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import it.bz.idm.bdp.dcbikesharingmoqo.dto.BikeDto;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.ProvenanceDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;
import it.bz.idm.bdp.json.NonBlockingJSONPusher;

@Service
public class BikesharingMoqoDataPusher extends NonBlockingJSONPusher {

    private static final Logger LOG = LoggerFactory.getLogger(BikesharingMoqoDataPusher.class.getName());

    @Autowired
    private Environment env;

    @Autowired
    private BikesharingMoqoDataConverter converter;

    @Value("${app.origin}")
    private String origin;
    private List<DataTypeDto> dataTypes;

    public BikesharingMoqoDataPusher() {
        LOG.debug("START.constructor.");
        LOG.debug("END.constructor.");
    }

    @Override
    public String initIntegreenTypology() {
        String stationType = BikesharingMoqoDataConverter.STATION_TYPE;
        return stationType;
    }

    @Override
    public <T> DataMapDto<RecordDtoImpl> mapData(T rawData) {
        LOG.debug("START.mapData");

        @SuppressWarnings("unchecked")
        List<BikeDto> data = (List<BikeDto>) rawData;
        if (data == null) {
            return null;
        }

        DataMapDto<RecordDtoImpl> map = new DataMapDto<>();
        Integer period = converter.getPeriod();

        int countStations = 0;
        int countBranches = 0;
        int countMeasures = 0;

        //All necessary information is stored in BikeDto
        for (BikeDto dto : data) {

            StationDto stationDto = converter.convertBikeDtoToStationDto(dto);

            String stationId = stationDto.getId();
            Long measurementTimestamp = dto.getMeasurementTimestamp();
            Map<String, Object> stationMetaData = stationDto.getMetaData();
            List<DataTypeDto> dataTypes = mapDataTypes2Bdp();

            //Check if we already treated this station (branch), if not found create the map and the list of records
            DataMapDto<RecordDtoImpl> recordsByStation = map.getBranch().get(stationId);
            if ( recordsByStation == null ) {
                recordsByStation = new DataMapDto<RecordDtoImpl>();
                map.getBranch().put(stationId, recordsByStation);
                List<RecordDtoImpl> dataList = new ArrayList<RecordDtoImpl>();
                recordsByStation.setData(dataList);
                countStations++;
            }

            //Get measurement values for each data type, we stored them in stationMetaData
            for (DataTypeDto dataTypeDto : dataTypes) {

                String dataTypeName = dataTypeDto.getName();

                //We put values in station metadata map ("availability" and "future-availability")
                Object measurementValue = stationMetaData.get(dataTypeName);

                if ( measurementValue!=null && measurementTimestamp!=null ) {
                    SimpleRecordDto record = new SimpleRecordDto();
                    record.setValue(measurementValue);
                    record.setTimestamp(measurementTimestamp);
                    record.setPeriod(period);

                    //Check if we already treated this type (branch), if not found create the map and the list of records
                    DataMapDto<RecordDtoImpl> recordsByType = recordsByStation.getBranch().get(dataTypeName);
                    if ( recordsByType == null ) {
                        recordsByType = new DataMapDto<RecordDtoImpl>();
                        recordsByStation.getBranch().put(dataTypeName, recordsByType);
                        List<RecordDtoImpl> dataList = new ArrayList<RecordDtoImpl>();
                        recordsByType.setData(dataList);
                        countBranches++;
                    }

                    //Add the measure in the list
                    List<RecordDtoImpl> records = recordsByType.getData();
                    records.add(record);
                    countMeasures++;

                    LOG.debug("ADD  MEASURE:  id="+stationDto.getId()+", typeName="+dataTypeName+"  value="+measurementValue);
                }

            }

        }

        LOG.debug("countStations="+countStations+"  countBranches="+countBranches+", countMeasures="+countMeasures);
        LOG.debug("map: "+map);
        LOG.debug("END.mapData");
        return map;
    }

    public StationList mapStations2Bdp(List<BikeDto> data) {
        LOG.debug("START.mapStations2Bdp");
        if (data == null) {
            return null;
        }

        int countStations = 0;
        StationList stations = new StationList();
        for (BikeDto dto : data) {
            StationDto stationDto = converter.convertBikeDtoToStationDto(dto);

            stations.add(stationDto);
            countStations++;
            LOG.debug("ADD  STATION:  id="+stationDto.getId());

        }
        LOG.debug("countStations="+countStations+"  stations: "+stations);
        LOG.debug("END.mapStations2Bdp");
        return stations;
    }

    /**
     * Return a static list of DataTypeDtos:
     * "availability", "future-availability", "in-maintenance"
     * 
     * @return
     */
    public List<DataTypeDto> mapDataTypes2Bdp() {
        if ( dataTypes == null ) {
            dataTypes = new ArrayList<DataTypeDto>();
            //availability
            dataTypes.add(new DataTypeDto(BikesharingMoqoDataConverter.DATA_TYPE_AVAILABILITY, "boolean", BikesharingMoqoDataConverter.DATA_TYPE_AVAILABILITY, null, converter.getPeriod()));
            //future-availability
            dataTypes.add(new DataTypeDto(BikesharingMoqoDataConverter.DATA_TYPE_FUTURE_AVAILABILITY, "boolean", BikesharingMoqoDataConverter.DATA_TYPE_FUTURE_AVAILABILITY, null, converter.getPeriod()));
            //in_maintenance
            dataTypes.add(new DataTypeDto(BikesharingMoqoDataConverter.DATA_TYPE_IN_MAINTENANCE, "boolean", BikesharingMoqoDataConverter.DATA_TYPE_IN_MAINTENANCE, null, converter.getPeriod()));
        }
        return dataTypes;
    }

    @Override
    public String toString() {
        String str1 = "http://" + config.getString(HOST_KEY) + ":" + config.getString(PORT_KEY) + config.getString("json_endpoint");
        String str2 =
                "integreenTypology=" + this.integreenTypology   + "  " +
                "DEFAULT_HOST="      + DEFAULT_HOST     + "  " +
                "DEFAULT_PORT="      + DEFAULT_PORT     + "  " +
                "DEFAULT_ENDPOINT="  + DEFAULT_ENDPOINT + "  " +
                "";
        return str2 + " ---> " + str1;
    }

    @Override
    public ProvenanceDto defineProvenance() {
        return new ProvenanceDto(null,env.getProperty("provenance_name"), env.getProperty("provenance_version"), origin);
    }

}
