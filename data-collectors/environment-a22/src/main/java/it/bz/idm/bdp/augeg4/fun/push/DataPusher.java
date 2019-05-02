package it.bz.idm.bdp.augeg4.fun.push;

import it.bz.idm.bdp.augeg4.dto.tohub.AugeG4ToHubDataDto;
import it.bz.idm.bdp.augeg4.face.DataPusherFace;
import it.bz.idm.bdp.augeg4.face.DataPusherMapperFace;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;
import it.bz.idm.bdp.json.JSONPusher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataPusher extends JSONPusher implements DataPusherFace {

    private static final Logger LOG = LogManager.getLogger(DataPusher.class.getName());

    private final DataPusherMapperFace mapper;

    private DataMapDto<RecordDtoImpl> rootMap;

    public DataPusher(@Value("${station.period}") int period) {
        mapper = new DataPusherMapper(period);
    }

    @Override
    public void pushData() {
        LOG.info("PUSH DATA");
        pushData(this.integreenTypology, rootMap);
    }

    /**
     * Define your station type, which must be present in bdp-core/dal and derived from "Station"
     */
    @Override
    public String initIntegreenTypology() {
        // This method is called by the DataPusher constructor, so we don't have access to the Environment of Spring.
        // TODO: Decide if keep the station type in the code or if we want to use environment variables
        return "Teststation";
    }

    @Override
    public <T> DataMapDto<RecordDtoImpl> mapData(T data) {
        try {
            @SuppressWarnings("unchecked")
            List<AugeG4ToHubDataDto> measurementsByStation = ((List<AugeG4ToHubDataDto>) data);
            return rootMap = mapper.map(measurementsByStation);
        } catch (ClassCastException ex) {
            throw new IllegalArgumentException("data must be of type List<AugeG4ToHubDataDto>", ex);
        }
    }

    @Override
    public StationList getSyncedStations() {
        List<StationDto> stations = fetchStations("Teststations", "?");
        return new StationList(stations);
    }
}
