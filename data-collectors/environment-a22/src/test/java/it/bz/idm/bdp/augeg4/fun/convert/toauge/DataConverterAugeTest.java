package it.bz.idm.bdp.augeg4.fun.convert.toauge;

import it.bz.idm.bdp.augeg4.dto.AugeG4ProcessedData;
import it.bz.idm.bdp.augeg4.dto.MeasurementId;
import it.bz.idm.bdp.augeg4.dto.ProcessedMeasurement;
import it.bz.idm.bdp.augeg4.dto.toauge.AugeG4ProcessedDataToAugeDto;
import it.bz.idm.bdp.augeg4.dto.toauge.ProcessedResValToAuge;
import org.junit.Test;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DataConverterAugeTest {

    private final DataConverterAuge converter = new DataConverterAuge();

    @Test
    public void test_convertion_of_valid_data () {
        // given
        ProcessedMeasurement measurement = new ProcessedMeasurement(new MeasurementId(1), 1, 2);
        List<ProcessedMeasurement> measurements = Collections.singletonList(measurement);
        Date aquisitionDate = new Date(1555320000000l);
        Date processingDate = new Date(1555320011885l);
        AugeG4ProcessedData processed = new AugeG4ProcessedData("AIRQ01", aquisitionDate, processingDate, measurements);
        List<AugeG4ProcessedData> processedData = Collections.singletonList(processed);

        // when
        List<AugeG4ProcessedDataToAugeDto> dtos = converter.convert(processedData);

        // then
        assertEquals(processedData.size(), dtos.size());

        AugeG4ProcessedDataToAugeDto dto = dtos.get(0);
        assertEquals(aquisitionDate, dto.getDateTimeAcquisition());
        assertEquals(processingDate, dto.getDateTimeLinearization());

        List<ProcessedResValToAuge> resVals = dto.getResVal();
        assertEquals(measurements.size(), resVals.size());

        ProcessedResValToAuge resVal = resVals.get(0);
        assertEquals(101, resVal.getId());
    }


    @Test
    public void should_ignore_processed_data_with_invalid_MeasurementId () {
        // given
        ProcessedMeasurement measurement = new ProcessedMeasurement(new MeasurementId(-123), 1, 2);
        List<ProcessedMeasurement> measurements = Collections.singletonList(measurement);
        AugeG4ProcessedData processed = new AugeG4ProcessedData("AIRQ01", new Date(), new Date(), measurements);
        List<AugeG4ProcessedData> processedData = Collections.singletonList(processed);

        // when
        List<AugeG4ProcessedDataToAugeDto> dtos = converter.convert(processedData);

        // then
        assertEquals(0, dtos.size());
    }
}
