package it.bz.idm.bdp.augeg4.fun.linearize;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps the id inside the RawResVal to the id used in LineaResVal
 */
class ResValIdMapper {

    private final Map<Integer, Integer> linearizedIdByRawId = new HashMap<>();

    ResValIdMapper() {
        initLinearizedIdByRawIdMap();
    }

    private void initLinearizedIdByRawIdMap () {
        linearizedIdByRawId.put(1, 101);
        //  TODO: Update with the new values
    }

    int mapRawIdToLinearized(int dataId) {
        if(!linearizedIdByRawId.containsKey(dataId)) {
            throw new IllegalArgumentException("Unknown raw id " + dataId);
        }
        return linearizedIdByRawId.get(dataId);
    }
}
