package it.bz.idm.bdp.dcemobilityh2;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import it.bz.idm.bdp.dcemobilityh2.dto.HydrogenDto;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationList;
import it.bz.idm.bdp.dto.emobility.ChargingPointsDtoV2;
import it.bz.idm.bdp.dto.emobility.EchargingPlugDto;
import it.bz.idm.bdp.dto.emobility.EchargingStationDto;
import it.bz.idm.bdp.json.JSONPusher;

@Service
public class HydrogenDataPusher extends JSONPusher {

    private static final Logger LOG = LogManager.getLogger(HydrogenDataPusher.class.getName());

    @Autowired
    private Environment env;

    public HydrogenDataPusher() {
        LOG.debug("EXECUTE.constructor.");
    }

    @Override
    public String initIntegreenTypology() {
        String stationType = "EChargingStation";
        LOG.debug("EXECUTE.initIntegreenTypology. Station-type=" + stationType);
        return stationType;
    }

    @Override
    public Object syncStations(StationList data) {
        LOG.info("START.syncStations");
        Object stations = super.syncStations(data);
        LOG.info("END.syncStations");
        return stations;
    }

    @Override
    public Object syncDataTypes(List<DataTypeDto> data) {
        LOG.info("START.syncDataTypes");
        Object dataTypes = super.syncDataTypes(data);
        LOG.info("END.syncDataTypes");
        return dataTypes;
    }

    @Override
    public Object pushData(DataMapDto<? extends RecordDtoImpl> dto) {
        LOG.info("START.pushData");
        Object retval = super.pushData(dto);
        LOG.info("END.pushData");
        return retval;
    }

    @Override
    public Object pushData(String datasourceName, DataMapDto<? extends RecordDtoImpl> dto) {
        LOG.info("START.pushData");
        Object retval = super.pushData(datasourceName, dto);
        LOG.info("END.pushData");
        return retval;
    }

    @Override
    public <T> DataMapDto<RecordDtoImpl> mapData(T rawData) {
        LOG.info("START.mapData");
        @SuppressWarnings("unchecked")
        List<HydrogenDto> data = (List<HydrogenDto>) rawData;
        if (data == null) {
            return null;
        }

        DataMapDto<RecordDtoImpl> map = new DataMapDto<>();
        Date now = new Date();
        String availableValue = env.getRequiredProperty(HydrogenDataConverter.PLUG_AVAILABLE_KEY);
        Integer period = env.getProperty(HydrogenDataConverter.PERIOD_KEY, Integer.class);

        for(HydrogenDto dto: data){
            DataMapDto<RecordDtoImpl> recordsByType = new DataMapDto<RecordDtoImpl>();
            Integer availableStations=0;
            EchargingStationDto stationDto = dto.getStation();
            //List<EchargingPlugDto> plugList = dto.getPlugList();
            List<ChargingPointsDtoV2> pointList = dto.getPointList();
            for (ChargingPointsDtoV2 point : pointList){
                List<RecordDtoImpl> records = new ArrayList<RecordDtoImpl>();
                if (availableValue.equals(point.getState())) {
                    availableStations++;
                }
                SimpleRecordDto record = new SimpleRecordDto(now.getTime(), availableStations.doubleValue());
                record.setPeriod(period);
                records.add(record);
                DataMapDto<RecordDtoImpl> dataSet = new DataMapDto<>(records);
                recordsByType.getBranch().put(DataTypeDto.NUMBER_AVAILABE, dataSet);
            }
            map.getBranch().put(stationDto.getId(), recordsByType);
        }
        LOG.info("END.mapData");
        return map;
    }

    public DataMapDto<RecordDtoImpl> mapPlugData2Bdp(List<HydrogenDto> data) {
        if (data == null) {
            return null;
        }

        DataMapDto<RecordDtoImpl> map = new DataMapDto<>();
        Date now = new Date();
        String availableValue = env.getRequiredProperty(HydrogenDataConverter.PLUG_AVAILABLE_KEY);
        Integer period = env.getProperty(HydrogenDataConverter.PERIOD_KEY, Integer.class);

        for(HydrogenDto dto: data) {
            //EchargingStationDto stationDto = dto.getStation();
            //List<EchargingPlugDto> plugList = dto.getPlugList();
            List<ChargingPointsDtoV2> pointList = dto.getPointList();
            for (ChargingPointsDtoV2 point : pointList){
                DataMapDto<RecordDtoImpl> recordsByType = new DataMapDto<RecordDtoImpl>();
                List<RecordDtoImpl> records = new ArrayList<RecordDtoImpl>();
                SimpleRecordDto record = new SimpleRecordDto();
                record.setTimestamp(now.getTime());
                record.setValue(availableValue.equals(point.getState()) ? 1. : 0);
                record.setPeriod(period);
                records.add(record);
                recordsByType.getBranch().put("echarging-plug-status", new DataMapDto<RecordDtoImpl>(records));
                //String id = dto.getId()+"-"+point.getOutlets().get(0).getId();
                String id = point.getId();
                map.getBranch().put(id, recordsByType);
            }
        }
        return map;
    }

    public StationList mapStations2Bdp(List<HydrogenDto> data) {
        if (data == null) {
            return null;
        }
        StationList stations = new StationList();
        for (HydrogenDto dto : data) {
            EchargingStationDto stationDto = dto.getStation();
            stations.add(stationDto);
        }
        return stations;
    }

    public StationList mapPlugs2Bdp(List<HydrogenDto> data) {
        if (data == null) {
            return null;
        }
        StationList plugs = new StationList();
        for (HydrogenDto dto : data) {
            List<EchargingPlugDto> plugList = dto.getPlugList();
            plugs.addAll(plugList);
        }
        return plugs;
    }

    @Override
    public String toString() {
        String str1 = "http://" + config.getString(HOST_KEY)+":"+config.getString(PORT_KEY)+config.getString("json_endpoint");
        String str2 = 
                "integreenTypology=" + this.integreenTypology	+ "  " +
                        "DEFAULT_HOST="      + DEFAULT_HOST		+ "  " +
                        "DEFAULT_PORT="      + DEFAULT_PORT		+ "  " +
                        "DEFAULT_ENDPOINT="  + DEFAULT_ENDPOINT	+ "  " +
                        "";
        return str2 + " ---> " + str1;
    }
}
