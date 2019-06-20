package it.bz.idm.bdp.augeg4.fun.process;

import it.bz.idm.bdp.augeg4.dto.AugeG4ProcessedData;
import it.bz.idm.bdp.augeg4.dto.AugeG4RawData;
import it.bz.idm.bdp.augeg4.dto.ProcessedMeasurement;
import it.bz.idm.bdp.augeg4.dto.RawMeasurement;
import it.bz.idm.bdp.augeg4.face.DataProcessorFace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class DataProcessor implements DataProcessorFace {

    private static final Logger LOG = LogManager.getLogger(DataProcessor.class.getName());

    private final MeasurementProcessor measurementProcessor = new MeasurementProcessor();

    @Override
    public List<AugeG4ProcessedData> process(List<AugeG4RawData> data) {
        return data.stream()
                .map(this::process)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }


    public Optional<AugeG4ProcessedData> process(AugeG4RawData rawData) {
        List<ProcessedMeasurement> measurements = processMeasurements(rawData);
        if(measurements.isEmpty()) {
            LOG.warn("process() measurements.isEmpty(): check processorParameters.csv for missing control unit id and Inquinante");
            return Optional.empty();
        }
        return Optional.of(new AugeG4ProcessedData(
                rawData.getControlUnitId(),
                rawData.getDateTimeAcquisition(),
                getProcessingDate(),
                measurements
        ));
    }

    private Date getProcessingDate() {
        return new Date();
    }

    private List<ProcessedMeasurement> processMeasurements(AugeG4RawData rawData) {
        return rawData.getMeasurements()
                .stream()
                .map(measurement -> measurementProcessor.process(rawData, measurement))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

}
