package it.bz.idm.bdp.spreadsheets;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.tika.langdetect.OptimaizeLangDetector;
import org.apache.tika.language.detect.LanguageDetector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.ProvenanceDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.json.JSONPusher;
import it.bz.idm.bdp.util.LocationLookup;
import it.bz.idm.bdp.util.NominatimLocationLookupUtil;

@Component
public class ODHClient extends JSONPusher{

	@Value(value="${stationtype}")
	private String stationtype;

	private LocationLookup lookUpUtil = new NominatimLocationLookupUtil();

	@Value(value="${suportedLanguages}")
	private String[] supportedLanguages;
	private LanguageDetector detector;
	private NumberFormat numberFormatter = NumberFormat.getInstance(Locale.US);

	@Value("${spreadsheetId}")
	private String origin;

	@Value("${headers.nameId}")
	private String nameId;

	@Value("${headers.addressId}")
	private String addressId;

	@Value("${headers.longitudeId}")
	private String longitudeId;

	@Value("${headers.latitudeId}")
	private String latitudeId;

	@Value("${composite.unique.key}")
	private String[] uniqueIdFields;

	public Set<String> excludeFromMetaData = new HashSet<>();

	@PostConstruct
	public void initMetadata() throws IOException {
		excludeFromMetaData.add(longitudeId);
		excludeFromMetaData.add(latitudeId);

		detector = new OptimaizeLangDetector().loadModels(new HashSet<>(Arrays.asList(supportedLanguages)));
		detector.setShortText(true);
	}

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
		return new ProvenanceDto(null,"Spreadsheets","0.1.0-SNAPSHOT","IDM");
	}

	/**
	 * @param headerMapping mapping of column index and column name
	 * @param row all cell values of one row
	 * @return a {@link StationDto} containing all information of this row
	 */
	public StationDto mapStation(Map<String, Short> headerMapping, List<Object> row) {
		StationDto dto = new StationDto();
		Short nameIndex = headerMapping.get(nameId.toLowerCase());
		Short longIndex = headerMapping.get(longitudeId);
		Short latIndex = headerMapping.get(latitudeId);
		Integer rowSize = row.size();
		if (nameIndex != null && rowSize > nameIndex ) {
			Object object = row.get(headerMapping.get(nameId.toLowerCase()));
			if (object != null && !object.toString().isEmpty())
				dto.setName(object.toString().trim().replace("\n", " ").replace("\r", "").replaceAll(" +", " "));
		}
		if (longIndex != null && latIndex != null && rowSize > longIndex && rowSize > latIndex) {
			try {
				String longString = row.get(longIndex).toString();
				String latString = row.get(latIndex).toString();
				if (!longString.isEmpty() || !latString.isEmpty()) {
					dto.setLongitude(numberFormatter.parse(longString).doubleValue());
					dto.setLatitude(numberFormatter.parse(latString).doubleValue());
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		Map<String, Object> metaData = new HashMap<>();
		for (Map.Entry<String, Short> entry : headerMapping.entrySet()) {
			Object value = null;
			if (row.size() > entry.getValue())
				value = row.get(entry.getValue());
			if (!excludeFromMetaData.contains(entry.getKey()) && value != null) {
				String text = value.toString().trim().replace("\n", " ").replace("\r", "").replaceAll(" +", " ");
				if (text!=null && !text.isEmpty()) {
					if (entry.getKey().length()>0)
						metaData.put(entry.getKey(), text != null ? text : "");
				}
			}
		}
		dto.setMetaData(metaData);
		dto.setOrigin(origin);
		dto.setStationType(this.getIntegreenTypology());
		dto.setId(generateUniqueId(dto));
		return dto;
	}

	private String generateUniqueId(StationDto dto) {
		StringBuffer uniqueId = new StringBuffer();
		uniqueId.append(dto.getOrigin()).append(":");
		for(String idField:uniqueIdFields) {
			String value= dto.getMetaData().get(idField).toString();
			if (value!=null && !value.isEmpty()) {
				uniqueId.append(value);
			}
		}
		return uniqueId.toString().replaceAll("\\s+","");
	}

	/**
	 * @param text
	 * @return mapping of language with all recognized sentences in that language
	 */
	private Map<String, String> mapTextToLanguage(String text) {
		Map<String,String> langMap = new HashMap<>();
		for (String sentence : text.split("(?<!\\w\\.\\w.)(?<![A-Z][a-z]\\.)(?<=\\.|\\?)\\s")) {
			String lang = detector.detect(sentence).getLanguage();
			langMap.compute(lang, (k, v) -> (v == null) ? sentence : v +" "+ sentence);
		}
		return langMap;
	}

	/**
	 * Uses nominatim to guess the coordinates by a address
	 * @param dto to guess the position off
	 */
	public void guessPositionByAddress(StationDto dto) {
		Object addressObject = dto.getMetaData().get(addressId);
		if (addressObject != null && !addressObject.toString().isEmpty()) {
			Double[] coordinates = lookUpUtil.lookupCoordinates(addressObject.toString());
			if (coordinates[0] != null && coordinates[1] != null) {
				dto.setLongitude(coordinates[0]);
				dto.setLatitude(coordinates[1]);
			}
		}

	}

	/**
	 * @param metaData map description column to different languages
	 */
	public void guessLanguages(Map<String,Object> metaData) {
			Object object = metaData.get("description");
			if (object !=null) {
				Map<String, String> textMap = mapTextToLanguage(object.toString());
				metaData.put("description", !textMap.isEmpty() ? textMap : "");
			}

	}

	/**
	 * handle multiple languages in multiple columns
	 * @param metaData current metadata of {@link StationDto}
	 * @param headerMapping mapping of column index and column name
	 */
	public void mergeTranslations(Map<String, Object> metaData,Map<String, Short> headerMapping) {
		for (Map.Entry<String, Short> entry : headerMapping.entrySet()) {
			String[] split = entry.getKey().split(":");
			Locale locale = null;
			if (split.length<2)
				continue;
			try {
				locale = LocaleUtils.toLocale(split[0]);
				Object object = metaData.get(split[1]);
				String content = metaData.get(entry.getKey()).toString();
				if (content == null || content.isEmpty())
					continue;
				if (object instanceof Map) {
					Map langMap = (Map) object;
					langMap.put(split[0], content);
				}else if (object instanceof String) {
					Map<String, String> langMap = mapTextToLanguage(object.toString());
					langMap.put(split[0], content);
					metaData.put(split[1], langMap);
				}else if (object == null) {
					Map<String,String> langMap = new HashMap<>();
					langMap.put(split[0],content);
					metaData.put(split[1], langMap);
				}
				metaData.remove(entry.getKey());
			}
			catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}

	}


}
