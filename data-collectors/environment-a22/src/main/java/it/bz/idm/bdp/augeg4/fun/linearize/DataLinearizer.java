package it.bz.idm.bdp.augeg4.fun.linearize;

import it.bz.idm.bdp.augeg4.dto.fromauge.AugeG4FromAlgorabDataDto;
import it.bz.idm.bdp.augeg4.dto.fromauge.RawResVal;
import it.bz.idm.bdp.augeg4.dto.toauge.AugeG4LinearizedDataDto;
import it.bz.idm.bdp.augeg4.dto.toauge.LinearResVal;
import it.bz.idm.bdp.augeg4.face.DataLinearizerFace;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DataLinearizer implements DataLinearizerFace {

    private final ResValLinearizer resValLinearizer = new ResValLinearizer();

    @Override
    public List<AugeG4LinearizedDataDto> linearize(List<AugeG4FromAlgorabDataDto> data) {
        return data
                .stream()
                .map(this::linearize)
                .collect(Collectors.toList());
    }

    public AugeG4LinearizedDataDto linearize(AugeG4FromAlgorabDataDto fromAlgorab) {
        String controlUnitId = fromAlgorab.getControlUnitId();
        Date acquisition = fromAlgorab.getDateTimeAcquisition();
        List<RawResVal> rawValues = fromAlgorab.getResVal();
        List<LinearResVal> linearizedResVal = linearizeResVal(rawValues);
        Date linearization = getLinearizationDate();
        return new AugeG4LinearizedDataDto(controlUnitId, acquisition, linearization, linearizedResVal);
    }

    private List<LinearResVal> linearizeResVal(List<RawResVal> rawValues) {
        return rawValues
                .stream()
                .map(resValLinearizer::linearize)
                .collect(Collectors.toList());
    }

    private Date getLinearizationDate() {
        return new Date();
    }
}
