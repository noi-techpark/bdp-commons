package it.bz.odh.spreadsheets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.text.NumberFormat;

import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;

import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;
import it.bz.odh.spreadsheets.services.GoogleSpreadSheetDataFetcher;
import it.bz.odh.spreadsheets.services.ODHClient;

/**
 * Reads static e-charging stations from a google spreadsheet and maps them to
 * Stations and plugs.
 * 
 * For each row in the spreadsheet we create
 * - parent station of type EChargingStation
 * - child station of type EChargingPlug
 * 
 * The available connectors are then mapped as metadata of the Plug
 */
@Component
public class StaEchargingCollector implements ISpreadsheetCollector {
    private Logger logger = LoggerFactory.getLogger(StaEchargingCollector.class);
    private final static String nameId = "station_name";
    private final static String plugId = "plug_name";
    private final static String longitudeId = "longitude";
    private final static String latitudeId = "latitude";
    private final static String metadataStateId = "state";
    private final static String metadataAccessTypeId = "access_type";
    private final static String connectorTypesId = "connector_type";

    private final static String stationType = "EChargingStation";
    private final static String plugStationType = "EChargingPlug";

    @Value("${spreadsheetId}")
    private String origin;
    @Value("${uniqueIdPrefix}")
    private String idPrefix;

    @Lazy
    @Autowired
    private GoogleSpreadSheetDataFetcher googleClient;

    private NumberFormat numberFormatter = NumberFormat.getInstance(Locale.US);

    @Lazy
    @Autowired
    private ODHClient odhClient;

    public void syncData() {
        logger.info("Start data syncronization");
        Spreadsheet fetchedSpreadSheet = (Spreadsheet) googleClient.fetchSheet();
        logger.debug("Start reading spreadsheet");
        Sheet sheet = fetchedSpreadSheet.getSheets().get(0);

        StationList stationDtos = new StationList();
        StationList plugDtos = new StationList();

        logger.debug("Start mapping sheet");
        mapSheet(stationDtos, plugDtos, sheet);

        if (!stationDtos.isEmpty()) {
            logger.debug("Syncronize stations if some where fetched and successfully parsed");

            odhClient.syncStations(stationType, stationDtos);
            odhClient.syncStations(plugStationType, plugDtos);

            logger.debug("Syncronize stations completed");
        }
        logger.info("Data syncronization completed");
    }

    public void mapSheet(StationList stationDtos, StationList plugDtos, Sheet sheet) {
        List<List<Object>> spreadSheetRows = googleClient.getWholeSheet(sheet.getProperties().getTitle()).getValues();

        ArrayList<String> colNames = new ArrayList<>(
                spreadSheetRows.get(0).stream()
                        .map(e -> StringUtils.lowerCase(Objects.toString(e, null)))
                        .collect(Collectors.toList()));

        int i = 1;
        for (final List<Object> row : spreadSheetRows) {
            try {
                mapRow(rowToMap(colNames, row), stationDtos, plugDtos);
            } catch (Exception ex) {
                logger.error("Exception mapping station for row " + i++, ex);
                continue;
            }
        }
    }

    private void mapRow(Map<String, Object> row, StationList stationDtos, StationList plugDtos) throws Exception {
        // Map the station parent
        StationDto station = new StationDto();

        station.setName(getString(nameId, row));
        station.setLatitude(getDouble(latitudeId, row));
        station.setLongitude(getDouble(longitudeId, row));

        Map<String, Object> meta = new HashMap<>();
        meta.put("state", getString(metadataStateId, row));
        meta.put("accessType", getString(metadataAccessTypeId, row));
        station.setMetaData(meta);

        station.setOrigin(origin);
        station.setStationType(stationType);
        station.setId(idPrefix + "*" + station.getName());
        stationDtos.add(station);

        // Map the plug as a child station
        StationDto plug = new StationDto();
        String plugName = getString(plugId, row);
        plug.setName(String.format("%s - %s", station.getName(), plugName));
        plug.setId(String.format("%s*%s", station.getId(), plugName));
        plug.setOrigin(station.getOrigin());
        plug.setStationType(plugStationType);
        plug.setLatitude(station.getLatitude());
        plug.setLongitude(station.getLongitude());
        plug.setParentStation(station.getId());

        // Untangle available connectors and register them as plug metadata
        String strPlugs = getString(connectorTypesId, row);
        List<String> outletTypes = Arrays.stream(strPlugs.split(","))
                .filter(e -> !StringUtils.isBlank(e))
                .map(String::trim)
                .sorted()
                .collect(Collectors.toList());

        List<Object> outlets = new ArrayList<>(outletTypes.size());
        int i = 1;
        for (String outletType : outletTypes) {
            Map<String, Object> outlet = new HashMap<>();
            outlet.put("id", plug.getId() + "*" + i++);
            outlet.put("outletTypeCode", outletType);
            outlets.add(outlet);
        }

        Map<String, Object> plugMeta = new HashMap<>();
        plugMeta.put("outlets", outlets);
        plug.setMetaData(plugMeta);
        plugDtos.add(plug);
    }

    private String getString(String columnName, Map<String, Object> row) {
        return Objects.toString(row.get(columnName), null);
    }

    private Double getDouble(String columnName, Map<String, Object> row) throws Exception {
        String s = getString(columnName, row);
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