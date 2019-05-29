package it.bz.idm.bdp.augeg4.face;

import it.bz.idm.bdp.augeg4.dto.fromauge.AugeG4ElaboratedDataDto;

import java.util.List;

/**
 * Retrieves raw data from Algorab.
 */
public interface DataRetrieverFace {

    List<AugeG4ElaboratedDataDto> fetchData();

    void stop();
}
