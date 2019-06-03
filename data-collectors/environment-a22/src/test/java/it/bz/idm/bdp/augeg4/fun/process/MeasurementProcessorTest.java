package it.bz.idm.bdp.augeg4.fun.process;

import it.bz.idm.bdp.augeg4.dto.AugeG4RawData;
import it.bz.idm.bdp.augeg4.dto.ProcessedMeasurement;
import it.bz.idm.bdp.augeg4.dto.RawMeasurement;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static it.bz.idm.bdp.augeg4.fun.process.MeasurementProcessor.MEASUREMENT_ID_O3;
import static it.bz.idm.bdp.augeg4.fun.process.MeasurementProcessor.MEASUREMENT_ID_TEMPERATURA;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MeasurementProcessorTest {

    @Test
    public void test_processing_of_raw_data_not_to_process() {
        // given
        MeasurementProcessor measurementProcessor = new MeasurementProcessor();
        RawMeasurement rawMeasurement = new RawMeasurement(MEASUREMENT_ID_TEMPERATURA, 1.2);
        AugeG4RawData rawData = new AugeG4RawData("AIRQ01", new Date(), Collections.singletonList(rawMeasurement));

        // when
        Optional<ProcessedMeasurement> processedMeasurementContainer = measurementProcessor.process(rawData, rawMeasurement);

        // then
        assertTrue(processedMeasurementContainer.isPresent());
    }

    @Test
    public void test_processing_of_raw_to_process() {
        // given
        MeasurementProcessor measurementProcessor = new MeasurementProcessor();
        RawMeasurement temperatura = new RawMeasurement(MEASUREMENT_ID_TEMPERATURA, 1.2);
        RawMeasurement O3 = new RawMeasurement(MEASUREMENT_ID_O3, 2.3);
        AugeG4RawData rawData = new AugeG4RawData("AIRQ01", new Date(), Arrays.asList(temperatura, O3));

        // when
        Optional<ProcessedMeasurement> processedMeasurementContainer = measurementProcessor.process(rawData, O3);

        // then
        assertTrue(processedMeasurementContainer.isPresent());
    }


    @Test
    public void ignore_failed_processing_of_raw_data_due_to_missing_measurement_used_in_complex_formula() {
        // given
        MeasurementProcessor measurementProcessor = new MeasurementProcessor();
        RawMeasurement O3 = new RawMeasurement(MEASUREMENT_ID_O3, 2.3);
        AugeG4RawData rawData = new AugeG4RawData("AIRQ01", new Date(), Collections.singletonList(O3));

        // when
        Optional<ProcessedMeasurement> processedMeasurementContainer = measurementProcessor.process(rawData, O3);

        // then
        assertFalse(processedMeasurementContainer.isPresent());
    }

}
