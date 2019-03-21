package it.bz.idm.bdp.dcparkingtn;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.stereotype.Service;

import it.bz.idm.bdp.dcparkingtn.dto.ParkingAreaServiceDto;
import it.bz.idm.bdp.dcparkingtn.dto.ParkingTnDto;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;
import it.bz.idm.bdp.json.JSONPusher;

@Service
public class ParkingTnDataPusher extends JSONPusher {

    public static final String PARKING_TYPE_IDENTIFIER = "occupied";

	private static final Logger LOG = LogManager.getLogger(ParkingTnDataPusher.class.getName());

    @Autowired
    private Environment env;

    public ParkingTnDataPusher() {
        LOG.debug("START.constructor.");
        LOG.debug("END.constructor.");
    }

    @PostConstruct
    private void init() {
        LOG.debug("START.init.");
        //Ensure the JSON converter is used instead of the XML converter (otherwise we get an HTTP 415 error)
        //this must be done because we added dependencies to com.fasterxml.jackson.dataformat.xml.XmlMapper to read data from IIT web service!
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        List<HttpMessageConverter<?>> newMessageConverters = new ArrayList<HttpMessageConverter<?>>();
        for (HttpMessageConverter<?> messageConverter : messageConverters) {
            if ( messageConverter instanceof MappingJackson2XmlHttpMessageConverter || messageConverter instanceof Jaxb2RootElementHttpMessageConverter ) {
                LOG.debug("REMOVE   converter: " + messageConverter.getClass().getName());
            } else {
                LOG.debug("PRESERVE converter: " + messageConverter.getClass().getName());
                newMessageConverters.add(messageConverter);
            }
        }
        restTemplate.setMessageConverters(newMessageConverters);
        LOG.debug("END.init.");
    }

    @Override
    public String initIntegreenTypology() {
        String stationType = "ParkingStation";
        return stationType;
    }

    @Override
    public <T> DataMapDto<RecordDtoImpl> mapData(T rawData) {
        LOG.debug("START.mapData");

        @SuppressWarnings("unchecked")
        List<ParkingTnDto> data = (List<ParkingTnDto>) rawData;
        if (data == null) {
            return null;
        }

        DataMapDto<RecordDtoImpl> map = new DataMapDto<>();
        Date now = new Date();
        Long timestamp = now.getTime();
        Integer period = env.getProperty(ParkingTnDataConverter.PERIOD_KEY, Integer.class);

        int countBranches = 0;
        int countMeasures = 0;

        for (ParkingTnDto dto: data) {
            StationDto stationDto = dto.getStation();
            ParkingAreaServiceDto extDto = dto.getParkingArea();
            Integer slotsOccupied = extDto.getSlotsTotal()-extDto.getSlotsAvailable();

            //Exclude Measures having slotsAvailable<0
            if ( slotsOccupied != null && slotsOccupied >= 0 ) {
                SimpleRecordDto record = new SimpleRecordDto();
                record.setValue(slotsOccupied);
                record.setTimestamp(timestamp);
                record.setPeriod(period);

                //Check if we already treated this station (branch), if not found create the map and the list of records
                DataMapDto<RecordDtoImpl> typeMap = map.getBranch().get(stationDto.getId());
                if ( typeMap  == null ) {
                    typeMap  = new DataMapDto<RecordDtoImpl>();
                    map.getBranch().put(stationDto.getId(), typeMap);
                    DataMapDto<RecordDtoImpl> recordsByType = new DataMapDto<>();
                    typeMap.getBranch().put(PARKING_TYPE_IDENTIFIER, recordsByType);
                    List<RecordDtoImpl> dataList = new ArrayList<RecordDtoImpl>();
                    recordsByType.setData(dataList);
                    countBranches++;
                    countMeasures++;
                }

                //Add the measure in the list
                List<RecordDtoImpl> records = typeMap.getBranch().get(PARKING_TYPE_IDENTIFIER).getData();
                records.add(record);
                LOG.debug("ADD  MEASURE:  id="+stationDto.getId()+", slotsOccupied="+slotsOccupied);
            } else {
                LOG.debug("SKIP MEASURE:  id="+stationDto.getId()+", slotsOccupied="+slotsOccupied);
            }
        }

        LOG.debug("countBranches="+countBranches+", countMeasures="+countMeasures);
        LOG.debug("map: "+map);
        LOG.debug("END.mapData");
        return map;
    }

    public StationList mapStations2Bdp(List<ParkingTnDto> data) {
        LOG.debug("START.mapStations2Bdp");
        if (data == null) {
            return null;
        }

        int countStations = 0;
        StationList stations = new StationList();
        for (ParkingTnDto dto : data) {
            StationDto stationDto = dto.getStation();
            ParkingAreaServiceDto extDto = dto.getParkingArea();
            Integer slotsAvailable = extDto.getSlotsAvailable();
            //Exclude Stations having slotsAvailable==-2 (i.e. does not provide real time measurements, see Analysis doc)
            if ( slotsAvailable != null && slotsAvailable != -2 ) {
                stations.add(stationDto);
                countStations++;
                LOG.debug("ADD  STATION:  id="+stationDto.getId()+", slotsAvailable="+slotsAvailable);
            } else {
                LOG.debug("SKIP STATION:  id="+stationDto.getId()+", slotsAvailable="+slotsAvailable);
            }
        }
        LOG.debug("countStations="+countStations+"  stations: "+stations);
        LOG.debug("END.mapStations2Bdp");
        return stations;
    }

    public List<DataTypeDto> mapDataTypes2Bdp(DataMapDto<RecordDtoImpl> stationRec) {
        LOG.debug("START.mapdataTypes2Bdp");
        if (stationRec == null) {
            return null;
        }

        List<DataTypeDto> dataTypeList = new ArrayList<DataTypeDto>();
        Set<String> dataTypeNames = new HashSet<String>();

        Map<String, DataMapDto<RecordDtoImpl>> branch1 = stationRec.getBranch();
        Set<String> keySet1 = branch1.keySet();
        for (String key1 : keySet1) {
            LOG.debug("check key1="+key1);
            DataMapDto<RecordDtoImpl> dataMapDto1 = branch1.get(key1);

            Map<String, DataMapDto<RecordDtoImpl>> branch2 = dataMapDto1.getBranch();
            Set<String> keySet2 = branch2.keySet();
            for (String key2 : keySet2) {
                LOG.debug("check key2="+key2);
                DataMapDto<RecordDtoImpl> dataMapDto2 = branch2.get(key2);
                List<RecordDtoImpl> data2 = dataMapDto2.getData();

                for (RecordDtoImpl recordDtoImpl : data2) {
                    LOG.debug("check recordDtoImpl="+recordDtoImpl);
                    SimpleRecordDto sr = (SimpleRecordDto) recordDtoImpl;

                    if ( !dataTypeNames.contains(key2) ) {
                        DataTypeDto type = new DataTypeDto();
                        type.setName(key2);
                        type.setPeriod(sr.getPeriod());
//                      type.setUnit(dto.getUnit());
                        LOG.debug("ADD DataTypeDto="+type);
                        dataTypeList.add(type);
                        dataTypeNames.add(key2);
                    }
                }

            }

        }

        LOG.debug("dataTypeList: "+dataTypeList);
        LOG.debug("END.mapdataTypes2Bdp");
        return dataTypeList;
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
}
