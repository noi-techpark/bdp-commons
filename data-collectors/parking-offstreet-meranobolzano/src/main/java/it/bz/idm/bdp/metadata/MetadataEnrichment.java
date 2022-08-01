package it.bz.idm.bdp.metadata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

	@Value("#{'${METADATA_ENRICHED_FIELDS}'.split(',')}")
	private List<String> enrichedFields;

	public void mapData(StationList stations) throws IOException {

		List<String> stationIdsInSheet = new ArrayList<>();
		List<String> stationIdsFromApi = new ArrayList<>();

		List<String> newStations = new ArrayList<>();
		List<Integer> deletedStationsRowIndexes = new ArrayList<>();

		HashMap<String, HashMap<String, String>> mapping = new HashMap<>();

		int sheetId = sheetDataFetcher.getSheetId();

		List<List<Object>> rows = sheetDataFetcher.getValues().getValues();

		List<Object> headerObjectRow = rows.remove(0);
		List<String> headerRow = headerObjectRow.stream().map(object -> Objects.toString(object, null).trim())
				.collect(Collectors.toList());

		// check index of enriched metadata columns
		List<Integer> metadataColumnIndexes = new ArrayList<>();
		for (int i = 0; i < headerRow.size(); i++) {
			String header = headerRow.get(i);
			if (enrichedFields.contains(header))
				metadataColumnIndexes.add(i);
		}

		// create id lists
		for (StationDto station : stations) {
			stationIdsFromApi.add(station.getId());
			mapping.put(station.getId(), null);
		}
		for (List<Object> row : rows) {
			String id = (String) row.get(0);
			stationIdsInSheet.add(id);
			HashMap<String, String> langMapping = new HashMap<>();

			for (String enrichedField : enrichedFields) {
				int headerIndex = headerRow.indexOf(enrichedField);
				if (headerIndex >= 0) {
					String value = (String) row.get(headerIndex);
					langMapping.put(enrichedField, value);
				}
			}
			mapping.put(id, langMapping);
		}

		// check which stations are already present
		for (int i = 0; i < stationIdsInSheet.size(); i++)
			if (!stationIdsFromApi.contains(stationIdsInSheet.get(i)))
				deletedStationsRowIndexes.add(i + 1);
		// delete removed stations
		sheetDataFetcher.deleteRows(deletedStationsRowIndexes, sheetId);

		// check which stations are not already in sheet
		for (String id : stationIdsFromApi)
			if (!stationIdsInSheet.contains(id))
				newStations.add(id);
		// insert new stations
		insertStationsInSheet(stations, newStations);

		// add enriched metadata from sheet to stations
		for (StationDto station : stations) {
			for(String enrichedField: enrichedFields){
				String newMetadata = mapping.get(station.getId()).get(enrichedField);
				station.getMetaData().put(enrichedField,newMetadata);
			}
		}
	}

	private void insertStationsInSheet(StationList stations, List<String> stationIds) throws IOException {
		List<List<Object>> values = new ArrayList<>();

		for (StationDto station : stations) {
			if (stationIds.contains(station.getId())) {
				List<Object> value = new ArrayList<>();
				value.add(station.getId());
				value.add(station.getOrigin());
				value.add(station.getName());

				values.add(value);
			}
		}

		sheetDataFetcher.appendRows(values);
	}
}
