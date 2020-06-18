package it.bz.idm.bdp.augeg4.fun.convert;

import it.bz.idm.bdp.augeg4.dto.AugeG4ProcessedData;
import it.bz.idm.bdp.augeg4.dto.ProcessedMeasurement;
import it.bz.idm.bdp.augeg4.dto.tohub.AugeG4ProcessedDataToHubDto;
import it.bz.idm.bdp.augeg4.dto.tohub.ProcessedMeasurementToHub;
import it.bz.idm.bdp.augeg4.face.DataConverterHubFace;
import it.bz.idm.bdp.augeg4.fun.convert.tohub.DataConverterHub;
import org.junit.Test;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static it.bz.idm.bdp.augeg4.fun.process.MeasurementProcessor.MEASUREMENT_ID_O3;
import static org.junit.Assert.assertEquals;

public class DataConverterTest {

    private static final double ASSERT_EQUALS_DOUBLE_DELTA = 0.001;

    private final DataConverterHubFace converter = new DataConverterHub();

    @Test
    public void convert_AugeG4LinearizedData_to_AugeG4ToHubData () {
        // given
        ProcessedMeasurement measurement = new ProcessedMeasurement(MEASUREMENT_ID_O3, 1, 2.);
        List<ProcessedMeasurement> measurements = Collections.singletonList(measurement);
        Date acquisition = new Date();
        Date linearization = new Date();
        String controlUnitId = "AIRQ01";
        AugeG4ProcessedData processed = new AugeG4ProcessedData(controlUnitId, acquisition, linearization, measurements);
        List<AugeG4ProcessedData> processedData = Collections.singletonList(processed);

        // when
        List<AugeG4ProcessedDataToHubDto> convertedData = converter.convert(processedData);

        // then
        assertEquals(convertedData.size(), processedData.size());

        AugeG4ProcessedDataToHubDto toHubDto = convertedData.get(0);
        assertEquals("AUGEG4_AIRQ01", toHubDto.getStationId().getValue());
        assertEquals(processed.getDateTimeAcquisition(), toHubDto.getAcquisition());

        List<ProcessedMeasurementToHub> convertedMeasurements = toHubDto.getProcessedMeasurementsToHub();
        assertEquals(measurements.size(), convertedMeasurements.size());

        ProcessedMeasurementToHub convertedMeasurement = convertedMeasurements.get(0);
        assertEquals(measurement.getRawValue(), convertedMeasurement.getRawValue(), ASSERT_EQUALS_DOUBLE_DELTA);
        assertEquals(measurement.getProcessedValue(), convertedMeasurement.getProcessedValue(), ASSERT_EQUALS_DOUBLE_DELTA);
        assertEquals("O3_raw", convertedMeasurement.getDataType());
    }


}
