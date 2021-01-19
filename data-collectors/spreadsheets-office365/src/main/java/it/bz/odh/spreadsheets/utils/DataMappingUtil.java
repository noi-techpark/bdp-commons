package it.bz.odh.spreadsheets.utils;

import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;
import it.bz.odh.spreadsheets.dto.DataTypeWrapperDto;
import it.bz.odh.spreadsheets.dto.MappingResult;
import it.bz.odh.spreadsheets.services.ODHClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

@Component
public class DataMappingUtil {

    public Set<String> excludeFromMetaData = new HashSet<>();

    @Value("${headers.addressId}")
    private String addressId;

    @Value("${headers.nameId}")
    private String nameId;

    @Value("${headers.longitudeId}")
    private String longitudeId;

    @Value("${headers.latitudeId}")
    private String latitudeId;

    @Value("${spreadsheetId}")
    private String origin;

    @Value("${headers.metaDataId}")
    private String metadataId;

    @Value("${composite.unique.key}")
    private String[] uniqueIdFields;


    @Lazy
    @Autowired
    private ODHClient odhClient;

    @Lazy
    @Autowired
    private LocationLookupUtil locationLookupUtil;

    @Lazy
    @Autowired
    private LangUtil langUtil;

    private NumberFormat numberFormatter = NumberFormat.getInstance(Locale.US);


    @PostConstruct
    public void initMetadata() throws IOException {
        excludeFromMetaData.add(longitudeId);
        excludeFromMetaData.add(latitudeId);
    }

    public MappingResult mapSheet(List<List<Object>> values, String sheetName, int sheetId) {
        MappingResult result = new MappingResult();
        Map<String, Short> headerMapping = listToMap(values.get(0));
        if (isValidStationSheet(headerMapping)) {
            StationList mappedStations = mapStations(values, headerMapping, sheetName);
            result.setStationDtos(mappedStations);
        } else if (isValidDataTypeSheet(headerMapping)) {
            DataTypeDto type = mapDataType(values,sheetId, headerMapping);
            DataTypeWrapperDto w = new DataTypeWrapperDto(type, sheetName);
            result.setDataType(w);
        }
        return result;
    }

    private DataTypeDto mapDataType(List<List<Object>> values, Integer sheetId, Map<String, Short> headerMapping) {
        DataTypeDto dto = new DataTypeDto();
        Short metaDataPosition = headerMapping.get(metadataId);
        //remove header row
        values.remove(0);
        dto.setName(origin + ":" + sheetId);
        for (List<Object> row : values) {
            if (!row.isEmpty()) {
                String key = row.get(metaDataPosition) != null ? row.get(metaDataPosition).toString() : null;
                row.remove(metaDataPosition.shortValue());
                Map<String, Object> metaDataMap = buildMetaDataMap(headerMapping, row);
                langUtil.mergeTranslations(metaDataMap, headerMapping);
                odhClient.normalizeMetaData(metaDataMap);
                dto.getMetaData().put(key, metaDataMap);
            }
        }
        odhClient.normalizeMetaData(dto.getMetaData());
        return dto;
    }

    /**
     * @param list values
     * @return mapping element in list with it's position in list
     */
    public Map<String, Short> listToMap(List<Object> list) {
        Map<String, Short> mapping = new HashMap<>();
        short count = 0;
        for (Object header : list) {
            if (header != null && !header.toString().isEmpty()) {
                mapping.put(header.toString().toLowerCase(), count);
                count++;
            }
        }
        return mapping;
    }

    protected boolean isValidStationSheet(Map<String, Short> headerMapping) {
        return (headerMapping.containsKey(addressId) || (headerMapping.containsKey(longitudeId) && headerMapping.containsKey(latitudeId)));
    }

    public boolean isValidDataTypeSheet(Map<String, Short> headerMapping) {
        return headerMapping.containsKey(metadataId);
    }

    ;

