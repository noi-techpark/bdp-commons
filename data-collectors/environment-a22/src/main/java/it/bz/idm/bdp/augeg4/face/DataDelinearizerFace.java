package it.bz.idm.bdp.augeg4.face;

import it.bz.idm.bdp.augeg4.dto.AugeG4RawData;
import it.bz.idm.bdp.augeg4.dto.fromauge.AugeG4ElaboratedDataDto;

import java.util.List;

public interface DataDelinearizerFace {
    List<AugeG4RawData> delinearize(List<AugeG4ElaboratedDataDto> data);
}
