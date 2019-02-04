package it.bz.idm.bdp.dcmeteotn;

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
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.stereotype.Service;

import it.bz.idm.bdp.dcmeteotn.dto.MeteoTnDto;
import it.bz.idm.bdp.dcmeteotn.dto.MeteoTnMeasurementDto;
import it.bz.idm.bdp.dcmeteotn.dto.MeteoTnMeasurementListDto;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationList;
import it.bz.idm.bdp.dto.meteo.MeteoStationDto;
import it.bz.idm.bdp.json.JSONPusher;

@Service
public class MeteoTnDataPusher extends JSONPusher {

    private static final Logger LOG = LogManager.getLogger(MeteoTnDataPusher.class.getName());

//    @Autowired
//    private Environment env;

    @Autowired
    private MeteoTnDataConverter converter;

    public MeteoTnDataPusher() {
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
        String stationType = "Meteostation";
        return stationType;
    }

    @Override
    public <T> DataMapDto<RecordDtoImpl> mapData(T rawData) {
        LOG.debug("START.mapData");

        @SuppressWarnings("unchecked")
        List<MeteoTnDto> data = (List<MeteoTnDto>) rawData;
        if (data == null) {
            return null;
        }

        DataMapDto<RecordDtoImpl> map = new DataMapDto<>();
        Date now = new Date();
        Long nowTimestamp = now.getTime();
        //Integer period = env.getProperty(MeteoTnDataConverter.PERIOD_KEY, Integer.class);
        Integer period = converter.getPeriod();

        int countStations = 0;
        int countBranches = 0;
        int countMeasures = 0;

        for (MeteoTnDto dto : data) {

            MeteoStationDto stationDto = dto.getStation();

            //Exclude Stations having endDate<today
            if ( dto.isValid() ) {
    
                String stationId = stationDto.getId();
                List<MeteoTnMeasurementListDto> measurementTypes = dto.getMeasurementTypes();

                //Check if we already treated this station (branch), if not found create the map and the list of records
                DataMapDto<RecordDtoImpl> recordsByStation = map.getBranch().get(stationId);
                if ( recordsByStation == null ) {
                    recordsByStation = new DataMapDto<RecordDtoImpl>();
                    map.getBranch().put(stationId, recordsByStation);
                    List<RecordDtoImpl> dataList = new ArrayList<RecordDtoImpl>();
                    recordsByStation.setData(dataList);
                    countStations++;
                }

                for (MeteoTnMeasurementListDto measurementListDto : measurementTypes) {

                    //String measurementListName = measurementListDto.getName();
                    List<MeteoTnMeasurementDto> measurements = measurementListDto.getMeasurements();

                    for (MeteoTnMeasurementDto measurementDto : measurements) {

                        //Get values from MeasutementDto and convert to SimpleRecordDto
                        String typeName = measurementDto.getTypeName();
                        long timestamp = measurementDto.getDate()!=null ? measurementDto.getDate().getTime() : nowTimestamp;
                        Object value = measurementDto.getValue();

                        SimpleRecordDto record = new SimpleRecordDto();
                        record.setValue(value);
                        record.setTimestamp(timestamp);
                        record.setPeriod(period);

                        //Check if we already treated this type (branch), if not found create the map and the list of records
                        DataMapDto<RecordDtoImpl> recordsByType = recordsByStation.getBranch().get(typeName);
                        if ( recordsByType == null ) {
                            recordsByType = new DataMapDto<RecordDtoImpl>();
                            recordsByStation.getBranch().put(typeName, recordsByType);
                            List<RecordDtoImpl> dataList = new ArrayList<RecordDtoImpl>();
                            recordsByType.setData(dataList);
                            countBranches++;
                        }

                        //Add the measure in the list
                        List<RecordDtoImpl> records = recordsByType.getData();
                        records.add(record);
                        countMeasures++;
                        LOG.debug("ADD  MEASURE:  id="+stationDto.getId()+", typeName="+typeName+"  value="+value);
                    }

                }

            } else {
                LOG.debug("SKIP MEASURES: id="+stationDto.getId());
            }
        }

        LOG.debug("countStations="+countStations+"  countBranches="+countBranches+", countMeasures="+countMeasures);
        LOG.debug("map: "+map);
        LOG.debug("END.mapData");
        return map;
    }

