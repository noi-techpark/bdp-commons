package it.bz.idm.bdp.augeg4.fun.linearize;

import it.bz.idm.bdp.augeg4.dto.fromauge.AugeG4FromAlgorabDataDto;
import it.bz.idm.bdp.augeg4.dto.fromauge.RawResVal;
import it.bz.idm.bdp.augeg4.dto.toauge.AugeG4LinearizedDataDto;
import it.bz.idm.bdp.augeg4.dto.toauge.LinearResVal;
import it.bz.idm.bdp.augeg4.face.DataLinearizerFace;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
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
        return new AugeG4LinearizedDataDto(
                        fromAlgorab.getControlUnitId(),
                        fromAlgorab.getDateTimeAcquisition(),
                        getLinearizationDate(),
                        linearizeResVal(fromAlgorab.getResVal()));
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
