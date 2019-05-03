package it.bz.idm.bdp.augeg4.face;

import it.bz.idm.bdp.augeg4.dto.fromauge.AugeG4FromAlgorabDataDto;

import java.util.List;

public interface DataServiceFace {

    void loadPreviouslySyncedStations() throws Exception;

    void syncStationsWithHub() throws Exception;

    void syncDataTypesWithHub() throws Exception;

    void pushData() throws Exception;

    void addDataFromAlgorab(List<AugeG4FromAlgorabDataDto> fromAlgorab);
}
