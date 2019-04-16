package it.bz.idm.bdp.augeg4.fun.convert;

import it.bz.idm.bdp.augeg4.fun.push.DataPusher;
import it.bz.idm.bdp.augeg4.dto.toauge.AugeG4LinearizedDataDto;
import it.bz.idm.bdp.augeg4.dto.toauge.LinearResVal;
import it.bz.idm.bdp.augeg4.dto.tohub.AugeG4ToHubDataDto;
import it.bz.idm.bdp.augeg4.dto.tohub.Measurement;
import it.bz.idm.bdp.augeg4.face.DataConverterFace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DataConverter implements DataConverterFace {

    private static final Logger LOG = LogManager.getLogger(DataPusher.class.getName());

    private final String prefix;

    private final Map<Integer, String> dataTypeByLinearizedId = new HashMap<>();

    /**
     * @param prefix Prefix that will be used to create the station identifier for the hub starting from the control
     *               unit id from Algorab
     */
    public DataConverter(@Value("${station.prefix}") String prefix) {
        this.prefix = prefix;
        initDataTypeByLinearizedIdMap();
    }

    private void initDataTypeByLinearizedIdMap () {
        dataTypeByLinearizedId.put(101, "temperature");
        dataTypeByLinearizedId.put(102, "pressure");
        // TODO: Define or load all the data types
    }

    @Override
    public List<AugeG4ToHubDataDto> convert(List<AugeG4LinearizedDataDto> data) {
        return data
                .stream()
                .map(this::convertDto)
                .collect(Collectors.toList());
    }

    private AugeG4ToHubDataDto convertDto(AugeG4LinearizedDataDto linearized) {
        String controlUnitId = linearized.getControlUnitId();
        Date acquisition = linearized.getDateTimeAcquisition();
        List<LinearResVal> resources = linearized.getResVal();
        List<Measurement> measurements = convertResources(resources);
        String station = convertControlUnitId(controlUnitId);
        return new AugeG4ToHubDataDto(station, acquisition, measurements);
    }

    private String convertControlUnitId(String controlUnitId) {
        return prefix + controlUnitId;
    }

    private List<Measurement> convertResources(List<LinearResVal> resources) {
        return resources
                .stream()
                .map(this::convertResource)
                .collect(Collectors.toList());
    }

    private Measurement convertResource(LinearResVal resource) {
        int linearizedId = resource.getId();
        double value = resource.getValue();
        String dataType = convertLinearizedId(linearizedId);
        return new Measurement(dataType, value);
    }

    private String convertLinearizedId(int linearizedId) {
        if (!dataTypeByLinearizedId.containsKey(linearizedId)) {
            throw new IllegalArgumentException("Unknown linearized id " + linearizedId);
        }
        return dataTypeByLinearizedId.get(linearizedId);
    }

}
