package it.bz.idm.bdp.augeg4.fun.process;

import it.bz.idm.bdp.augeg4.dto.MeasurementId;
import org.junit.Test;

import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class MeasurementProcessorParametersTest {


    @Test
    public void get_parameters_of_existing_station_and_res_val_id () {
        // given
        String controlUnitId = "AIRQ01";
        MeasurementId measurementId = MeasurementProcessor.MEASUREMENT_ID_NO2;
        double temperature = 10.0;
        MeasurementProcessorParameters processorParameters = new MeasurementProcessorParameters();

        // when
        Optional<MeasurementParameters> parameters = processorParameters.getMeasurementParameters(controlUnitId, measurementId, temperature);

        // expect
        assertTrue(parameters.isPresent());
        assertEquals(-1075.7313, parameters.get().getA().doubleValue());
        assertEquals(2.802068E-6, parameters.get().getE().doubleValue());

    }

    @Test
    public void get_parameters_of_existing_station_and_res_val_id_high_temperature () {
        // given
        String controlUnitId = "AIRQ01";
        MeasurementId measurementId = MeasurementProcessor.MEASUREMENT_ID_NO2;
        double temperature = 30.0; // high temp >= 20.0
        MeasurementProcessorParameters processorParameters = new MeasurementProcessorParameters();

        // when
        Optional<MeasurementParameters> parameters = processorParameters.getMeasurementParameters(controlUnitId, measurementId, temperature);

        // expect
        assertTrue(parameters.isPresent());
        assertEquals(-5400.2488, parameters.get().getA().doubleValue());
        assertEquals(-8.956467E-5, parameters.get().getE().doubleValue());

    }
}
