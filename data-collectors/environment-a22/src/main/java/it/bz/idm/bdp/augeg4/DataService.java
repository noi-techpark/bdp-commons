package it.bz.idm.bdp.augeg4;

import it.bz.idm.bdp.augeg4.dto.fromauge.AugeG4FromAlgorabDataDto;
import it.bz.idm.bdp.augeg4.dto.toauge.AugeG4LinearizedDataDto;
import it.bz.idm.bdp.augeg4.dto.tohub.AugeG4ToHubDataDto;
import it.bz.idm.bdp.augeg4.dto.tohub.StationId;
import it.bz.idm.bdp.augeg4.face.DataConverterFace;
import it.bz.idm.bdp.augeg4.face.DataLinearizerFace;
import it.bz.idm.bdp.augeg4.face.DataPusherFace;
import it.bz.idm.bdp.augeg4.face.DataServiceFace;
import it.bz.idm.bdp.augeg4.fun.convert.ConverterMapping;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DataService implements DataServiceFace {

    private static final Logger LOG = LogManager.getLogger(DataService.class.getName());

    private static final int MAX_QUEUE_SIZE = 100;

    private final DataPusherFace pusher;

    private final DataConverterFace converter;

    private final DataLinearizerFace linearizer;

    private final ConverterMappings converterMappings = new ConverterMappings();

    private final FixedQueue<AugeG4ToHubDataDto> queuedData = new FixedQueue<>(MAX_QUEUE_SIZE);
    private final Map<StationId, StationDto> stationsMap = new ConcurrentHashMap<>();


    public DataService(DataPusherFace pusher, DataLinearizerFace linearizer, DataConverterFace converter) {
        this.pusher = pusher;
        this.linearizer = linearizer;
        this.converter = converter;
    }

    @Override
    public void loadPreviouslySyncedStations() throws Exception {
        LOG.info("loadPreviouslySyncedStations() called");
        try {
            StationList stations = pusher.getSyncedStations();
            stations.forEach(this::insertStationInMapIfNew);
        } catch (Exception e) {
            LOG.error("Load of previously synced stations failed: {}.", e.getMessage());
            throw e;
        }
    }

    private void insertStationInMapIfNew(StationDto station) {
        StationId stationId = new StationId(station.getId());
        if (!stationsMap.containsKey(stationId)) {
            stationsMap.put(stationId, station);
        }
    }

    /**
     * Loads from HUB previously synced stations and then sends stations in the stationsMap to the HUB
     *
     * @throws Exception
     */
    @Override
    public void syncStationsWithHub() throws Exception {
        LOG.info("syncStationsWithHub() called");
        try {
            pusher.syncStations(getStationListFromMap());
        } catch (Exception e) {
            LOG.error("Sync of stations failed: {}.", e.getMessage());
            throw e;
        }
    }

    private StationList getStationListFromMap() {
        return new StationList(stationsMap.values());
    }

    /**
     * Sends to HUB the list of known DataTypes
     *
     * @throws Exception
     */
    @Override
    public void syncDataTypesWithHub() throws Exception {
        LOG.info("syncDataTypesWithHub() called.");
        try {
            pusher.syncDataTypes(getDataTypeList());
        } catch (Exception e) {
            LOG.error("Sync of DataTypes failed: {}.", e.getMessage());
            throw e;
        }
    }

    private List<DataTypeDto> getDataTypeList() {
        return converterMappings.getMappings()
                .stream()
                .map(this::mapConverterMappingToDataTypeDto)
                .collect(Collectors.toList());
    }

    private DataTypeDto mapConverterMappingToDataTypeDto(ConverterMapping mapping) {
        return new DataTypeDto(
                mapping.getDataType(),
                mapping.getUnit(),
                mapping.getDescription(),
                mapping.getRtype()
        );
    }

    /**
     * Dequeues converted data and sends it to the HUB
     *
     * @throws Exception
     */
    @Override
    public void pushData() throws Exception {
        LOG.info("pushData() called.");
        try {
            List<AugeG4ToHubDataDto> data = dequeueConvertedData();
            pusher.mapData(data);
            pusher.pushData();
        } catch (Exception e) {
            LOG.error("Push of failed: {}.", e.getMessage());
            throw e;
        }
    }

    private List<AugeG4ToHubDataDto> dequeueConvertedData() {
        List<AugeG4ToHubDataDto> list = new ArrayList<>();
        queuedData.drainTo(list);
        return list;
    }

    /**
     * - linearizes the raw data from Algorab
     * - sends the linearized data back to Algorab
     * - converts the data
     * - prepares the map of stations to be later synced with the HUB
     * - prepares the converted data to be later sent to the HUB
     *
     * @param rawFromAlgorab
     */
    @Override
    public void addDataFromAlgorab(List<AugeG4FromAlgorabDataDto> rawFromAlgorab) {
        LOG.info("addDataFromAlgorab() called.");
        List<AugeG4LinearizedDataDto> linearized = linearizer.linearize(rawFromAlgorab);
        sendLinearizedDataToAlgorab(linearized);
        List<AugeG4ToHubDataDto> converted = converter.convert(linearized);
        prepareStationsForHub(converted);
        prepareConvertedDataForHub(converted);
    }

    private void sendLinearizedDataToAlgorab(List<AugeG4LinearizedDataDto> linearized) {
        LOG.info("sendLinearizedDataToAlgorab() called.");
        // TODO: Implement
    }

    private void prepareStationsForHub(List<AugeG4ToHubDataDto> data) {
        data.forEach(measurementsByStation -> {
            StationDto station = getOrInsertStationFromConvertedData(measurementsByStation);
            updateStationMetadataFromConvertedData(station, measurementsByStation);
        });
    }

    private StationDto getOrInsertStationFromConvertedData(AugeG4ToHubDataDto dto) {
        StationId id = dto.getStationId();
        if (!stationsMap.containsKey(id)) {
            stationsMap.put(id, createStationDtoFromConvertedData(dto));
        }
        return stationsMap.get(id);
    }

    private StationDto createStationDtoFromConvertedData(AugeG4ToHubDataDto dto) {
        StationDto station = new StationDto();
        station.setId(dto.getStationId().getValue());
        station.setName(dto.getStationId().getValue());
        station.setStationType(pusher.getStationType());
        station.setOrigin(pusher.getOrigin());
        return station;
    }

    private void updateStationMetadataFromConvertedData(StationDto station, AugeG4ToHubDataDto data) {
        // TODO: update metadata. Blocked until we have instructions of how to use the metadatas of StationDto
    }

    private void prepareConvertedDataForHub(List<AugeG4ToHubDataDto> data) {
        queuedData.addAll(data);
    }

}
