// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.dcbikesharingpapin;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.bz.idm.bdp.dcbikesharingpapin.dto.BikesharingPapinStationDto;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;

@Component
public class BikesharingMappingUtil {
    private static final Logger LOG = LoggerFactory.getLogger(BikesharingMappingUtil.class.getName());
    @Autowired
    private BikesharingPapinDataConverter converter;
    
    private List<DataTypeDto> dataTypes;

    public <T> DataMapDto<RecordDtoImpl> mapData(T rawData) {
        LOG.debug("START.mapData");

        DataMapDto<RecordDtoImpl> map = mapStationData(rawData);

        LOG.debug("END.mapData");
        return map;
    }

    public <T> DataMapDto<RecordDtoImpl> mapStationData(T rawData) {
        LOG.debug("START.mapStationData");

        @SuppressWarnings("unchecked")
        List<BikesharingPapinStationDto> data = (List<BikesharingPapinStationDto>) rawData;
        if (data == null) {
            return null;
        }

        DataMapDto<RecordDtoImpl> map = new DataMapDto<>();
        Integer period = converter.getPeriod();

        //All necessary information is stored in BikesharingPapinStationDto
        for (BikesharingPapinStationDto station : data) {

            Long measurementTimestamp = station.getMeasurementTimestamp();

            //Level 1. Station
            String stationId = station.getId();
            DataMapDto<RecordDtoImpl> recordsByStation = new DataMapDto<RecordDtoImpl>();
            map.getBranch().put(stationId, recordsByStation);

            addMeasurementForStationAndType(period, measurementTimestamp, stationId, recordsByStation, BikesharingPapinDataConverter.DATA_TYPE_STATION_AVAILABILITY,    station.getState());
            addMeasurementForStationAndType(period, measurementTimestamp, stationId, recordsByStation, BikesharingPapinDataConverter.DATA_TYPE_STATION_IS_CLOSE,        station.getClose());
        }

        if ( LOG.isDebugEnabled() ) {
            logMapData(map);
            LOG.debug("END.mapStationData");
        }
        return map;
    }

    public StationList mapStations2Bdp(List<BikesharingPapinStationDto> data) {
        LOG.debug("START.mapStations2Bdp");
        if (data == null) {
            return null;
        }

        int countStations = 0;
        StationList stations = new StationList();
        for (BikesharingPapinStationDto dto : data) {
            StationDto stationDto = converter.convertBikesharingStationDtoToStationDto(dto);

            //Add Station for level 1. BikesharingStation
            stations.add(stationDto);
            countStations++;
            LOG.debug("ADD  STATION:  id="+stationDto.getId());

        }
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("countStations="+countStations+"  stations: "+stations);
            LOG.debug("END.mapStations2Bdp");
        }
        return stations;
    }

    /**
     * Return a static list of DataTypeDtos, for the three type of stations: Station
     * 
     * @return
     */
    public List<DataTypeDto> mapDataTypes2Bdp() {
        if ( dataTypes == null ) {
            dataTypes = new ArrayList<DataTypeDto>();
            //Station
            dataTypes.add(new DataTypeDto(BikesharingPapinDataConverter.DATA_TYPE_STATION_AVAILABILITY,    null, BikesharingPapinDataConverter.DATA_TYPE_STATION_AVAILABILITY,    null, converter.getPeriod()));
            dataTypes.add(new DataTypeDto(BikesharingPapinDataConverter.DATA_TYPE_STATION_IS_CLOSE,        null, BikesharingPapinDataConverter.DATA_TYPE_STATION_IS_CLOSE,        null, converter.getPeriod()));
        }
        return dataTypes;
    }

    private void addMeasurementForStationAndType(Integer period, Long measurementTimestamp, String stationId, DataMapDto<RecordDtoImpl> recordsByStation, String dataTypeName, Object measurementValue) {
        if ( DCUtils.paramNotNull(measurementValue) && measurementTimestamp != null ) {
            SimpleRecordDto record = new SimpleRecordDto();
            record.setValue(measurementValue);
            record.setTimestamp(measurementTimestamp);
            record.setPeriod(period);

            //Check if we already treated this type (branch), if not found create the map and the list of records
            DataMapDto<RecordDtoImpl> recordsByType = recordsByStation.getBranch().get(dataTypeName);
            if ( recordsByType == null ) {
                recordsByType = new DataMapDto<RecordDtoImpl>();
                recordsByStation.getBranch().put(dataTypeName, recordsByType);
            }

            //Add the measure in the list
            List<RecordDtoImpl> records = recordsByType.getData();
            records.add(record);

            LOG.debug("ADD  MEASURE:  id="+stationId+", typeName="+dataTypeName+"  value="+measurementValue);
        }
    }

    private void logMapData(DataMapDto<RecordDtoImpl> map) {
        String name = map!=null ? map.getName() : null;
        List<RecordDtoImpl> list = map!=null ? map.getData() : null;
        int size = list!=null ? list.size() : 0;
        LOG.debug("map: "+map+"  name="+name+"  dataSize="+size);
        if ( size > 0 ) {
            for ( int i = 0 ; i < size ; i++ ) {
                RecordDtoImpl r = list.get(i);
                LOG.debug(i+")  value="+r.getValue()+"  ts="+r.getTimestamp());
            }
        }
    }
}
