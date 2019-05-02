package it.bz.idm.bdp.augeg4;

import it.bz.idm.bdp.augeg4.dto.fromauge.AugeG4FromAlgorabDataDto;
import it.bz.idm.bdp.augeg4.dto.toauge.AugeG4LinearizedDataDto;
import it.bz.idm.bdp.augeg4.dto.tohub.AugeG4ToHubDataDto;
import it.bz.idm.bdp.augeg4.face.DataConverterFace;
import it.bz.idm.bdp.augeg4.face.DataLinearizerFace;
import it.bz.idm.bdp.augeg4.face.DataPusherFace;
import it.bz.idm.bdp.augeg4.face.DataServiceFace;
import it.bz.idm.bdp.augeg4.fun.convert.ConverterMappings;
import it.bz.idm.bdp.augeg4.util.FixedQueue;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DataService implements DataServiceFace {

    private static final Logger LOG = LogManager.getLogger(DataService.class.getName());
    private static final int MAX_QUEUE_SIZE = 100;

    private DataPusherFace pusher;

    private DataConverterFace converter;

    private DataLinearizerFace linearizer;

    private ConverterMappings converterMappings = new ConverterMappings();

    private Queue<AugeG4ToHubDataDto> queuedData = new FixedQueue<>(MAX_QUEUE_SIZE);
    private Map<String, StationDto> stationsMap = new ConcurrentHashMap<>();


    public DataService(DataPusherFace pusher, DataLinearizerFace linearizer, DataConverterFace converter) {
        this.pusher = pusher;
        this.linearizer = linearizer;
        this.converter = converter;
    }

    @Override
    public void syncStationsWithHub() throws Exception {
        LOG.info("syncStationsWithHub() called");
        try {
            insertPreviouslySyncedStationsInMap();
            StationList stationList = getStationListFromMap();
            pusher.syncStations(stationList);
        } catch (Exception e) {
            LOG.error("Sync of stationsMap failed: {}.", e.getMessage());
            throw e;
        }
    }

    @Override
    public void syncDataTypesWithHub() throws Exception {
        LOG.info("syncDataTypesWithHub() called.");
        List<DataTypeDto> dataTypeList = getDataTypeList();
        pusher.syncDataTypes(dataTypeList);
    }

    private List<DataTypeDto> getDataTypeList() {
        return converterMappings.getMappings()
                .stream()
                .map(mapping -> {
                    DataTypeDto type = new DataTypeDto();
                    type.setName(mapping.getDataType());
                    type.setPeriod(mapping.getPeriod());
                    type.setUnit(mapping.getUnit());
                    return type;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void pushData() throws Exception {
        LOG.info("pushData() called.");
        try {
            List<AugeG4ToHubDataDto> data = popDataFromQueue();
            pusher.mapData(data);
            pusher.pushData();
        } catch (Exception e) {
            LOG.error("Processing of failed: {}.", e.getMessage());
            throw e;
        }
    }

    // TODO: Rename
    private List<AugeG4ToHubDataDto> popDataFromQueue() {
        // TODO: When switching to FixedQueue, check if the code below still applies
        List<AugeG4ToHubDataDto> list = new ArrayList<>(queuedData);
        queuedData.clear();
        return list;
    }

    @Override
    public void addDataFromAlgorab(List<AugeG4FromAlgorabDataDto> fromAlgorab) {
        LOG.info("addDataFromAlgorab() called.");
        List<AugeG4LinearizedDataDto> linearized = linearizer.linearize(fromAlgorab);
        // TODO: Send back to algorab
        List<AugeG4ToHubDataDto> data = converter.convert(linearized);
        insertStationsFromDataInMapIfNew(data);
        queuedData.addAll(data);
    }

    private StationList getStationListFromMap() {
        StationList list = new StationList();
        list.addAll(stationsMap.values());
        return list;
    }

    private void insertStationsFromDataInMapIfNew(List<AugeG4ToHubDataDto> data) {
        data.forEach(this::insertStationFromDataInMapIfNew);
    }

    private void insertStationFromDataInMapIfNew(AugeG4ToHubDataDto dto) {
        String id = dto.getStation();
        if (!stationsMap.containsKey(id)) {
            insertStationFromDataInMap(dto);
        }
    }

    private void insertStationFromDataInMap(AugeG4ToHubDataDto dto) {
        StationDto station = new StationDto();
        station.setId(dto.getStation());
        // TODO: Set name, type and origin
        station.setName("Non-unique name for ID " + dto.getStation());
        station.setStationType("Teststation");
        station.setOrigin("?");
        stationsMap.put(dto.getStation(), station);
    }

    private void insertPreviouslySyncedStationsInMap() {
        StationList stations = pusher.getSyncedStations();
        for (StationDto station : stations) {
            insertStationInMapIfNew(station);
        }
    }

    private void insertStationInMapIfNew(StationDto station) {
        if (!stationsMap.containsKey(station)) {
            stationsMap.put(station.getId(), station);
        }
    }


}
