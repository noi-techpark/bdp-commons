package it.bz.idm.bdp.augeg4.fun.process;

import it.bz.idm.bdp.augeg4.dto.AugeG4RawData;
import it.bz.idm.bdp.augeg4.dto.MeasurementId;
import it.bz.idm.bdp.augeg4.dto.ProcessedMeasurement;
import it.bz.idm.bdp.augeg4.dto.RawMeasurement;
import it.bz.idm.bdp.augeg4.util.math.BigDecimalMath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Optional;

public class MeasurementProcessor {

    private static final Logger LOG = LogManager.getLogger(MeasurementProcessor.class.getName());

    public static final MeasurementId MEASUREMENT_ID_TEMPERATURA = new MeasurementId(2);
    public static final MeasurementId MEASUREMENT_ID_RH = new MeasurementId(3);
    public static final MeasurementId MEASUREMENT_ID_O3 = new MeasurementId(8);
    public static final MeasurementId MEASUREMENT_ID_NO2 = new MeasurementId(14);
    public static final MeasurementId MEASUREMENT_ID_PM10 = new MeasurementId(12);
    public static final int NESSUNA_FORMULA = 0;
    public static final int FORMULA_PER_NO_E_NO2 = 1;
    public static final int FORMULA_PER_PM10 = 2;

    private final MeasurementProcessorParameters measurementProcessorParameters = new MeasurementProcessorParameters();
    private final int RAD = 1;
    private static final MathContext mathContext = new MathContext(10, RoundingMode.HALF_EVEN);


    public Optional<ProcessedMeasurement> process(AugeG4RawData rawData, RawMeasurement rawMeasurement) {
        if (!isToProcess(rawData, rawMeasurement)) {
            return processMeasurementNotToProcess(rawMeasurement);
        }
        return processMeasurementToProcess(rawData, rawMeasurement);
    }

    private boolean isToProcess(AugeG4RawData rawData, RawMeasurement rawMeasurement) {
        return measurementProcessorParameters.hasMeasurementParameters(
                rawData.getControlUnitId(),
                rawMeasurement.getId(),
                ""
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
        Optional<Double> RH = getMeasurementFromRawData(rawData, MEASUREMENT_ID_RH);
        Optional<Double> PM10 = getMeasurementFromRawData(rawData, MEASUREMENT_ID_PM10);
        int formula;
        if (O3.isPresent() && temperature.isPresent()) {
            formula = FORMULA_PER_NO_E_NO2;
        } else if (RH.isPresent() && PM10.isPresent() && temperature.isPresent()) {
            formula = FORMULA_PER_PM10;
            if (temperature.get()>=20.0 && PM10.get()>100.0) formula = NESSUNA_FORMULA;
            if (temperature.get()<20.0 && RH.get()>97.0) formula = NESSUNA_FORMULA;
        } else {
            formula = NESSUNA_FORMULA;
        }
        Optional<MeasurementParameters> parametersContainer= measurementProcessorParameters.getMeasurementParameters(
                rawData.getControlUnitId(),
                rawMeasurement.getId(),
                temperature.get()
            );
        if(!parametersContainer.isPresent()) {
            return Optional.empty();
        }
        MeasurementParameters parameters = parametersContainer.get();
        switch (formula) {
            case 1: return Optional.of(applyFunction(
                    BigDecimal.valueOf(rawMeasurement.getValue()),
                    parameters.getA(),
                    parameters.getB(),
                    parameters.getC(),
                    parameters.getD(),
                    parameters.getE(),
                    parameters.getF(),
                    BigDecimal.valueOf(O3.get()),
                    BigDecimal.valueOf(temperature.get()),
                    BigDecimal.valueOf(RAD)
            ));
            case 2: return Optional.of(applyFunction2(
                    BigDecimal.valueOf(rawMeasurement.getValue()),
                    parameters.getA(),
                    parameters.getB(),
                    parameters.getC(),
                    parameters.getD(),
                    parameters.getE(),
                    parameters.getF(),
                    BigDecimal.valueOf(RH.get()),
                    BigDecimal.valueOf(temperature.get()),
                    BigDecimal.valueOf(RAD)
            ));
            default:
                return Optional.empty();
        }

    }

    public double applyFunction(BigDecimal x,
                                 BigDecimal a,
                                 BigDecimal b,
                                 BigDecimal c,
                                 BigDecimal d,
                                 BigDecimal e,
                                 BigDecimal f,
                                 BigDecimal O3,
                                 BigDecimal T_int,
                                 BigDecimal Rad) {
        BigDecimal uno = a.multiply(x.pow(2, mathContext), mathContext);
        LOG.debug("uno:"+uno);

        BigDecimal due = b.multiply(x, mathContext);
        LOG.debug("due:"+due);

        BigDecimal tre = c.multiply(BigDecimalMath.pow(O3, new BigDecimal("0.1")), mathContext);
        LOG.debug("tre:"+tre);

        BigDecimal quattro = d.multiply(T_int.pow(4), mathContext);
        LOG.debug("quattro:"+quattro);

        BigDecimal cinque = e.multiply(Rad, mathContext);
        LOG.debug("cinque:"+cinque);
        BigDecimal somma = uno
                .add(due, mathContext)
                .add(tre, mathContext)
                .add(quattro, mathContext)
                .add(cinque, mathContext);
        LOG.debug("somma:"+somma);
        return somma.doubleValue();
    }

    public double applyFunction2(BigDecimal x,
                                BigDecimal a,
                                BigDecimal b,
                                BigDecimal c,
                                BigDecimal d,
                                BigDecimal e,
                                BigDecimal f,
                                BigDecimal RH,
                                BigDecimal T_int,
                                BigDecimal Rad) {
        BigDecimal uno = a.multiply(BigDecimalMath.pow(x, new BigDecimal("0.7")), mathContext);
        LOG.debug("uno:"+uno);

        BigDecimal due = b.multiply(BigDecimalMath.pow(RH, new BigDecimal("0.75")), mathContext);
        LOG.debug("due:"+due);

        BigDecimal tre = c.multiply(BigDecimalMath.pow(T_int, new BigDecimal("0.3")), mathContext);
        LOG.debug("tre:"+tre);

        BigDecimal quattro = new BigDecimal(0);
        LOG.debug("quattro:"+quattro);

        BigDecimal cinque = new BigDecimal(0);
        LOG.debug("cinque:"+cinque);
        BigDecimal somma = uno
                .add(due, mathContext)
                .add(tre, mathContext)
                .add(quattro, mathContext)
                .add(cinque, mathContext);
        LOG.debug("somma:"+somma);
        return somma.doubleValue();
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
