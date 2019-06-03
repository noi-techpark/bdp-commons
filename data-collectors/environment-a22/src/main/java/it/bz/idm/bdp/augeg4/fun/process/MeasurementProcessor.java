package it.bz.idm.bdp.augeg4.fun.process;

import it.bz.idm.bdp.augeg4.dto.AugeG4RawData;
import it.bz.idm.bdp.augeg4.dto.MeasurementId;
import it.bz.idm.bdp.augeg4.dto.ProcessedMeasurement;
import it.bz.idm.bdp.augeg4.dto.RawMeasurement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class MeasurementProcessor {

    private static final Logger LOG = LogManager.getLogger(MeasurementProcessor.class.getName());

    // TODO: Where do we keep these hard coded ids?
    public static final MeasurementId MEASUREMENT_ID_O3 = new MeasurementId(8);
    public static final MeasurementId MEASUREMENT_ID_TEMPERATURA = new MeasurementId(2);

    private final MeasurementProcessorParameters measurementProcessorParameters = new MeasurementProcessorParameters();

    public Optional<ProcessedMeasurement> process(AugeG4RawData rawData, RawMeasurement rawMeasurement) {
        if (!isToProcess(rawData, rawMeasurement)) {
            return processMeasurementNotToProcess(rawMeasurement);
        }
        return processMeasurementToProcess(rawData, rawMeasurement);
    }

    private boolean isToProcess(AugeG4RawData rawData, RawMeasurement rawMeasurement) {
        return measurementProcessorParameters.hasMeasurementParameters(
                rawData.getControlUnitId(),
                rawMeasurement.getId()
        );
    }

    private Optional<ProcessedMeasurement> processMeasurementNotToProcess(RawMeasurement rawMeasurement) {
        return Optional.of(new ProcessedMeasurement(
                rawMeasurement.getId(),
                rawMeasurement.getValue(),
                rawMeasurement.getValue()
        ));
    }

    private Optional<ProcessedMeasurement> processMeasurementToProcess(AugeG4RawData rawData, RawMeasurement rawMeasurement) {
        Optional<Double> processedValue = applyFunction(rawData, rawMeasurement);
        return processedValue.map(value -> new ProcessedMeasurement(
                rawMeasurement.getId(),
                rawMeasurement.getValue(),
                value
        ));
    }

    private Optional<Double> applyFunction(AugeG4RawData rawData, RawMeasurement rawMeasurement) {
        Optional<Double> O3 = getMeasurementFromRawData(rawData, MEASUREMENT_ID_O3);
        Optional<Double> temperature = getMeasurementFromRawData(rawData, MEASUREMENT_ID_TEMPERATURA);
        if (!O3.isPresent() || !temperature.isPresent()) {
            return Optional.empty();
        }
        Optional<MeasurementParameters> parametersContainer = measurementProcessorParameters.getMeasurementParameters(
                rawData.getControlUnitId(),
                rawMeasurement.getId()
        );
        if(!parametersContainer.isPresent()) {
            return Optional.empty();
        }
        MeasurementParameters parameters = parametersContainer.get();
        return Optional.of(applyFunction(
                rawMeasurement.getValue(),
                parameters.getA(),
                parameters.getB(),
                parameters.getC(),
                parameters.getD(),
                parameters.getE(),
                parameters.getF(),
                O3.get(),
                temperature.get(),
                1 // TODO: Replace with Rad
        ));
    }

    private double applyFunction(double x, double a, double b, double c, double d, double e, double f, double O3, double T_int, double Rad) {
        return a * Math.pow(x, 2) + b * x + c * O3 + d * T_int + e * Rad + f;
    }

    private Optional<Double> getMeasurementFromRawData(AugeG4RawData rawData, MeasurementId measurementId) {
        for (RawMeasurement measurement : rawData.getMeasurements()) {
            if (measurement.getId().equals(measurementId)) {
                return Optional.of(measurement.getValue());
            }
        }
        LOG.warn("getMeasurementFromRawData() measurement id {} not found in raw data {}", measurementId, rawData);
        return Optional.empty();
    }
}