    /**
     * @param spreadSheetValues   values to map to a {@link StationDto}
     * @param headerMapping
     * @return list of all valid rows in form of {@link StationDto}
     */
    public StationList mapStations(List<List<Object>> spreadSheetValues, Map<String, Short> headerMapping, String sheetName) {
        StationList dtos = new StationList();
        Set<Integer> missingPositions = new HashSet<>();
        int i = 0;
        for (final List<Object> row : spreadSheetValues) {
            if (i > 0) {
                StationDto dto = null;
                try {
                    dto = mapStation(headerMapping, row);
                } catch (Exception ex) {
                    i++;
                    continue;
                }
                if (dto.getLongitude() == null) {
                    try {
                        locationLookupUtil.guessPositionByAddress(dto);
                    } catch (final IllegalStateException ex) {
                        missingPositions.add(i);
                        i++;
                        continue;
                    }
                }
                langUtil.guessLanguages(dto.getMetaData());
                langUtil.mergeTranslations(dto.getMetaData(), headerMapping);
                if (dto.getName() == null || dto.getName().isEmpty())
                    dto.setName(dto.getId());
                Map<String, Object> normalizedMetaData = odhClient.normalizeMetaData(dto.getMetaData());
                normalizedMetaData.put("sheetName", sheetName);
                dto.getMetaData().clear();
                dto.setMetaData(normalizedMetaData);
                dtos.add(dto);
            }
            i++;
        }

        //TODO print missing positions to info logger
        return dtos;
    }

    /**
     * @param headerMapping mapping of column index and column name
     * @param row           all cell values of one row
     * @return a {@link StationDto} containing all information of this row
     */
    public StationDto mapStation(Map<String, Short> headerMapping, List<Object> row) {
        StationDto dto = new StationDto();
        Short nameIndex = headerMapping.get(nameId);
        Short longIndex = headerMapping.get(longitudeId);
        Short latIndex = headerMapping.get(latitudeId);
        Integer rowSize = row.size();
        if (nameIndex != null && rowSize > nameIndex) {
            Object object = row.get(headerMapping.get(nameId.toLowerCase()));
            if (object != null && !object.toString().isEmpty())
                dto.setName(object.toString().trim().replace("\n", " ").replace("\r", "").replaceAll(" +", " "));
        }
        if (longIndex != null && latIndex != null && rowSize > longIndex && rowSize > latIndex) {
            try {
                String longString = row.get(longIndex).toString();
                String latString = row.get(latIndex).toString();
                if (!longString.isEmpty() && !latString.isEmpty()) {
                    dto.setLongitude(numberFormatter.parse(longString).doubleValue());
                    dto.setLatitude(numberFormatter.parse(latString).doubleValue());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Map<String, Object> metaData = buildMetaDataMap(headerMapping, row);
        dto.setMetaData(metaData);
        dto.setOrigin(origin);
        dto.setStationType(odhClient.getIntegreenTypology());
        dto.setId(generateUniqueId(dto));
        return dto;
    }

    private Map<String, Object> buildMetaDataMap(Map<String, Short> headerMapping, List<Object> row) {
        // map non required fileds to metadata
        Map<String, Object> metaData = new HashMap<>();
        for (Map.Entry<String, Short> entry : headerMapping.entrySet()) {
            Object value = null;
            if (row.size() > entry.getValue())
                value = row.get(entry.getValue());
            String keyValue = entry.getKey();
            if (!excludeFromMetaData.contains(keyValue) && value != null) {
                String text = StringUtils.normalizeSpace(value.toString()).replace("\n", " ").replace("\r", "");
                if (text != null && !text.isEmpty()) {
                    if (keyValue.length() > 0) {
                        Object jsonGuessedType = jsonTypeGuessing(text);
                        if (jsonGuessedType != null) {
                            metaData.put(keyValue, jsonGuessedType);
                        }
                    }
                }
            }
        }
        return metaData;
    }

    private String generateUniqueId(StationDto dto) {
        StringBuffer uniqueId = new StringBuffer();
        uniqueId.append(dto.getOrigin()).append(":");
        for (String idField : uniqueIdFields) {
            if (dto.getMetaData().get(idField) == null)
                throw new IllegalStateException("Impossible to create unique identifier since required filed " + idField + " is missing");
            String value = dto.getMetaData().get(idField).toString();
            if (value != null && !value.isEmpty()) {
                uniqueId.append(value);
            }
        }
        return uniqueId.toString().replaceAll("\\s+", "");
    }

    private Object jsonTypeGuessing(String text) {
        try {
            return numberFormatter.parse(text);
        } catch (ParseException e) {
            // Do not do anything since we just want to check if string is parsable to a
            // number
        }
        if ("true".equals(text) || "false".equals(text))
            return "true".equals(text);

        return text;
    }
}
