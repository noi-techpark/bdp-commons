// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.dconstreetparkingbz;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import it.bz.idm.bdp.dconstreetparkingbz.dto.OnstreetParkingBzSensorDto;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.ProvenanceDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;
import it.bz.idm.bdp.json.JSONPusher;
import it.bz.idm.bdp.json.NonBlockingJSONPusher;

@Service
@PropertySource({ "classpath:/META-INF/spring/application.properties" })
public class OnstreetParkingBzDataPusher extends NonBlockingJSONPusher{

    private static final Logger LOG = LoggerFactory.getLogger(OnstreetParkingBzDataPusher.class.getName());

    @Autowired
    private Environment env;

    @Autowired
    private OnstreetParkingBzDataConverter converter;

//    @Autowired
//    private RestClient reader;

    private List<DataTypeDto> dataTypes;

    @Override
    public String initIntegreenTypology() {
        String stationType = OnstreetParkingBzDataConverter.STATION_TYPE_SENSOR;
        return stationType;
    }

    @Override
    public ProvenanceDto defineProvenance() {
        return new ProvenanceDto(null,env.getProperty("provenance_name"), env.getProperty("provenance_version"),  env.getProperty("app.origin"));
    }

    @Override
    public <T> DataMapDto<RecordDtoImpl> mapData(T rawData) {
        String methodName = "mapData";
        LOG.debug("START."+methodName);

        OnstreetParkingBzSensorDto data = (OnstreetParkingBzSensorDto) rawData;
        if (data == null) {
            LOG.info(methodName+" DATA IS NULL, EXIT!");
            return null;
        }

        //Create return Datamap
        DataMapDto<RecordDtoImpl> map = new DataMapDto<>();

        //Get values to put in the map
        Integer period = converter.getPeriod();
        String dataTypeName = OnstreetParkingBzDataConverter.DATA_TYPE_OCCUPIED;
        String stationId = data.getValueId();
        Long value = data.getValueOccupied();
        Long timestamp = data.getValueTimestamp();

        //Add the Station to DataMap
        DataMapDto<RecordDtoImpl> recordsByStation = new DataMapDto<RecordDtoImpl>();
        map.getBranch().put(stationId, recordsByStation);

        //Add the DataType to DataMap
        DataMapDto<RecordDtoImpl> recordsByType = new DataMapDto<RecordDtoImpl>();
        recordsByStation.getBranch().put(dataTypeName, recordsByType);
        List<RecordDtoImpl> dataList = new ArrayList<RecordDtoImpl>();
        recordsByType.setData(dataList);

        //Add measurement to DataMap
        SimpleRecordDto record = new SimpleRecordDto();
        record.setValue(value);
        record.setTimestamp(timestamp);
        record.setPeriod(period);
        dataList.add(record);

        LOG.debug("map: "+map);
        LOG.debug("END."+methodName);
        return map;
    }

    public StationList mapSensors2Bdp() {
        LOG.debug("START.mapSensors2Bdp");
//        if (data == null) {
//            return null;
//        }

        int countStations = 0;
        StationList stations = new StationList();

        //Convert Spreadsheet to StationList

        if ( LOG.isDebugEnabled() ) {
            LOG.debug("countStations="+countStations+"  stations: "+stations);
            LOG.debug("END.mapSensors2Bdp");
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
            dataTypes = new ArrayList<DataTypeDto>();
            dataTypes.add(new DataTypeDto(OnstreetParkingBzDataConverter.DATA_TYPE_OCCUPIED, null, OnstreetParkingBzDataConverter.DATA_TYPE_OCCUPIED, null, converter.getPeriod()));
        }
        return dataTypes;
    }

}
