package it.bz.idm.bdp.augeg4.fun.convert.tohub;

import it.bz.idm.bdp.augeg4.dto.AugeG4ProcessedData;
import it.bz.idm.bdp.augeg4.dto.MeasurementId;
import it.bz.idm.bdp.augeg4.dto.ProcessedMeasurement;
import it.bz.idm.bdp.augeg4.dto.tohub.AugeG4ProcessedDataToHubDto;
import it.bz.idm.bdp.augeg4.dto.tohub.ProcessedMeasurementToHub;
import org.junit.Test;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static it.bz.idm.bdp.augeg4.fun.process.MeasurementProcessor.MEASUREMENT_ID_TEMPERATURA;
import static org.junit.Assert.assertEquals;

public class DataConverterHubTest {
    // TODO: Add tests

    private final DataConverterHub converter = new DataConverterHub();

    @Test
    public void test_convertion_of_valid_data () {
        // given
        ProcessedMeasurement measurement = new ProcessedMeasurement(MEASUREMENT_ID_TEMPERATURA, 1, null);
        List<ProcessedMeasurement> measurements = Collections.singletonList(measurement);
        Date aquisitionDate = new Date();
        AugeG4ProcessedData processed = new AugeG4ProcessedData("AIRQ01", aquisitionDate, new Date(), measurements);
        List<AugeG4ProcessedData> processedData = Collections.singletonList(processed);

        // when
        List<AugeG4ProcessedDataToHubDto> dtos = converter.convert(processedData);

        // then
        assertEquals(0, dtos.size());
    }

    @Test
    public void should_ignore_processed_data_with_invalid_MeasurementId () {
        // given
        ProcessedMeasurement measurement = new ProcessedMeasurement(new MeasurementId(-123), 1, 2.);
        List<ProcessedMeasurement> measurements = Collections.singletonList(measurement);
        AugeG4ProcessedData processed = new AugeG4ProcessedData("AIRQ01", new Date(), new Date(), measurements);
        List<AugeG4ProcessedData> processedData = Collections.singletonList(processed);

        // when
        List<AugeG4ProcessedDataToHubDto> dtos = converter.convert(processedData);

        // then
        assertEquals(0, dtos.size());
    }

}
