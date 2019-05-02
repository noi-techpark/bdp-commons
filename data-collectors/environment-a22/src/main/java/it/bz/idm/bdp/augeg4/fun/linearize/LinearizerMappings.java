package it.bz.idm.bdp.augeg4.fun.linearize;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Maps the id inside the RawResVal to the id used in LineaResVal
 */
class LinearizerMappings {

    private static final Logger LOG = LogManager.getLogger(LinearizerMappings.class.getName());

    private static final String LINEARIZER_MAPPINGS_FILE_NAME = "/mappings/linearizerMappings.json";

    private static class IdMapping {
        int rawId;
        int linearizedId;

        void setRawId(int rawId) {
            this.rawId = rawId;
        }

        void setLinearizedId(int linearizedId) {
            this.linearizedId = linearizedId;
        }
    }

    private final Map<Integer, Integer> linearizedIdByRawId = new HashMap<>();

    LinearizerMappings() {
        initLinearizedIdByRawIdMap();
    }

    private void initLinearizedIdByRawIdMap () {
        IdMapping[] mappings = loadMappingsFromJsonFile();
        insertMappingsInLinearizedIdByRawIdMap(mappings);
    }

    private IdMapping[] loadMappingsFromJsonFile () {
        ObjectMapper mapper = new ObjectMapper();
        URL fileUrl = LinearizerMappings.class.getResource(LINEARIZER_MAPPINGS_FILE_NAME);
        if (fileUrl == null) {
            LOG.error("loadMappingsFromJsonFile failed: mapping file not found");
            throw new IllegalStateException("can't find " + LINEARIZER_MAPPINGS_FILE_NAME);
        }
        try {
            return mapper.readValue(fileUrl, IdMapping[].class);
        } catch (IOException ex) {
            LOG.error("loadMappingsFromJsonFile failed: {}", ex.getMessage());
            throw new IllegalStateException("can't read " + LINEARIZER_MAPPINGS_FILE_NAME, ex);
        }
    }

    private void insertMappingsInLinearizedIdByRawIdMap (IdMapping[] mappings) {
        for (IdMapping mapping : mappings) {
            linearizedIdByRawId.put(mapping.rawId, mapping.linearizedId);
        }
    }

    int mapRawIdToLinearized(int dataId) {
        if(!linearizedIdByRawId.containsKey(dataId)) {
            LOG.error("mapRawIdToLinearized called with an unknown raw id: {}", dataId);
            throw new IllegalArgumentException("Unknown raw id " + dataId);
        }
        return linearizedIdByRawId.get(dataId);
    }
}
