package it.bz.odh.spreadsheets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class DataMappingUtil {

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

}
