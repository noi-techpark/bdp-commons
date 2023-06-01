// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.augeg4.fun.convert.tohub;

import it.bz.idm.bdp.augeg4.dto.AugeG4ProcessedData;
import it.bz.idm.bdp.augeg4.dto.MeasurementId;
import it.bz.idm.bdp.augeg4.dto.ProcessedMeasurement;
import it.bz.idm.bdp.augeg4.dto.tohub.AugeG4ProcessedDataToHubDto;
import it.bz.idm.bdp.augeg4.dto.tohub.ProcessedMeasurementToHub;
import it.bz.idm.bdp.augeg4.dto.tohub.StationId;
import it.bz.idm.bdp.augeg4.face.DataConverterHubFace;
import it.bz.idm.bdp.augeg4.fun.convert.MeasurementMapping;
import it.bz.idm.bdp.augeg4.fun.convert.MeasurementMappings;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DataConverterHub implements DataConverterHubFace {

    private static final Logger LOG = LoggerFactory.getLogger(DataConverterHub.class.getName());

    public static final String PREFIX = "AUGEG4_";

    private final MeasurementMappings measurementMappings = new MeasurementMappings();

    @Override
    public List<AugeG4ProcessedDataToHubDto> convert(List<AugeG4ProcessedData> data) {
        return data
                .stream()
                .map(this::convertDto)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Optional<AugeG4ProcessedDataToHubDto> convertDto(AugeG4ProcessedData processedData) {
        List<ProcessedMeasurementToHub> convertedMeasurements = convertMeasurements(processedData.getMeasurements());
        if (convertedMeasurements.isEmpty()) {
            LOG.warn("convertDto() convertedMeasurements.isEmpty(): check measurementMappings.csv for missing id",
                    processedData.getControlUnitId());
            return Optional.empty();
        }
        return Optional.of(new AugeG4ProcessedDataToHubDto(
                convertStationId(processedData.getControlUnitId()),
                processedData.getDateTimeAcquisition(),
                convertedMeasurements));
    }

    private StationId convertStationId(String controlUnitId) {
        return new StationId(PREFIX, controlUnitId);
    }

    private List<ProcessedMeasurementToHub> convertMeasurements(List<ProcessedMeasurement> measurements) {
        return measurements
                .stream()
                .map(this::convertMeasurement)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Optional<ProcessedMeasurementToHub> convertMeasurement(ProcessedMeasurement measurement) {
        return mapMeasurementIdToDataType(measurement.getId())
                .map(dataType -> new ProcessedMeasurementToHub(
                        dataType,
                        measurement.getRawValue(),
                        measurement.getProcessedValue()));
    }

    private Optional<String> mapMeasurementIdToDataType(MeasurementId measurementId) {
        return measurementMappings.getMapping(measurementId)
                .map(MeasurementMapping::getDataType);
    }

}
