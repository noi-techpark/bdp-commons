// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

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
        double temperature = 31.0;
        MeasurementProcessorParameters processorParameters = new MeasurementProcessorParameters();

        // when
        Optional<MeasurementParameters> parameters = processorParameters.getMeasurementParameters(controlUnitId, measurementId, temperature);

        // expect
        assertTrue(parameters.isPresent());
        assertEquals(-2947.954, parameters.get().getA().doubleValue());
        assertEquals(5.313303E-6, parameters.get().getE().doubleValue());

    }

    @Test
    public void get_parameters_of_existing_station_and_res_val_id_high_temperature () {
        // given
        String controlUnitId = "AIRQ01";
        MeasurementId measurementId = MeasurementProcessor.MEASUREMENT_ID_NO2;
        double temperature = 10.0; // high temp >= 20.0
        MeasurementProcessorParameters processorParameters = new MeasurementProcessorParameters();

        // when
        Optional<MeasurementParameters> parameters = processorParameters.getMeasurementParameters(controlUnitId, measurementId, temperature);

        // expect
        assertTrue(parameters.isPresent());
        assertEquals(-2947.954, parameters.get().getA().doubleValue());
        assertEquals(5.313303E-6, parameters.get().getE().doubleValue());
    }
}
