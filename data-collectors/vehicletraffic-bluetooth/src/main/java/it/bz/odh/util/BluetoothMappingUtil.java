package it.bz.odh.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.LocaleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.google.api.services.sheets.v4.model.ValueRange;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.OddsRecordDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.odh.service.SpreadsheetReader;

@Component
public class BluetoothMappingUtil {

	@Value("${spreadsheet.requiredFields}")
	private String[] requiredFields;
	@Autowired
	private EncryptUtil cryptUtil;
	@Value("${datatype}")
	private String datatype;
	@Lazy
	@Autowired
	private SpreadsheetReader reader;

	@Value("${spreadsheet.sheetName}")
	private String sheetName;

	private List<Map<String, String>> cachedData;

	/** converts data matrix to list of data objects
	 * @param valueRange google spreadsheet rows
	 * @return rows as list of objects
	 */
	public List<Map<String,String>> convertToMap(ValueRange valueRange) {
		int i;
		int j;
		List<String> headerRow = normalize(valueRange.getValues().get(0));
		List<Map<String,String>> boxes = new ArrayList<>();
		int size = valueRange.getValues().size();
		for (i = 1; i < size; i++) {
			Map<String,String> object = new HashMap<>();
			for (j=0;j<headerRow.size();j++) {
				List<Object> row = valueRange.getValues().get(i);
				if (j<row.size())
					object.put(headerRow.get(j), row.get(j).toString());
			}
			boxes.add(object);
		}
		return boxes;
	}

	private List<String> normalize(List<Object> list) {
		List<String> stringList = new ArrayList<>() ;
		for (Object obj : list) {
			stringList.add(obj.toString().toLowerCase());
		}
		return stringList;
	}

	public <T> DataMapDto<RecordDtoImpl> mapData(T data) {
		DataMapDto<RecordDtoImpl> dataMap = new DataMapDto<>();
		@SuppressWarnings("unchecked")
		List<OddsRecordDto> dtos = (List<OddsRecordDto>) data;
		for (OddsRecordDto dto : dtos){
			DataMapDto<RecordDtoImpl> stationMap = dataMap.upsertBranch(dto.getStationcode());
			DataMapDto<RecordDtoImpl> typeMap = stationMap.upsertBranch(datatype);
			SimpleRecordDto textDto = new SimpleRecordDto();
			String stringValue = dto.getMac();
			if (cryptUtil.isValid())
				stringValue = cryptUtil.encrypt(stringValue);
			textDto.setValue(stringValue);
			textDto.setTimestamp(dto.getGathered_on().getTime());
			textDto.setPeriod(1);
			typeMap.getData().add(textDto);
		}
		return dataMap;
	}
	/**
	 * @param objs checks if data objects have a valid format
	 */
	public void validate(List<Map<String, String>> objs) {
		int i=0;
		Set<Integer> errorRows = new HashSet<>();
		for (Map<String,String> obj:objs) {
			if (!isValid(obj))
				errorRows.add(i+2);
			i++;
		}
		if (!errorRows.isEmpty())
			throw new IllegalStateException("Rows " + errorRows + " contain unparsable data.");
	}

	/**
	 * @return all rows as objects which are deemed as valid data
	 */
	public List<Map<String, String>> getValidEntries() {
		if (cachedData != null)
				return cachedData;
		List<Map<String, String>> validEntries = new ArrayList<>();
		List<Map<String, String>> objs = convertToMap(reader.getWholeSheet(sheetName));
		for (Map<String, String> obj : objs){
			if (isValid(obj))
				validEntries.add(obj);
		}
		setCachedData(validEntries);
		return validEntries;
	}

	private boolean isValid(Map<String, String> obj) {
		boolean validCoordinates = false;
		String longString = obj.get("longitude");
		String latString = obj.get("latitude");
		try {
			double longitude = Double.parseDouble(longString);
			double latitude = Double.parseDouble(latString);

			validCoordinates = (longitude <= 180 && longitude >= -180 &&latitude <= 90 && latitude >= -90);
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		return (obj.get("id") != null && !obj.get("id").isEmpty() && validCoordinates);
	}

	/**
	 * @param id identifier of BluetoothStation
	 * @return coordinates in EPSG:4326 in the order lon,lat as defined in spreadsheet
	 */
	public Double[] getCoordinatesByIdentifier(String id) {
		for (Map<String, String> entry : getValidEntries())
			if (entry.get("id")!= null && entry.get("id").equals(id)) {
				Double lon = Double.parseDouble(entry.get("longitude"));
				Double lat = Double.parseDouble(entry.get("latitude"));
				return new Double[] {lon, lat};
			}
		return null;

	}

	/**
	 * @param id identifier of BluetoothStation
	 * @return all key values which are optional and therfore metadata
	 */
	public Map<String, Object> getMetaDataByIdentifier(String id) {
		List<String> req = Arrays.asList(requiredFields);
		for (Map<String, String> obj :getValidEntries())
			if (obj.get("id")!= null && obj.get("id").equals(id)) {
				Map<String, Object> metaDataObj = new HashMap<>();
				for (Map.Entry<String, String> entry : obj.entrySet()){
					if (!(req.contains(entry.getKey().toLowerCase()))){
						metaDataObj.put(entry.getKey(), entry.getValue());
					}
				}
				return metaDataObj;
			}
		return null;
	}

	public List<Map<String, String>> getCachedData() {
		return cachedData;
	}

	public void setCachedData(List<Map<String, String>> cachedData) {
		this.cachedData = cachedData;
	}

    /**
     * handle multiple languages in multiple columns
     * @param metaData current metadata of {@link StationDto}
     * @return
     */
    public Map<String, Object> mergeTranslations(Map<String, Object> metaData) {
        Map<String, Object> cleanMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : metaData.entrySet()) {
            String key = entry.getKey();
            String[] split = key.split(":");
            if (split.length<2) {
                cleanMap.put(key,entry.getValue());
                continue;
            }
            try {
                LocaleUtils.toLocale(split[0]); // check if it's a valid locale
                Object value = entry.getValue();
                if (value == null || value.toString().isEmpty())
                    continue;
                if (value instanceof String) {
                	Object object = cleanMap.get(split[1]);
					@SuppressWarnings("unchecked")
					Map<String,Object> existingMap = object instanceof Map ? (Map<String, Object>) object : new HashMap<>();
                	existingMap.put(split[0], value.toString());
                    cleanMap.put(split[1], existingMap);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cleanMap;
    }

}
