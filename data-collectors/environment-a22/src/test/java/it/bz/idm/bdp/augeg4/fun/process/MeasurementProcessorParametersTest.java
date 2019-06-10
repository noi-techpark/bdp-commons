package it.bz.idm.bdp.augeg4.fun.process;

import org.junit.Test;

import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class MeasurementProcessorParametersTest {

    @Test
    public void get_parameters_of_existing_station_and_res_val_id () {
        // given
        String controlUnitId = "AIRQ01";
        MeasurementProcessorParameters processorParameters = new MeasurementProcessorParameters();

        // when
        Optional<MeasurementParameters> parameters = processorParameters.getMeasurementParameters(controlUnitId, MeasurementProcessor.MEASUREMENT_ID_NO2);

        // expect
        assertTrue(parameters.isPresent());
        assertEquals(-450.3, parameters.get().getA().doubleValue());

    }

}
