package it.bz.odh.trafficprovbz;

import com.jayway.jsonpath.JsonPath;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.odh.trafficprovbz.dto.AggregatedDataDto;
import it.bz.odh.trafficprovbz.dto.MetadataDto;
import it.bz.odh.trafficprovbz.dto.PassagesDataDto;
import net.minidev.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Parser {

	private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");


	public static StationDto createStation(MetadataDto metadataDto, JSONObject otherFields, LinkedHashMap<String, String> lane, LinkedHashMap<String, String> classificationSchema) {
		Double lat = JsonPath.read(otherFields, "$.GeoInfo.Latitudine");
		Double lon = JsonPath.read(otherFields, "$.GeoInfo.Longitudine");

		metadataDto.setOtherField("SchemaDiClassificazione", classificationSchema);
		String description = JsonPath.read(lane, "$.Descrizione");
		String stationName = metadataDto.getName() + ":" + description;
		return new StationDto(metadataDto.getId(), stationName, lat, lon);
	}

	public static SimpleRecordDto createTrafficMeasurement(AggregatedDataDto aggregatedDataDto, Integer period) throws ParseException {
		Map<String, Object> aggregatedDataMap = new HashMap<>();
		aggregatedDataMap.put("total-transits", aggregatedDataDto.getTotalTransits());
		JSONObject otherFields = new JSONObject(aggregatedDataDto.getOtherFields());
		if (otherFields.containsKey("TotaliPerClasseVeicolare")) {
			LinkedHashMap<String, Integer> classes = JsonPath.read(otherFields, "$.TotaliPerClasseVeicolare");
			//Set<String> keys = classes.keySet();
			for (Map.Entry<String, Integer> entry : classes.entrySet()) {
				switch (entry.getKey()) {
					case "1":
						aggregatedDataMap.put("number-of-motorcycles", entry.getValue());
						break;
					case "2":
						aggregatedDataMap.put("number-of-cars", entry.getValue());
						break;
					case "3":
						aggregatedDataMap.put("number-of-cars-and-minivans-with-trailer", entry.getValue());
						break;
					case "4":
						aggregatedDataMap.put("number-of-small-trucks-and-vans", entry.getValue());
						break;
					case "5":
						aggregatedDataMap.put("number-of-medium-sized-trucks", entry.getValue());
						break;
					case "6":
						aggregatedDataMap.put("number-of-big-trucks", entry.getValue());
						break;
					case "7":
						aggregatedDataMap.put("number-of-articulated-trucks", entry.getValue());
						break;
					case "8":
						aggregatedDataMap.put("number-of-articulated-lorries", entry.getValue());
						break;
					case "9":
						aggregatedDataMap.put("number-of-busses", entry.getValue());
						break;
					case "10":
						aggregatedDataMap.put("number-of-unclassified-vehicles", entry.getValue());
						break;
				}
			}
			aggregatedDataMap.put("average-vehicle-speed", aggregatedDataDto.getAverageVehicleSpeed());
			aggregatedDataMap.put("headway", aggregatedDataDto.getHeadway());
			aggregatedDataMap.put("headway-variance", aggregatedDataDto.getHeadwayVariance());
			aggregatedDataMap.put("gap", aggregatedDataDto.getGap());
			aggregatedDataMap.put("gap-variance", aggregatedDataDto.getGapVariance());
		}

		Long timestamp = formatter.parse(aggregatedDataDto.getDate()).getTime();
		SimpleRecordDto simpleRecordDto = new SimpleRecordDto(timestamp, aggregatedDataMap, period);
		simpleRecordDto.setCreated_on(new Date().getTime());
		return simpleRecordDto;
	}

	public static SimpleRecordDto createBluetoothMeasurement(PassagesDataDto passagesDataDto, Integer period) throws ParseException {
		Long timestamp = formatter.parse(passagesDataDto.getDate()).getTime();
		return new SimpleRecordDto(timestamp, passagesDataDto.getIdVehicle(), period);
	}
}
