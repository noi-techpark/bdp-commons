// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.dcbikesharingbz;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.bz.idm.bdp.dcbikesharingbz.dto.BikesharingBzBayDto;
import it.bz.idm.bdp.dcbikesharingbz.dto.BikesharingBzStationDto;
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
    private BikesharingBzDataConverter converter;

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
        List<BikesharingBzStationDto> data = (List<BikesharingBzStationDto>) rawData;
        if (data == null) {
            return null;
        }

        DataMapDto<RecordDtoImpl> map = new DataMapDto<>();
        Integer period = converter.getPeriod();

        //All necessary information is stored in BikesharingBzStationDto
        for (BikesharingBzStationDto station : data) {

            Long measurementTimestamp = station.getMeasurementTimestamp();

            //Level 1. Station
            String stationId = station.getId();
            DataMapDto<RecordDtoImpl> recordsByStation = new DataMapDto<>();
            map.getBranch().put(stationId, recordsByStation);

            addMeasurementForStationAndType(period, measurementTimestamp, stationId, recordsByStation, BikesharingBzDataConverter.DATA_TYPE_STATION_AVAILABILITY,    station.getState());
            addMeasurementForStationAndType(period, measurementTimestamp, stationId, recordsByStation, BikesharingBzDataConverter.DATA_TYPE_STATION_NUMBER_AVAILABE, station.getAvailableVehicles());
            addMeasurementForStationAndType(period, measurementTimestamp, stationId, recordsByStation, BikesharingBzDataConverter.DATA_TYPE_STATION_TOTAL_BAYS,      station.getTotalBays());
            addMeasurementForStationAndType(period, measurementTimestamp, stationId, recordsByStation, BikesharingBzDataConverter.DATA_TYPE_STATION_FREE_BAYS,       station.getFreeBays());
        }

        if ( LOG.isDebugEnabled() ) {
            logMapData(map);
            LOG.debug("END.mapStationData");
        }
        return map;
    }
    public <T> DataMapDto<RecordDtoImpl> mapBayData(T rawData) {
        LOG.debug("START.mapBayData");

        @SuppressWarnings("unchecked")
        List<BikesharingBzStationDto> data = (List<BikesharingBzStationDto>) rawData;
        if (data == null) {
            return null;
        }

        DataMapDto<RecordDtoImpl> map = new DataMapDto<>();
        Integer period = converter.getPeriod();

        //All necessary information is stored in BikesharingBzStationDto
        for (BikesharingBzStationDto station : data) {

            Long measurementTimestamp = station.getMeasurementTimestamp();

            List<BikesharingBzBayDto> bayList = station.getBayList();
            for (BikesharingBzBayDto bay : bayList) {
                //Level 2. Bay
                String bayId = bay.getLabel();
                DataMapDto<RecordDtoImpl> recordsByBay = new DataMapDto<>();
                map.getBranch().put(bayId, recordsByBay);

                addMeasurementForStationAndType(period, measurementTimestamp, bayId, recordsByBay, BikesharingBzDataConverter.DATA_TYPE_BAY_AVAILABILITY, bay.getState());
                addMeasurementForStationAndType(period, measurementTimestamp, bayId, recordsByBay, BikesharingBzDataConverter.DATA_TYPE_BAY_USAGE_STATE,  bay.getUsageState());

            }

        }

        if ( LOG.isDebugEnabled() ) {
            logMapData(map);
            LOG.debug("END.mapBayData");
        }
        return map;
    }

    public <T> DataMapDto<RecordDtoImpl> mapBicycleData(T rawData) {
        LOG.debug("START.mapBicycleData");

        @SuppressWarnings("unchecked")
        List<BikesharingBzStationDto> data = (List<BikesharingBzStationDto>) rawData;
        if (data == null) {
            return null;
        }

        DataMapDto<RecordDtoImpl> map = new DataMapDto<>();
        Integer period = converter.getPeriod();

        //All necessary information is stored in BikesharingBzStationDto
        for (BikesharingBzStationDto station : data) {

            Long measurementTimestamp = station.getMeasurementTimestamp();

            List<BikesharingBzBayDto> bayList = station.getBayList();
            for (BikesharingBzBayDto bay : bayList) {
                //Level 3. Bicycle (may not be present)
                String bicycleId = bay.getVehicleCode();
                if ( bicycleId != null ) {
                    DataMapDto<RecordDtoImpl> recordsByBicycle = new DataMapDto<>();
                    map.getBranch().put(bicycleId, recordsByBicycle);

                    addMeasurementForStationAndType(period, measurementTimestamp, bicycleId, recordsByBicycle, BikesharingBzDataConverter.DATA_TYPE_BICYCLE_AVAILABILITY,  DCUtils.convertBooleanToString(bay.getVehiclePresent()));
                    addMeasurementForStationAndType(period, measurementTimestamp, bicycleId, recordsByBicycle, BikesharingBzDataConverter.DATA_TYPE_BICYCLE_BATTERY_STATE, bay.getVehicleBatteryState());
                }
            }

        }

        if ( LOG.isDebugEnabled() ) {
            logMapData(map);
            LOG.debug("END.mapBicycleData");
        }
        return map;
    }

    public StationList mapStations2Bdp(List<BikesharingBzStationDto> data) {
        LOG.debug("START.mapStations2Bdp");
        if (data == null) {
            return null;
        }

        int countStations = 0;
        StationList stations = new StationList();
        for (BikesharingBzStationDto dto : data) {
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

    public StationList mapBays2Bdp(List<BikesharingBzStationDto> data) {
        LOG.debug("START.mapBays2Bdp");
        if (data == null) {
            return null;
        }

        int countStations = 0;
        StationList stations = new StationList();
        for (BikesharingBzStationDto dtoL1 : data) {
            for (BikesharingBzBayDto dtoL2 : dtoL1.getBayList()) {
                StationDto bayStationDto = converter.convertBikesharingBayDtoToStationDto_Bay(dtoL2);
                //Add Station for level 2. Bicyclestationbay
                if ( bayStationDto != null ) {
                    stations.add(bayStationDto);
                    countStations++;
                    LOG.debug("ADD  BAY:      id="+bayStationDto.getId());
                }
            }

        }
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("countStations="+countStations+"  stations: "+stations);
            LOG.debug("END.mapBays2Bdp");
        }
        return stations;
    }

    public StationList mapBicycles2Bdp(List<BikesharingBzStationDto> data) {
        LOG.debug("START.mapBicycles2Bdp");
        if (data == null) {
            return null;
        }

        int countStations = 0;
        StationList stations = new StationList();
        for (BikesharingBzStationDto dtoL1 : data) {
            for (BikesharingBzBayDto dtoL2 : dtoL1.getBayList()) {
                StationDto bicycleStationDto = converter.convertBikesharingBayDtoToStationDto_Bicycle(dtoL2);
                //Add Station for level 3. Bicycle
                if ( bicycleStationDto != null ) {
                    stations.add(bicycleStationDto);
                    countStations++;
                    LOG.debug("ADD  BICYCLE:  id="+bicycleStationDto.getId());
                }
            }

        }
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("countStations="+countStations+"  stations: "+stations);
            LOG.debug("END.mapBicycles2Bdp");
        }
        return stations;
    }

    /**
     * Return a static list of DataTypeDtos, for the three type of stations: Station, Bay, Bicycle
     *
     * @return
     */
    public List<DataTypeDto> mapDataTypes2Bdp() {
        if ( dataTypes == null ) {
            dataTypes = new ArrayList<>();
            //Station
            dataTypes.add(new DataTypeDto(BikesharingBzDataConverter.DATA_TYPE_STATION_AVAILABILITY,    null, BikesharingBzDataConverter.DATA_TYPE_STATION_AVAILABILITY,    null, converter.getPeriod()));
            dataTypes.add(new DataTypeDto(BikesharingBzDataConverter.DATA_TYPE_STATION_NUMBER_AVAILABE, null, BikesharingBzDataConverter.DATA_TYPE_STATION_NUMBER_AVAILABE, null, converter.getPeriod()));
            dataTypes.add(new DataTypeDto(BikesharingBzDataConverter.DATA_TYPE_STATION_TOTAL_BAYS,      null, BikesharingBzDataConverter.DATA_TYPE_STATION_TOTAL_BAYS,      null, converter.getPeriod()));
            dataTypes.add(new DataTypeDto(BikesharingBzDataConverter.DATA_TYPE_STATION_FREE_BAYS,       null, BikesharingBzDataConverter.DATA_TYPE_STATION_FREE_BAYS,       null, converter.getPeriod()));
            //Bay
            //The type "availability" is reused for all the station types, we add it only once
            //dataTypes.add(new DataTypeDto(BikesharingBzDataConverter.DATA_TYPE_BAY_AVAILABILITY, null, BikesharingBzDataConverter.DATA_TYPE_BAY_AVAILABILITY, null, converter.getPeriod()));
            dataTypes.add(new DataTypeDto(BikesharingBzDataConverter.DATA_TYPE_BAY_USAGE_STATE,  null, BikesharingBzDataConverter.DATA_TYPE_BAY_USAGE_STATE,  null, converter.getPeriod()));
            //Bicycle
            //The type "availability" is reused for all the station types, we add it only once
            //dataTypes.add(new DataTypeDto(BikesharingBzDataConverter.DATA_TYPE_BICICLE_AVAILABILITY,  null, BikesharingBzDataConverter.DATA_TYPE_BICICLE_AVAILABILITY,  null, converter.getPeriod()));
            dataTypes.add(new DataTypeDto(BikesharingBzDataConverter.DATA_TYPE_BICYCLE_BATTERY_STATE, null, BikesharingBzDataConverter.DATA_TYPE_BICYCLE_BATTERY_STATE, null, converter.getPeriod()));
        }
        return dataTypes;
    }

    private void addMeasurementForStationAndType(Integer period, Long measurementTimestamp, String stationId, DataMapDto<RecordDtoImpl> recordsByStation, String dataTypeName, Object measurementValue) {
        if ( DCUtils.paramNotNull(measurementValue) && measurementTimestamp != null ) {
            SimpleRecordDto rec = new SimpleRecordDto();
            rec.setValue(measurementValue);
            rec.setTimestamp(measurementTimestamp);
            rec.setPeriod(period);

            //Check if we already treated this type (branch), if not found create the map and the list of records
            DataMapDto<RecordDtoImpl> recordsByType = recordsByStation.getBranch().get(dataTypeName);
            if ( recordsByType == null ) {
                recordsByType = new DataMapDto<>();
                recordsByStation.getBranch().put(dataTypeName, recordsByType);
            }

            //Add the measure in the list
            List<RecordDtoImpl> records = recordsByType.getData();
            records.add(rec);

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
