// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;

@Component
public class ParkingMeranoClient {

	private static final int PERIOD = 300;
	private static final String OCCUPIED_TYPE = "occupied";
	private RestTemplate restTemplate;
	private String endpoint;
	private final static String ORIGIN = "ParkingMeran";
	public final static String ID_NAME_SPACE = "Me:";
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	@Autowired
	public ParkingMeranoClient(@Value("${parking_mearno_endpoint}") String endpoint) {
		restTemplate = new RestTemplate();
		this.endpoint = endpoint;
		format.setTimeZone(TimeZone.getTimeZone("Europe/Rome"));
	}

	public ParkingMeranoStationDto[] getParkingStations() {
		ParkingMeranoStationDto[] ret = null;
		try {
			ret = restTemplate.getForObject(endpoint, ParkingMeranoStationDto[].class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static StationDto[] convert(ParkingMeranoStationDto[] dtos) {
		StationDto[] ret = null;
		if (dtos != null && dtos.length > 0) {
			ret = new StationDto[dtos.length];
			for (int i = 0; i < dtos.length; i++) {
				ret[i] = new StationDto();
				ret[i].setId(ID_NAME_SPACE + dtos[i].getAreaName());
				ret[i].setName(dtos[i].getAreaName());
				ret[i].setOrigin(ORIGIN);
				ret[i].getMetaData().put("capacity", dtos[i].getTotalParkingSpaces());
			}
		}
		return ret;
	}

	public void insertDataInto(DataMapDto<RecordDtoImpl> sMap) {
		ParkingMeranoStationDto[] stationDtos = getParkingStations();
		if (stationDtos != null)
			for (ParkingMeranoStationDto e : stationDtos) {
				DataMapDto<RecordDtoImpl> dMap = new DataMapDto<>();
				DataMapDto<RecordDtoImpl> tMap = new DataMapDto<>();
				List<RecordDtoImpl> records = new ArrayList<RecordDtoImpl>();
				SimpleRecordDto record = new SimpleRecordDto();
				record.setValue(e.getTotalParkingSpaces() - e.getFreeParkingSpaces());
				Date date;
				try {
					date = format.parse(e.getCurrentDateTime());
					record.setTimestamp(date.getTime());
					record.setPeriod(PERIOD);
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
				records.add(record);
				dMap.setData(records);
				if (tMap.getBranch().get(OCCUPIED_TYPE) == null)
					tMap.getBranch().put(OCCUPIED_TYPE, dMap);

				String stationKey = "me:" + e.getAreaName().toLowerCase().replaceAll("\\s+", "");
				if (sMap.getBranch().get(stationKey) == null)
					sMap.getBranch().put(stationKey, tMap);
			}
	}

	public void insertParkingMetaDataInto(List<StationDto> stations) {
		ParkingMeranoStationDto[] stationDtos = getParkingStations();
		if (stationDtos != null)
			for (ParkingMeranoStationDto dto : stationDtos) {
				StationDto stationDto = new StationDto();
				stationDto.setId("me:" + dto.getAreaName().toLowerCase().replaceAll("\\s+", ""));
				stationDto.setName(dto.getAreaName());
				stationDto.getMetaData().put("capacity", dto.getTotalParkingSpaces());
				stationDto.getMetaData().put("municipality", "Meran - Merano");
				stationDto.setOrigin("Municipality Merano");
				stations.add(stationDto);
			}
	}
}
