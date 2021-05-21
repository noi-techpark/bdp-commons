package it.bz.idm.bdp.augeg4.fun.process;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.bz.idm.bdp.augeg4.dto.AugeG4RawData;
import it.bz.idm.bdp.augeg4.dto.MeasurementId;
import it.bz.idm.bdp.augeg4.dto.ProcessedMeasurement;
import it.bz.idm.bdp.augeg4.dto.RawMeasurement;
import it.bz.idm.bdp.augeg4.util.math.BigDecimalMath;

public class MeasurementProcessor {

    private static final Logger LOG = LogManager.getLogger(MeasurementProcessor.class.getName());

    public static final MeasurementId MEASUREMENT_ID_TEMPERATURA = new MeasurementId(7);
    public static final MeasurementId MEASUREMENT_ID_RH = new MeasurementId(3);
    public static final MeasurementId MEASUREMENT_ID_O3 = new MeasurementId(8);
    public static final MeasurementId MEASUREMENT_ID_NO2 = new MeasurementId(14);
    public static final MeasurementId MEASUREMENT_ID_NO = new MeasurementId(15);
    public static final MeasurementId MEASUREMENT_ID_PM10 = new MeasurementId(12);
    public static final MeasurementId MEASUREMENT_ID_PM25 = new MeasurementId(13);
    public static final int NESSUNA_FORMULA = 0;
    public static final int FORMULA_PER_NO_E_NO2 = 1;
    public static final int FORMULA_PER_PM10_E_PM25 = 2;
     public static final int FORMULA_PER_03 = 3;

    private final MeasurementProcessorParameters measurementProcessorParameters = new MeasurementProcessorParameters();
    private final int RAD = 1;
    private static final MathContext mathContext = new MathContext(10, RoundingMode.HALF_EVEN);


    public Optional<ProcessedMeasurement> process(AugeG4RawData rawData, RawMeasurement rawMeasurement) {
        try {
            if (!isToProcess(rawData, rawMeasurement)) {
                return processMeasurementNotToProcess(rawMeasurement);
            }
            return processMeasurementToProcess(rawData, rawMeasurement);
        }catch(ArithmeticException ex) {
            ex.printStackTrace();
            return Optional.empty();
        }
    }

    private boolean isToProcess(AugeG4RawData rawData, RawMeasurement rawMeasurement) {
        List<RawMeasurement> measurements = rawData.getMeasurements().stream().filter(x->x.getId().getValue()==7).collect(Collectors.toList());
        return measurementProcessorParameters.hasMeasurementParameters(
                rawData.getControlUnitId(),
                rawMeasurement.getId(),
                !measurements.isEmpty() && measurements.get(0).getValue()<20 ? "20" : ""
        );
    }

    private Optional<ProcessedMeasurement> processMeasurementNotToProcess(RawMeasurement rawMeasurement) {
        return Optional.of(new ProcessedMeasurement(
                rawMeasurement.getId(),
                rawMeasurement.getValue(),
                null
        ));
    }

    private Optional<ProcessedMeasurement> processMeasurementToProcess(AugeG4RawData rawData, RawMeasurement rawMeasurement) {
        try {
            Optional<Double> processedValue = applyFunction(rawData, rawMeasurement);
            return processedValue.map(value -> new ProcessedMeasurement(
                    rawMeasurement.getId(),
                    rawMeasurement.getValue(),
                    value
            ));
        }catch(Exception e) {
            LOG.debug("Processing exception thrown for sensor " + rawData.getControlUnitId()+" and type "+rawMeasurement.getId());
            throw e;
        }
    }

    private Optional<Double> applyFunction(AugeG4RawData rawData, RawMeasurement rawMeasurement) {
        Optional<Double> O3 = getMeasurementFromRawData(rawData, MEASUREMENT_ID_O3);
        Optional<Double> NO = getMeasurementFromRawData(rawData, MEASUREMENT_ID_NO);
        Optional<Double> NO2 = getMeasurementFromRawData(rawData, MEASUREMENT_ID_NO2);
        Optional<Double> temperature = getMeasurementFromRawData(rawData, MEASUREMENT_ID_TEMPERATURA);
        Optional<Double> RH = getMeasurementFromRawData(rawData, MEASUREMENT_ID_RH);
        Optional<Double> PM10 = getMeasurementFromRawData(rawData, MEASUREMENT_ID_PM10);
        Optional<Double> PM25 = getMeasurementFromRawData(rawData, MEASUREMENT_ID_PM25);
        int formula = chooseCorrectFormula(rawMeasurement.getId(), O3, NO, NO2, temperature, RH, PM10, PM25);
        Optional<MeasurementParameters> parametersContainer= measurementProcessorParameters.getMeasurementParameters(
                rawData.getControlUnitId(),
                rawMeasurement.getId(),
                temperature.isPresent()?temperature.get():null
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
            case 3: return Optional.of(applyFunction3(
                    BigDecimal.valueOf(rawMeasurement.getValue()),
                    parameters.getA(),
                    parameters.getB(),
                    parameters.getC(),
                    parameters.getD(),
                    parameters.getE(),
                    parameters.getF(),
                    BigDecimal.valueOf(RH.get()),
                    BigDecimal.valueOf(temperature.get()),
                    BigDecimal.valueOf(NO2.get()),
                    BigDecimal.valueOf(O3.get()),
                    BigDecimal.valueOf(RAD)
            ));
            default:
                return Optional.empty();
        }

    }

