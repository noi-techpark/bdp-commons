package it.bz.odh.spreadsheets.services;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.ProvenanceDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.json.NonBlockingJSONPusher;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Lazy
@Service
public class ODHClient extends NonBlockingJSONPusher {

    @Value("${stationtype}")
    private String stationtype;

    @Value("${provenance.name}")
    private String provenanceName;

    @Value("${provenance.version}")
    private String provenanceVersion;

    @Value("${spreadsheetId}")
    private String origin;

    @Override
    public <T> DataMapDto<RecordDtoImpl> mapData(T data) {
        return null;
    }

    @Override
    public String initIntegreenTypology() {
        return stationtype;
    }

    @Override
    public ProvenanceDto defineProvenance() {
        return new ProvenanceDto(null, provenanceName, provenanceVersion, origin);
    }


    private String normalizeKey(String keyValue) {
        String accentFreeString = StringUtils.stripAccents(keyValue).replaceAll(" ", "_");
        String asciiString = accentFreeString.replaceAll("[^\\x00-\\x7F]", "");
        String validVar = asciiString.replaceAll("[^\\w0-9]", "");
        return validVar;
    }


    public Map<String, Object> normalizeMetaData(Map<String, Object> metaData) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : metaData.entrySet()) {
            String normalizedKey = normalizeKey(entry.getKey());
            resultMap.put(normalizedKey, entry.getValue());
        }
        return resultMap;
    }
}
