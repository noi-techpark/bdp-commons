package it.bz.idm.bdp.augeg4.face;

import it.bz.idm.bdp.augeg4.dto.fromauge.AugeG4FromAlgorabDataDto;

import java.util.List;

/**
 * Retrieves raw data from Algorab.
 */
public interface DataRetrieverFace {
    List<AugeG4FromAlgorabDataDto> fetchData() throws Exception;
}
