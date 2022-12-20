package it.bz.noi.onstreetparking;

import it.bz.idm.bdp.dto.*;
import it.bz.noi.onstreetparking.configuration.OnStreetParkingConfiguration;
import it.bz.noi.onstreetparking.dto.ParkingData;
import it.bz.noi.onstreetparking.pusher.OnStreetParkingJsonPusher;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OnStreetParkingSensorService {

	private static Logger LOG = LoggerFactory.getLogger(OnStreetParkingSensorService.class);
	private static final String STATION_DATA_SYNCRONIZATION_BLOCK = "STATION_DATA_SYNCRONIZATION_BLOCK";

	private static final String STATION_DATATYPE_FREE = "free";
	private static final String STATION_DATATYPE_OCCUPIED = "occupied";

	@Autowired
	private OnStreetParkingJsonPusher jsonPusher;
	@Autowired
	protected OnStreetParkingConfiguration onStreetParkingConfiguration;

	private Map<String, StationDto> stationCodeStationMap = null;

	public void fetchStationIfAbsent(boolean force) {
		synchronized (STATION_DATA_SYNCRONIZATION_BLOCK) {
			if (stationCodeStationMap == null || force) {
				LOG.info("fetchStationList");
				stationCodeStationMap = new HashMap<>();
				jsonPusher.fetchStations(onStreetParkingConfiguration.getStationtype(), onStreetParkingConfiguration.getOrigin()).forEach(stationDto -> stationCodeStationMap.put(stationDto.getId(), stationDto));
				setupDataType();
			}
		}
	}

	private long getMillisOfLastMeasurement(String stationCode) {
		return ((Date) jsonPusher.getDateOfLastRecord(stationCode, null, null)).getTime();
	}

	public void cleanupStationList() {
		synchronized (STATION_DATA_SYNCRONIZATION_BLOCK) {
			long now = System.currentTimeMillis();
			fetchStationIfAbsent(true);
			List<String> previousStationCode = new ArrayList<>(stationCodeStationMap.keySet());
			previousStationCode.forEach(stationCode -> {
				long secondsSinceLastMeasurement = (now - getMillisOfLastMeasurement(stationCode)) / 1000;
				if(secondsSinceLastMeasurement > onStreetParkingConfiguration.getMaxTimeSinceLastMeasurementSeconds()) {
					LOG.info("set station inactive: {}, time since last measurement: {}s)", stationCode, secondsSinceLastMeasurement);
					stationCodeStationMap.remove(stationCode);
				}
			});
			jsonPusher.syncStations(new StationList(stationCodeStationMap.values()));
		}
	}

	public void applyParkingData(ParkingData parkingData) {
		LOG.info("apply parking data message: guid={}, name={}, status={}, lastChange:{}",
			parkingData.getGuid(), parkingData.getName(), parkingData.getState(), parkingData.getLastChange());
		synchronized (STATION_DATA_SYNCRONIZATION_BLOCK) {
			fetchStationIfAbsent(false);
			StationDto stationDto = new StationDto(
				parkingData.getGuid(),
				parkingData.getName(),
				parkingData.getPosition().getLatitude(),
				parkingData.getPosition().getLongitude()
			);
			stationDto.setOrigin(onStreetParkingConfiguration.getOrigin());
			stationDto.setStationType(onStreetParkingConfiguration.getStationtype());
			stationCodeStationMap.put(stationDto.getId(), stationDto);
			jsonPusher.syncStations(new StationList(stationCodeStationMap.values()));
		}

		Integer freeValue = null;
		Integer occupiedValue = null;

		if(parkingData.getState().equals("free")) {
			freeValue = 1;
			occupiedValue = 0;
		} else if(parkingData.getState().equals("occupied")) {
			freeValue = 0;
			occupiedValue = 1;
		}

		DataMapDto<RecordDtoImpl> dataMap = new DataMapDto<>();
		dataMap.addRecord(parkingData.getGuid(), STATION_DATATYPE_FREE,
			new SimpleRecordDto(parkingData.getLastChange().toInstant().toEpochMilli(), freeValue, onStreetParkingConfiguration.getPeriod()));
		dataMap.addRecord(parkingData.getGuid(), STATION_DATATYPE_OCCUPIED,
			new SimpleRecordDto(parkingData.getLastChange().toInstant().toEpochMilli(), occupiedValue, onStreetParkingConfiguration.getPeriod()));
		jsonPusher.pushData(dataMap);
	}

	private void setupDataType() {
		LOG.info("setupDataType");
		List<DataTypeDto> dataTypeDtoList = new ArrayList<>();
			dataTypeDtoList.add(
				new DataTypeDto(STATION_DATATYPE_FREE,
					"",
					STATION_DATATYPE_FREE,
					"Instantaneous")
			);
			dataTypeDtoList.add(
				new DataTypeDto(STATION_DATATYPE_OCCUPIED,
					"",
					STATION_DATATYPE_OCCUPIED,
					"Instantaneous")
			);
		jsonPusher.syncDataTypes(dataTypeDtoList);
	}
}