    public <T> DataMapDto<RecordDtoImpl> mapSingleStationData2Bdp(MeteoTnDto data, Date lastSavedRecord) {
        LOG.debug("START.mapSingleStationData2Bdp");
        if (data == null) {
            return null;
        }

        if ( lastSavedRecord != null ) {
            // Remove measurements older than lastSavedRecord in order to have less data to send to the Data Hub
            List<MeteoTnMeasurementListDto> measurementTypes = data.getMeasurementTypes();
            for ( int i=0 ; measurementTypes!=null && i<measurementTypes.size() ; i++ ) {
                MeteoTnMeasurementListDto measurementListDto = measurementTypes.get(i);
                List<MeteoTnMeasurementDto> measurements = measurementListDto.getMeasurements();
                List<MeteoTnMeasurementDto> filteredList = new ArrayList<MeteoTnMeasurementDto>();
                for ( int j=0 ; measurements!=null && j<measurements.size() ; j++ ) {
                    MeteoTnMeasurementDto measurementDto = measurements.get(j);
                    Date date = measurementDto.getDate();
                    if ( date.compareTo(lastSavedRecord) >= 0 ) {
                        filteredList.add(measurementDto);
                    }
                }
                measurementListDto.setMeasurements(filteredList);
            }
        }

        List<MeteoTnDto> list = new ArrayList<MeteoTnDto>();
        list.add(data);
        DataMapDto<RecordDtoImpl> map = mapData(list);

        LOG.debug("END.mapSingleStationData2Bdp");
        return map;
    }

    public StationList mapStations2Bdp(List<MeteoTnDto> data) {
        LOG.debug("START.mapStations2Bdp");
        if (data == null) {
            return null;
        }

        int countStations = 0;
        StationList stations = new StationList();
        for (MeteoTnDto dto : data) {
            MeteoStationDto stationDto = dto.getStation();
            Map<String, String> stationAttributes = dto.getStationAttributes();
            Object endDate = stationAttributes.get("enddate");

            //Exclude Stations having endDate<today
            if ( dto.isValid() ) {
                stations.add(stationDto);
                countStations++;
                LOG.debug("ADD  STATION:  id="+stationDto.getId()+", endDate="+endDate);
            } else {
                LOG.debug("SKIP STATION:  id="+stationDto.getId()+", endDate="+endDate);
            }
        }
        LOG.debug("countStations="+countStations+"  stations: "+stations);
        LOG.debug("END.mapStations2Bdp");
        return stations;
    }

    public List<DataTypeDto> mapDataTypes2Bdp(List<MeteoTnDto> data) {
        LOG.debug("START.mapdataTypes2Bdp");
        if (data == null) {
            return null;
        }

        List<DataTypeDto> dataTypeList = new ArrayList<DataTypeDto>();
        Set<String> dataTypeNames = new HashSet<String>();
        for (MeteoTnDto dto : data) {
            Map<String, DataTypeDto> dataTypes = dto.getDataTypes();
            Set<String> keySet = dataTypes!=null ? dataTypes.keySet() : null;

            if ( keySet!=null && keySet.size()>0 ) {
                for (String key : keySet) {
                    if ( !dataTypeNames.contains(key) ) {
                        DataTypeDto type = dataTypes.get(key);
                        dataTypeList.add(type);
                        dataTypeNames.add(key);
                        LOG.debug("ADD DataTypeDto="+type);
                    }
                }
            }

        }

        LOG.debug("dataTypeList: "+dataTypeList);
        LOG.debug("END.mapdataTypes2Bdp");
        return dataTypeList;
    }

    public List<DataTypeDto> mapDataTypes2Bdp_OLD(DataMapDto<RecordDtoImpl> stationRec) {
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

    public Date getLastSavedRecordForStation(MeteoStationDto station) {
        LOG.debug("START.getLastSavedRecordForStation");
        if (station == null) {
            return null;
        }

        Date lastSavedRecord = null;
        Integer period = converter.getPeriod();
        String stationCode = station.getId();
        Object dateOfLastRecord = super.getDateOfLastRecord(stationCode, null, period);
        if ( dateOfLastRecord instanceof Date ) {
            lastSavedRecord = (Date) dateOfLastRecord;
        }

        LOG.debug("stationCode="+stationCode+"  lastSavedRecord="+lastSavedRecord);
        LOG.debug("END.getLastSavedRecordForStation");
        return lastSavedRecord;
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
