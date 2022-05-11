package it.bz.idm.bdp.augeg4;

import it.bz.idm.bdp.augeg4.fun.convert.MeasurementMapping;
import it.bz.idm.bdp.augeg4.fun.convert.MeasurementMappings;
import it.bz.idm.bdp.dto.DataTypeDto;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

class DataTypeDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(DataTypeDelegate.class.getName());

    private final DataService dataService;

    DataTypeDelegate(DataService dataService) {
        this.dataService = dataService;
    }

    /**
     * Sends to HUB the list of known DataTypes
     *
     * @throws Exception
     */
    void syncDataTypes() {
        LOG.debug("syncDataTypes() called.");
        try {
            dataService.getDataPusherHub().syncDataTypes(getDataTypeList());
        } catch (Exception e) {
            LOG.error("Sync of DataTypes failed: {}.", e.getMessage());
            throw e;
        }
    }

    private List<DataTypeDto> getDataTypeList() {
        return new MeasurementMappings()
                .getMappings()
                .stream()
                .map(this::mapMeasurementMappingToDataTypeDto)
                .collect(Collectors.toList());
    }

    private DataTypeDto mapMeasurementMappingToDataTypeDto(MeasurementMapping mapping) {
        return new DataTypeDto(
                mapping.getDataType(),
                mapping.getUnit(),
                mapping.getDescription(),
                mapping.getRtype()
        );
    }
}
