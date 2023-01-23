package it.bz.odh.spreadsheets.mapper;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.google.api.services.sheets.v4.model.Sheet;

import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;
import it.bz.odh.spreadsheets.dto.MappingResult;
import it.bz.odh.spreadsheets.services.GoogleSpreadSheetDataFetcher;
import it.bz.odh.spreadsheets.util.LocationLookupUtil;

/**
 * Reads static e-charging stations from a google spreadsheet and maps them to Stations and plugs.
 * 
 * For each row in the spreadsheet we create
 * - parent station of type EChargingStation 
 * - child station of type EChargingPlug
 * 
 * The available connectors are then mapped as metadata of the Plug
 */
@Lazy
@Component
public class StaEchargingMapper implements ISheetMapper {
    private final Logger logger = LoggerFactory.getLogger(StaEchargingMapper.class);

    @Lazy
    @Autowired
    private GoogleSpreadSheetDataFetcher googleClient;

    private final static String nameId = "station_name";
    private final static String plugId = "plug_name";
    private final static String longitudeId = "longitude";
    private final static String latitudeId = "latitude";
    private final static String metadataStateId = "state";
    private final static String metadataAccessTypeId = "access_type";
    private final static String connectorTypesId = "connector_type";

    private final static String stationType = "EChargingStation";
    private final static String plugStationType = "EChargingPlug";

    @Value("${stationorigin}")
    private String origin;

    @Value("${uniqueIdPrefix}")
    private String idPrefix;

    @Lazy
    @Autowired
    private LocationLookupUtil locationLookupUtil;

    private NumberFormat numberFormatter = NumberFormat.getInstance(Locale.US);

    public MappingResult mapSheet(List<List<Object>> values, Sheet sheet) {
        MappingResult result = new MappingResult();
        StationList mappedStations = mapStations(values);
        result.setStationDtos(mappedStations);
        return result;
    }

    private StationList mapStations(List<List<Object>> spreadSheetValues) {
        // convert header to List of Strings. We assume that every column from 0 - size
        // is populated
        ArrayList<String> colNames = new ArrayList<>(
                spreadSheetValues.get(0).stream()
                        .map(e -> StringUtils.lowerCase(Objects.toString(e, null)))
                        .collect(Collectors.toList()));

        StationList dtos = new StationList();

        int i = 0;

        for (final List<Object> row : spreadSheetValues) {
            // always skip header row
            if (i++ == 0) {
                continue;
            }

            try {
                // returns a list of DTOs, because the plugs are mapped as stations, too
                dtos.addAll(mapStation(rowToMap(colNames, row)));
            } catch (Exception ex) {
                logger.error("Exception mapping station for row " + i, ex);
                continue;
            }
        }

        return dtos;
    }

    private List<StationDto> mapStation(Map<String, Object> row) throws Exception {
        List<StationDto> stations = new ArrayList<>();

        // Map the station parent
        StationDto dto = new StationDto();

        dto.setName(getStringValue(nameId, row));
        dto.setLatitude(getDoubleValue(latitudeId, row));
        dto.setLongitude(getDoubleValue(longitudeId, row));

        Map<String, Object> meta = new HashMap<>();
        meta.put("state", getStringValue(metadataStateId, row));
        meta.put("accessType", getStringValue(metadataAccessTypeId, row));
        dto.setMetaData(meta);

        dto.setOrigin(origin);
        dto.setStationType(stationType);
        dto.setId(idPrefix + "*" + dto.getName());
        stations.add(dto);

        // Map the plug as a child station
        StationDto plugDto = new StationDto();
        String plugName = getStringValue(plugId, row);
        plugDto.setName(String.format("%s - %s", dto.getName(), plugName));
        plugDto.setId(String.format("%s*%s", dto.getId(), plugName));
        plugDto.setOrigin(dto.getOrigin());
        plugDto.setStationType(plugStationType);
        plugDto.setLatitude(dto.getLatitude());
        plugDto.setLongitude(dto.getLongitude());
        plugDto.setParentStation(dto.getId());
        
        // Untangle available connectors and register them as plug metadata
        String strPlugs = getStringValue(connectorTypesId, row);
        List<String> plugs = Arrays.stream(strPlugs.split(","))
                .filter(e -> !StringUtils.isBlank(e))
                .map(String::trim)
                .sorted()
                .collect(Collectors.toList());
        
        Map<String, Object> plugMeta = new HashMap<>();
        List<Object> outlets = new ArrayList<>(plugs.size());
        int i = 1;
        for (String plug : plugs) {
            Map<String, Object> outlet = new HashMap<>();
            outlet.put("id", plugDto.getId() + "*" + i++);
            outlet.put("outletTypeCode", plug);
            outlets.add(outlet);
        }

        plugMeta.put("outlets", outlets);
        plugDto.setMetaData(plugMeta);
        stations.add(plugDto);

        return stations;
    }

    private String getStringValue(String columnName, Map<String, Object> row) {
        return Objects.toString(row.get(columnName), null);
    }

    private Double getDoubleValue(String columnName, Map<String, Object> row) throws Exception {
        String s = getStringValue(columnName, row);
        return numberFormatter.parse(s).doubleValue();
    }

    private Map<String, Object> rowToMap(List<String> cols, List<Object> row) {
        Map<String, Object> ret = new HashMap<>();
        int i = 0;
        for (Object o : row) {
            String col = cols.get(i);
            if (col != null) {
                ret.put(col, Objects.toString(o, null));
            }
            i++;
        }
        return ret;
    }
}
