package it.bz.idm.bdp.dcparkingtn.metadata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;

@Lazy
@Component
public class MetadataEnrichment {

	@Lazy
	@Autowired
	private GoogleSpreadSheetUtil sheetDataFetcher;

	private List<String> stationIdsInSheet;
	private HashMap<String, HashMap<String, String>> mapping;
	private List<Integer> metadataColumnIndexes;
	private int sheetId;
	private List<String> enrichedFields;

	@PostConstruct
	private void init() {
		sheetId = sheetDataFetcher.getSheetId();
		stationIdsInSheet = new ArrayList<>();
		mapping = new HashMap<>();
		metadataColumnIndexes = new ArrayList<>();
		enrichedFields = new ArrayList<>();
	}

	public void mapData(StationList stations) throws IOException {
		resetValues();

		List<List<Object>> rows = sheetDataFetcher.getValues().getValues();
		List<String> headerRow = extractHeaderRow(rows);

		// initialize enriched fields
		for(int i = 3; i < headerRow.size(); i++)
			enrichedFields.add(headerRow.get(i));

		initializeMetadataColumnIndexes(headerRow);
		initializeMapping(rows, headerRow);

		// disable stations deletion
		// removeStationsFromSheet(stations);
		insertStationsInSheet(stations);
		enrichMetadata(stations);
	}

	private void initializeMetadataColumnIndexes(List<String> headerRow) {
		for (int i = 0; i < headerRow.size(); i++) {
			String header = headerRow.get(i);
			if (enrichedFields.contains(header))
				metadataColumnIndexes.add(i);
		}
	}

	private void initializeMapping(List<List<Object>> rows, List<String> headerRow ){
		for (List<Object> row : rows) {
			String id = (String) row.get(0);
			stationIdsInSheet.add(id);
			HashMap<String, String> langMapping = new HashMap<>();

			for (String enrichedField : enrichedFields) {
				int headerIndex = headerRow.indexOf(enrichedField);
				if (headerIndex >= 0 && headerIndex < row.size())
					langMapping.put(enrichedField, (String) row.get(headerIndex));
			}
			mapping.put(id, langMapping);
		}
	}

	private void removeStationsFromSheet(StationList stations)
			throws IOException {
		List<Integer> deletedStationsRowIndexes = new ArrayList<>();
		List<String> stationIdsFromApi = new ArrayList<>();

		for (StationDto station : stations)
			stationIdsFromApi.add(station.getId());

		// check which stations are no longer coming from the api, but are inside the
		// sheet
		for (int i = 0; i < stationIdsInSheet.size(); i++)
			if (!stationIdsFromApi.contains(stationIdsInSheet.get(i)))
				deletedStationsRowIndexes.add(i + 1);

		sheetDataFetcher.deleteRows(sheetId, deletedStationsRowIndexes);
	}

	private void insertStationsInSheet(StationList stations) throws IOException {
		List<List<Object>> values = new ArrayList<>();

		// check which stations are not already in sheet
		List<String> newStations = new ArrayList<>();
		for (StationDto station : stations)
			if (!stationIdsInSheet.contains(station.getId()))
				newStations.add(station.getId());

		for (StationDto station : stations) {
			if (newStations.contains(station.getId())) {
				List<Object> value = new ArrayList<>();
				value.add(station.getId());
				value.add(station.getOrigin());
				value.add(station.getName());

				values.add(value);
			}
		}

		sheetDataFetcher.appendRows(values);
	}

	private List<String> extractHeaderRow(List<List<Object>> rows) {
		List<Object> headerObjectRow = rows.remove(0);
		// transform object list top string list
		return headerObjectRow.stream().map(object -> Objects.toString(object, null).trim())
				.collect(Collectors.toList());
	}

	private void enrichMetadata(StationList stations) {
		for (StationDto station : stations) {
			for (String enrichedField : enrichedFields) {
				if (mapping.get(station.getId()) != null && mapping.get(station.getId()).get(enrichedField) != null) {
					String newMetadata = mapping.get(station.getId()).get(enrichedField);
					station.getMetaData().put(enrichedField, newMetadata);
				}
			}
		}
	}

	private void resetValues() {
		stationIdsInSheet.clear();
		mapping.clear();
		metadataColumnIndexes.clear();
		enrichedFields.clear();
	}
}
