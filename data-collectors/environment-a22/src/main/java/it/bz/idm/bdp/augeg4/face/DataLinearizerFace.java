package it.bz.idm.bdp.augeg4.face;

import it.bz.idm.bdp.augeg4.dto.fromauge.AugeG4FromAlgorabDataDto;
import it.bz.idm.bdp.augeg4.dto.toauge.AugeG4LinearizedDataDto;

import java.util.List;

/**
 * Converts dta from raw to cleaned linearized.
 */
public interface DataLinearizerFace {

    List<AugeG4LinearizedDataDto> linearize(List<AugeG4FromAlgorabDataDto> data);
}