    private int chooseCorrectFormula(MeasurementId rawMeasurementId, Optional<Double> O3, Optional<Double> NO, Optional<Double> NO2, Optional<Double> temperature,
            Optional<Double> RH, Optional<Double> PM10, Optional<Double> PM25) {
        int formula = NESSUNA_FORMULA;
        if ((MEASUREMENT_ID_NO2.equals(rawMeasurementId) || MEASUREMENT_ID_NO.equals(rawMeasurementId))
                && (O3.isPresent() && temperature.isPresent() && NO.isPresent() && NO2.isPresent()))
            formula = FORMULA_PER_NO_E_NO2;
        else if ((MEASUREMENT_ID_PM10.equals(rawMeasurementId) || MEASUREMENT_ID_PM25.equals(rawMeasurementId))
                && (RH.isPresent() && PM10.isPresent() && PM25.isPresent() && temperature.isPresent())
                && (!(temperature.get()>=20.0 && PM10.get()>100.0))
                && (!(temperature.get()<20.0 && RH.get()>97.0))
                )
            formula = FORMULA_PER_PM10_E_PM25;
        else if (MEASUREMENT_ID_O3.equals(rawMeasurementId)
                && (RH.isPresent() && PM10.isPresent() && temperature.isPresent()))
            formula = FORMULA_PER_03;
        return formula;
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
        BigDecimal uno = b.multiply(x.pow(2, mathContext), mathContext);
        LOG.debug("uno:"+uno);

        BigDecimal due = c.multiply(x, mathContext);
        LOG.debug("due:"+due);

        BigDecimal tre = d.multiply(BigDecimalMath.pow(O3, new BigDecimal("0.1")), mathContext);
        LOG.debug("tre:"+tre);

        BigDecimal quattro = e.multiply(T_int.pow(4), mathContext);
        LOG.debug("quattro:"+quattro);

        BigDecimal somma = a.add(uno,mathContext)
                .add(due, mathContext)
                .add(tre, mathContext)
                .add(quattro, mathContext);
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
        BigDecimal uno = b.multiply(BigDecimalMath.pow(x, new BigDecimal("0.7")), mathContext);
        LOG.debug("uno:"+uno);

        BigDecimal due = c.multiply(BigDecimalMath.pow(RH, new BigDecimal("0.75")), mathContext);
        LOG.debug("due:"+due);

        BigDecimal tre = d.multiply(BigDecimalMath.pow(T_int, new BigDecimal("0.3")), mathContext);
        LOG.debug("tre:"+tre);

        BigDecimal somma = a
                .add(uno, mathContext)
                .add(due, mathContext)
                .add(tre, mathContext);
        LOG.debug("somma:"+somma);
        return somma.doubleValue();
    }
    
    public double applyFunction3(BigDecimal x,
                                BigDecimal a,
                                BigDecimal b,
                                BigDecimal c,
                                BigDecimal d,
                                BigDecimal e,
                                BigDecimal f,
                                BigDecimal RH,
                                BigDecimal T_int,
                                BigDecimal NO2,
                                BigDecimal O3,
                                BigDecimal Rad) {
        BigDecimal uno = b.multiply(BigDecimalMath.pow(x, new BigDecimal("0.44")), mathContext);
        LOG.debug("uno:"+uno);

        BigDecimal due = c.multiply(BigDecimalMath.pow(NO2, new BigDecimal("0.58")), mathContext);
        LOG.debug("due:"+due);

        BigDecimal tre = d.multiply(BigDecimalMath.pow(RH, new BigDecimal("0.54")), mathContext);
        LOG.debug("tre:"+tre);
        
        BigDecimal quattro = e.multiply(BigDecimalMath.pow(T_int, new BigDecimal("1.2")), mathContext);
        LOG.debug("quattro:"+quattro);

        BigDecimal somma = a
                .add(uno, mathContext)
                .add(due, mathContext)
                .add(tre, mathContext)
                .add(quattro, mathContext);
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
