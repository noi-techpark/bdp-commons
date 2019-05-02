package it.bz.idm.bdp.augeg4.fun.convert;

import it.bz.idm.bdp.augeg4.dto.toauge.AugeG4LinearizedDataDto;
import it.bz.idm.bdp.augeg4.dto.toauge.LinearResVal;
import it.bz.idm.bdp.augeg4.dto.tohub.AugeG4ToHubDataDto;
import it.bz.idm.bdp.augeg4.dto.tohub.Measurement;
import it.bz.idm.bdp.augeg4.face.DataConverterFace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DataConverter implements DataConverterFace {

    private static final Logger LOG = LogManager.getLogger(DataConverter.class.getName());

    private final String prefix;

    private final ConverterMappings converterMappings = new ConverterMappings();

    /**
     * @param prefix Prefix that will be used to create the station identifier for the hub starting from the control
     *               unit id from Algorab
     */
    public DataConverter(@Value("${station.prefix}") String prefix) {
        this.prefix = prefix;
    }

    @Override
    public List<AugeG4ToHubDataDto> convert(List<AugeG4LinearizedDataDto> data) {
        return data
                .stream()
                .map(this::convertDto)
                .collect(Collectors.toList());
    }

    private AugeG4ToHubDataDto convertDto(AugeG4LinearizedDataDto linearized) {
        return new AugeG4ToHubDataDto(
                convertControlUnitId(
                        linearized.getControlUnitId()),
                        linearized.getDateTimeAcquisition(),
                        convertResources(linearized.getResVal()));
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
        return new Measurement(
                    convertLinearizedId(resource.getId()),
                    resource.getValue());
    }

    private String convertLinearizedId(int linearizedId) {
        return converterMappings.mapLinearizedIdToDataType(linearizedId);
    }

}
