package it.bz.idm.bdp.augeg4.fun.convert.toauge;

import it.bz.idm.bdp.augeg4.dto.AugeG4ProcessedData;
import it.bz.idm.bdp.augeg4.dto.MeasurementId;
import it.bz.idm.bdp.augeg4.dto.ProcessedMeasurement;
import it.bz.idm.bdp.augeg4.dto.toauge.AugeG4ProcessedDataToAugeDto;
import it.bz.idm.bdp.augeg4.dto.toauge.ProcessedResValToAuge;
import it.bz.idm.bdp.augeg4.face.DataConverterAugeFace;
import it.bz.idm.bdp.augeg4.fun.convert.MeasurementMapping;
import it.bz.idm.bdp.augeg4.fun.convert.MeasurementMappings;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DataConverterAuge implements DataConverterAugeFace {

    private static final Logger LOG = LoggerFactory.getLogger(DataConverterAuge.class.getName());

    private final MeasurementMappings measurementMappings = new MeasurementMappings();

    @Override
    public List<AugeG4ProcessedDataToAugeDto> convert(List<AugeG4ProcessedData> processedData) {
        return processedData
                .stream()
                .map(this::convertDto)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Optional<AugeG4ProcessedDataToAugeDto> convertDto(AugeG4ProcessedData processedData) {
        List<ProcessedResValToAuge> convertedResVal = convertMeasurements(processedData.getMeasurements());
        if (convertedResVal.isEmpty()) {
            LOG.warn("convertDto() convertedResVal.isEmpty(): check measurementMappings.csv for missing id: {}",
                    processedData.getControlUnitId());
            return Optional.empty();
        }
        return Optional.of(new AugeG4ProcessedDataToAugeDto(
                processedData.getDateTimeAcquisition(),
                processedData.getDateTimeProcessing(),
                processedData.getControlUnitId(),
                convertedResVal));
    }

    private List<ProcessedResValToAuge> convertMeasurements(List<ProcessedMeasurement> measurements) {
        return measurements
                .stream()
                .map(this::convertMeasurement)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Optional<ProcessedResValToAuge> convertMeasurement(ProcessedMeasurement processedMeasurement) {
        return mapMeasurementIdToLinearizedId(processedMeasurement.getId())
                .map(linearizedId -> processedMeasurement.getProcessedValue() != null ? new ProcessedResValToAuge(
                        linearizedId,
                        processedMeasurement.getProcessedValue()) : null)
                .filter(x -> x != null);
    }

    private Optional<Integer> mapMeasurementIdToLinearizedId(MeasurementId measurementId) {
        return measurementMappings.getMapping(measurementId)
                .map(MeasurementMapping::getProcessedId);
    }

}
