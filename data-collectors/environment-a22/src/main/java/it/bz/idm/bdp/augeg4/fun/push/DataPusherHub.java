package it.bz.idm.bdp.augeg4.fun.push;

import java.util.List;

import it.bz.idm.bdp.dto.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import it.bz.idm.bdp.augeg4.dto.tohub.AugeG4ProcessedDataToHubDto;
import it.bz.idm.bdp.augeg4.face.DataPusherHubFace;
import it.bz.idm.bdp.augeg4.face.DataPusherMapperFace;
//import it.bz.idm.bdp.dto.ProvenanceDto;
import it.bz.idm.bdp.json.JSONPusher;

@Service
public class DataPusherHub extends JSONPusher implements DataPusherHubFace {

    private static final String STATION_TYPE = "EnvironmentStation";

    private static final String STATION_ORIGIN = "A22_algorab";

    private static final Logger LOG = LogManager.getLogger(DataPusherHub.class.getName());

    private final DataPusherMapperFace mapper;

    private DataMapDto<RecordDtoImpl> rootMap;

    @Autowired
    private Environment env;

    public DataPusherHub(@Value("${station.period}") int period) {
        mapper = new DataPusherMapper(period);
    }

    @Override
    public Object syncDataTypes(List<DataTypeDto> dataTypeDtoList) {
        List<DataTypeDto> mappedDataTypeDtoList = mapper.mapDataTypes(dataTypeDtoList);
        return super.syncDataTypes(mappedDataTypeDtoList);
    }

    @Override
    public void pushData() {
        LOG.info("PUSH DATA");
        if (rootMap.getBranch().keySet().isEmpty()) {
            LOG.warn("pushData() rootMap.getBranch().keySet().isEmpty()");
        } else {
            // FIXME integreenTypology substitute with real value
            LOG.debug("PUSH Typology: ["+this.integreenTypology+"]");
            super.pushData(this.integreenTypology, rootMap);
        }
    }

    /**
     * TODO: Define your station type, which must be present in bdp-core/dal and derived from "Station"
     */
    @Override
    public String initIntegreenTypology() {
        return getStationType();
    }

    @Override
    public String getStationType() {
        return STATION_TYPE;
    }

    @Override
    public String getOrigin() {
        return STATION_ORIGIN;
    }

    @Override
    public <T> DataMapDto<RecordDtoImpl> mapData(T data) {
        try {
            @SuppressWarnings("unchecked")
            List<AugeG4ProcessedDataToHubDto> measurementsByStation = ((List<AugeG4ProcessedDataToHubDto>) data);
            return rootMap = mapper.mapData(measurementsByStation);
        } catch (ClassCastException ex) {
            throw new IllegalArgumentException("data must be of type List<AugeG4ProcessedDataToHubDto>", ex);
        }
    }

    @Override
    public StationList getSyncedStations() {
        List<StationDto> stations = super.fetchStations(getStationType(), getOrigin());
        return new StationList(stations);
    }

    protected DataMapDto<RecordDtoImpl>  getRootMap() {
        return this.rootMap;
    }

	@Override
	public ProvenanceDto defineProvenance() {
		return new ProvenanceDto(null, env.getProperty("provenance.name"), env.getProperty("provenance.version"),  env.getProperty("origin"));
	}
}
