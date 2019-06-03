package it.bz.idm.bdp.augeg4;

import it.bz.idm.bdp.augeg4.dto.tohub.AugeG4ProcessedDataToHubDto;
import it.bz.idm.bdp.augeg4.dto.tohub.StationId;
import it.bz.idm.bdp.augeg4.fun.convert.tohub.StationMapping;
import it.bz.idm.bdp.augeg4.fun.convert.tohub.StationMappings;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static it.bz.idm.bdp.augeg4.fun.convert.tohub.DataConverterHub.PREFIX;

class StationsDelegate {

    private static final Logger LOG = LogManager.getLogger(StationsDelegate.class.getName());

    private final DataService dataService;
    private Map<StationId, StationDto> stationsMap = new ConcurrentHashMap<>();
    private final StationMappings stationMappings = new StationMappings();


    StationsDelegate(DataService dataService) {
        this.dataService = dataService;
    }


    void loadPreviouslySyncedStations() throws Exception {
        LOG.info("loadPreviouslySyncedStations() called");
        try {
            StationList stations = dataService.getDataPusherHub().getSyncedStations();
            stations.forEach(this::insertStationInMapIfNew);
        } catch (Exception e) {
            LOG.error("Load of previously synced stations failed: {}.", e.getMessage());
            throw e;
        }
    }


    private void insertStationInMapIfNew(StationDto station) {
        Optional<StationId> stationIdContainer = StationId.fromValue(station.getId(), PREFIX);
        if(!stationIdContainer.isPresent()) {
            LOG.warn("insertStationInMapIfNew() called with a StationDto that uses a different prefix");
            return;
        }
        StationId stationId = stationIdContainer.get();
        if (!stationsMap.containsKey(stationId)) {
            stationsMap.put(stationId, station);
        }
    }


    void syncStationsWithHub() {
        LOG.info("syncStations() called");
        try {
            StationList stationList = dequeueStations();
            dataService.getDataPusherHub().syncStations(stationList);
        } catch (Exception e) {
            LOG.error("Sync of stations failed: {}.", e.getMessage());
            throw e;
        }
    }


    private StationList dequeueStations() {
        Map<StationId, StationDto> oldStationsMap = this.stationsMap;
        this.stationsMap = new ConcurrentHashMap<>();
        return new StationList(oldStationsMap.values());
    }


    void prepareStationsForHub(List<AugeG4ProcessedDataToHubDto> data) {
        LOG.info("prepareStationsForHub() called");
        data.forEach(this::insertStationFromConvertedData);
    }


    private void insertStationFromConvertedData(AugeG4ProcessedDataToHubDto dto) {
        StationId id = dto.getStationId();
        if (!stationsMap.containsKey(id)) {
            insertStationFromConvertedData(dto, id);
        }
    }


    private void insertStationFromConvertedData(AugeG4ProcessedDataToHubDto dto, StationId id) {
        Optional<StationDto> station = createStationDtoFromConvertedData(dto);
        station.ifPresent(stationDto -> stationsMap.put(id, stationDto));
    }


    private Optional<StationDto> createStationDtoFromConvertedData(AugeG4ProcessedDataToHubDto dto) {
        return stationMappings.getMapping(dto.getStationId().getControlUnitId())
                .map(mapping -> mapStationMappingToStationDto(dto, mapping));
    }


    private StationDto mapStationMappingToStationDto(AugeG4ProcessedDataToHubDto dto, StationMapping stationMapping) {
        StationDto station = new StationDto(
                dto.getStationId().getValue(),
                stationMapping.getName(),
                stationMapping.getLatitude(),
                stationMapping.getLongitude()
        );
        station.setOrigin(dataService.getDataPusherHub().getOrigin());
        station.setStationType(dataService.getDataPusherHub().getStationType());
        return station;
    }

    public int getStationsCount() {
        return stationsMap.size();
    }
}
