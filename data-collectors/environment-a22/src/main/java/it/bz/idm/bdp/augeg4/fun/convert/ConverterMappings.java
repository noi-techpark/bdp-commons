package it.bz.idm.bdp.augeg4.fun.convert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConverterMappings {

    private static final Logger LOG = LogManager.getLogger(ConverterMappings.class.getName());

    private static final String CONVERTER_MAPPINGS_FILE_NAME = "/mappings/converterMappings.json";

    private final List<ConverterMapping> mappings;

    private final Map<Integer, String> dataTypeByLienarizedId;

    public ConverterMappings() {
        mappings = loadMappingsFromJsonFile();
        dataTypeByLienarizedId = createDataTypeByLinearizedIdMap();
    }

    private List<ConverterMapping> loadMappingsFromJsonFile() {
        ObjectMapper mapper = new ObjectMapper();
        URL fileUrl = ConverterMappings.class.getResource(CONVERTER_MAPPINGS_FILE_NAME);
        if (fileUrl == null) {
            LOG.error("loadMappingsFromJsonFile failed: mapping file not found");
            throw new IllegalStateException("can't find " + CONVERTER_MAPPINGS_FILE_NAME);
        }
        try {
            return mapper.readValue(fileUrl, new TypeReference<List<ConverterMapping>>(){});
        } catch (IOException e) {
            LOG.error("loadMappingsFromJsonFile failed: {}", e.getMessage());
            throw new IllegalStateException("can't read " + CONVERTER_MAPPINGS_FILE_NAME, e);
        }
    }

    private Map<Integer, String> createDataTypeByLinearizedIdMap() {
        Map<Integer, String> map = new HashMap<>();
        mappings.forEach(mapping -> map.put(mapping.getLinearizedId(), mapping.getDataType()));
        return map;
    }

    String mapLinearizedIdToDataType(int linearizedId) {
        if (!dataTypeByLienarizedId.containsKey(linearizedId)) {
            LOG.error("mapLinearizedIdToDataType called with an unknown linearized id: {}", linearizedId);
            throw new IllegalArgumentException("Unknown linearized id " + linearizedId);
        }
        return dataTypeByLienarizedId.get(linearizedId);
    }

    public List<ConverterMapping> getMappings() {
        return mappings;
    }
}
