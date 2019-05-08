package it.bz.idm.bdp.augeg4.fun.linearize;

import it.bz.idm.bdp.augeg4.dto.fromauge.AugeG4FromAlgorabDataDto;
import it.bz.idm.bdp.augeg4.dto.fromauge.RawResVal;
import it.bz.idm.bdp.augeg4.dto.toauge.AugeG4LinearizedDataDto;
import it.bz.idm.bdp.augeg4.dto.toauge.LinearResVal;
import org.junit.Test;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DataLinearizerTest {

    private final DataLinearizer linearizer = new DataLinearizer();

    @Test
    public void test_mapping_from_AugeG4FromAlgorabData_to_AugeG4LinearizedDataDto () {
        // given
        AugeG4FromAlgorabDataDto fromAlgorab = new AugeG4FromAlgorabDataDto();
        fromAlgorab.setControlUnitId("STATION A");
        fromAlgorab.setDateTimeAcquisition(new Date());
        RawResVal rawResVal = new RawResVal(1, 1, 1, 1, 1);
        List<RawResVal> rawResVals = Collections.singletonList(rawResVal);
        fromAlgorab.setResVal(rawResVals);
        List<AugeG4FromAlgorabDataDto> fromAlgorabDtos = Collections.singletonList(fromAlgorab);

        // when
        List<AugeG4LinearizedDataDto> linearizedDtos = linearizer.linearize(fromAlgorabDtos);

        // then
        assertEquals(fromAlgorabDtos.size(), linearizedDtos.size());

        AugeG4LinearizedDataDto linearized = linearizedDtos.get(0);
        assertEquals(fromAlgorab.getControlUnitId(), linearized.getControlUnitId());
        assertEquals(fromAlgorab.getDateTimeAcquisition(), linearized.getDateTimeAcquisition());
        // TODO: Check linearization date

        List<LinearResVal> linearizedResVals = linearized.getResVal();
        assertEquals(rawResVals.size(), linearizedResVals.size());
    }

}
