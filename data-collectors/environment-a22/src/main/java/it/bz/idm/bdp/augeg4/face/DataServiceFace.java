package it.bz.idm.bdp.augeg4.face;



public interface DataServiceFace {

    void loadPreviouslySyncedStations() throws Exception;

    void syncStations() throws Exception;

    void syncDataTypes() throws Exception;

    void pushData() throws Exception;

}
