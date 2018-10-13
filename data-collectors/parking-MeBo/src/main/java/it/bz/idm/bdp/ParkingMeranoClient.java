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
import it.bz.idm.bdp.dto.parking.ParkingStationDto;

@Component
public class ParkingMeranoClient {

	private RestTemplate restTemplate;
	private String endpoint;
	private final static String ORIGIN = "ParkingMeran";
	public final static String ID_NAME_SPACE = "Me:";
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	@Autowired
	public ParkingMeranoClient(@Value("${parking_mearno.endpoint}") String endpoint) {
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

	public static ParkingStationDto[] convert(ParkingMeranoStationDto[] dtos) {
		ParkingStationDto[] ret = null;
		if (dtos != null && dtos.length > 0) {
			ret = new ParkingStationDto[dtos.length];
			for (int i = 0; i < dtos.length; i++) {
				ret[i] = new ParkingStationDto();
				ret[i].setId(ID_NAME_SPACE + dtos[i].getAreaName());
				ret[i].setName(dtos[i].getAreaName());
				ret[i].setOrigin(ORIGIN);
				ret[i].setSlots(dtos[i].getTotalParkingSpaces());
			}
		}
		return ret;
	}

	public void insertDataInto(DataMapDto<RecordDtoImpl> sMap) {
		for (ParkingMeranoStationDto e : this.getParkingStations()) {
			DataMapDto<RecordDtoImpl> dMap = new DataMapDto<>();
			List<RecordDtoImpl> records = new ArrayList<RecordDtoImpl>();
			SimpleRecordDto record = new SimpleRecordDto();
			record.setValue(e.getFreeParkingSpaces());
			Date date;
			try {
				date = format.parse(e.getCurrentDateTime());
				record.setTimestamp(date.getTime());
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
			records.add(record);
			dMap.setData(records);
			sMap.getBranch().put("me:"+e.getAreaName().toLowerCase().replaceAll("\\s+",""), dMap);
		}
	}
	public void insertParkingMetaDataInto(List<StationDto> stations) {
		ParkingMeranoStationDto[] parkingStations = this.getParkingStations();
		for (ParkingMeranoStationDto dto:parkingStations) {
			ParkingStationDto stationDto = new ParkingStationDto();
			stationDto.setId("me:"+dto.getAreaName().toLowerCase().replaceAll("\\s+",""));
			stationDto.setName(dto.getAreaName());
			stationDto.setSlots(dto.getTotalParkingSpaces());
			stationDto.setOrigin("Municipality Merano");
			stations.add(stationDto);
		}
	}
}