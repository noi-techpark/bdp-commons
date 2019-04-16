package it.bz.idm.bdp.augeg4.face;

import it.bz.idm.bdp.augeg4.dto.toauge.AugeG4LinearizedDataDto;
import it.bz.idm.bdp.augeg4.dto.tohub.AugeG4ToHubDataDto;

import java.util.List;

/**
 * Converts data from linearized to Data Hub format.
 */
public interface DataConverterFace {

    List<AugeG4ToHubDataDto> convert(List<AugeG4LinearizedDataDto> data);
}
