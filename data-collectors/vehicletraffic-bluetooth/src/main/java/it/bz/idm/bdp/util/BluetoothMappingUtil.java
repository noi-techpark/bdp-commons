package it.bz.idm.bdp.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.google.api.services.sheets.v4.model.ValueRange;

import it.bz.idm.bdp.service.SpreadsheetReader;

@Component
public class BluetoothMappingUtil {

	@Value("${spreadsheet.requiredFields}")
	private String[] requiredFields;

	@Lazy
	@Autowired
	private SpreadsheetReader reader;

	@Value("${spreadsheet.sheetName}")
	private String SHEETNAME;

	private List<Map<String, String>> cachedData;
	
	public List<Map<String,String>> convertToMap(ValueRange valueRange) {
		int i,j;
		List<String> headerRow = normalize(valueRange.getValues().get(0));
		List<Map<String,String>> boxes = new ArrayList<Map<String,String>>();
		int size = valueRange.getValues().size();
		for (i = 1; i < size; i++) {
			Map<String,String> object = new HashMap<String, String>();
			for (j=0;j<headerRow.size();j++) {
				List<Object> row = valueRange.getValues().get(i);
				if (j<row.size())
					object.put(headerRow.get(j).toString(), row.get(j).toString());
			}
			boxes.add(object);
		}
		return boxes;
		
	}

	private List<String> normalize(List<Object> list) {
		List<String> stringList = new ArrayList<String>() ;
		for (Object obj : list) {
			stringList.add(obj.toString().toLowerCase());
		}
		return stringList;
	}

	public void validate(List<Map<String, String>> objs) {
		int i=0;
		Set<Integer> errorRows = new HashSet<Integer>();
		for (Map<String,String> obj:objs) {
			if (!isValid(obj))
				errorRows.add(i+2);
			i++;
		}
		if (!errorRows.isEmpty())
			throw new IllegalStateException("Rows " + errorRows + " contain unparsable data.");
	}

	public List<Map<String, String>> getValidEntries() {
		if (cachedData != null)
				return cachedData;
		List<Map<String, String>> validEntries = new ArrayList<Map<String,String>>();
		List<Map<String, String>> objs = convertToMap(reader.getWholeSheet(SHEETNAME));
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
		return (!(obj.get("id")==null) && !(obj.get("id").isEmpty()) && validCoordinates);
	}

	public Double[] getCoordinatesByIdentifier(String id) {
		for (Map<String, String> entry : getValidEntries())
			if (entry.get("id")!= null && entry.get("id").equals(id)) {
				Double lon = Double.parseDouble(entry.get("longitude"));
				Double lat = Double.parseDouble(entry.get("latitude"));
				return new Double[] {lon, lat};
			}
		return null;
		
	}

	public Map<String, Object> getMetaDataByIdentifier(String id) {
		List<String> req = Arrays.asList(requiredFields);
		for (Map<String, String> obj :getValidEntries())
			if (obj.get("id")!= null && obj.get("id").equals(id)) {
				Map<String, Object> metaDataObj = new HashMap<String, Object>();
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

}
