package it.bz.idm.bdp.augeg4.fun.delinearize;

import it.bz.idm.bdp.augeg4.dto.AugeG4RawData;
import it.bz.idm.bdp.augeg4.dto.RawMeasurement;
import it.bz.idm.bdp.augeg4.dto.fromauge.AugeG4ElaboratedDataDto;
import it.bz.idm.bdp.augeg4.dto.fromauge.ElaboratedResVal;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DataDelinearizerTest {
    private final DataDelinearizer delinearizer = new DataDelinearizer();

    @Test
    public void test_mapping_from_AugeG4FromAlgorabData_to_AugeG4LinearizedDataDto() {
        // given
        AugeG4ElaboratedDataDto fromAlgorab = new AugeG4ElaboratedDataDto();
        fromAlgorab.setControlUnitId("STATION A");
        fromAlgorab.setDateTimeAcquisition(new Date());
        ElaboratedResVal elaboratedResVal = new ElaboratedResVal(1, 1.0, 1, 1.0, 1.0);
        List<ElaboratedResVal> elaboratedResVals = Collections.singletonList(elaboratedResVal);
        fromAlgorab.setResVal(elaboratedResVals);
        List<AugeG4ElaboratedDataDto> fromAlgorabDtos = Collections.singletonList(fromAlgorab);

        // when
        List<AugeG4RawData> delinearizedDtos = delinearizer.delinearize(fromAlgorabDtos);

        // then
        assertEquals(fromAlgorabDtos.size(), delinearizedDtos.size());

        AugeG4RawData delinearized = delinearizedDtos.get(0);
        assertEquals(fromAlgorab.getControlUnitId(), delinearized.getControlUnitId());
        assertEquals(fromAlgorab.getDateTimeAcquisition(), delinearized.getDateTimeAcquisition());

        List<RawMeasurement> linearizedResVals = delinearized.getMeasurements();
        assertEquals(elaboratedResVals.size(), linearizedResVals.size());
    }



    @Test
    public void test_ignore_data_with_unknown_lin_fun_id() {
        // given
        AugeG4ElaboratedDataDto fromAlgorab = new AugeG4ElaboratedDataDto();
        fromAlgorab.setControlUnitId("STATION A");
        fromAlgorab.setDateTimeAcquisition(new Date());
        ElaboratedResVal elaboratedResVal1 = new ElaboratedResVal(1, 1.0, -123, 1.0, 1.0);
        ElaboratedResVal elaboratedResVal2 = new ElaboratedResVal(1, 1.0, 1, 1.0, 1.0);
        fromAlgorab.setResVal(Arrays.asList(elaboratedResVal1,elaboratedResVal2));
        List<AugeG4ElaboratedDataDto> fromAlgorabDtos = Collections.singletonList(fromAlgorab);

        // when
        List<AugeG4RawData> delinearizedDtos = delinearizer.delinearize(fromAlgorabDtos);

        // then
        assertEquals(1, delinearizedDtos.size());

        AugeG4RawData delinearized = delinearizedDtos.get(0);
        assertEquals(fromAlgorab.getControlUnitId(), delinearized.getControlUnitId());
        assertEquals(fromAlgorab.getDateTimeAcquisition(), delinearized.getDateTimeAcquisition());

        List<RawMeasurement> delinearizedResVals = delinearized.getMeasurements();
        assertEquals(1, delinearizedResVals.size());
    }
}
