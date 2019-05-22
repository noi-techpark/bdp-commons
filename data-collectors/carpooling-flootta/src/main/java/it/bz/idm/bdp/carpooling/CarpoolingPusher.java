package it.bz.idm.bdp.carpooling;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import it.bz.idm.bdp.carpooling.dto.generated.JServices;
import it.bz.idm.bdp.carpooling.dto.generated.Stats;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.json.JSONPusher;

@Component
public class CarpoolingPusher extends JSONPusher {

	private static final Integer DEFAULT_PERIOD = 3600;
	private static final String INNOVIE_STATION_IDENTIFIER = "carpooling:innovie";

	private static DataTypeDto co2 		= new DataTypeDto("avoided-co2","kg","avoided carbon dioxide consumption","Instantaneous"),
			registeredUsers = new DataTypeDto("carpooling-users",null,"number of registered users in the carpooling system","Instantaneous"),
			confirmedTrips  = new DataTypeDto("carpooling-trips",null,"Number of car pooling trips","Instantaneous"),
			drivers			= new DataTypeDto("carpooling-drivers",null,"Number of drivers in the car pooling system","Instantaneous"),
			passengers		= new DataTypeDto("carpooling-passenger",null,"Number of passenger in the car pooling system","Instantaneous"),
			distance		= new DataTypeDto("traveled-distance","km","Number of km traveled","Instantaneous");

	@Override
	public String initIntegreenTypology() {
		return "CarpoolingHub";
	}

	@Override
	public <T> DataMapDto<RecordDtoImpl> mapData(T data) {
		JServices response = (JServices) data;
		DataMapDto<RecordDtoImpl> dataMap = new DataMapDto<RecordDtoImpl>();
		DataMapDto<RecordDtoImpl> typeMap = new DataMapDto<RecordDtoImpl>();
		Stats stats = response.getServices().getStats();
		Long now = new Date().getTime();
		DataMapDto<RecordDtoImpl> c = new DataMapDto<>(), ru = new DataMapDto<>(), ct = new DataMapDto<>(), d = new DataMapDto<>(),p = new DataMapDto<>(), dis = new DataMapDto<>();
		List<RecordDtoImpl> cRecords = new ArrayList<>();
		cRecords.add(new SimpleRecordDto(now,stats.getCo2(),DEFAULT_PERIOD));
		c.setData(cRecords);
		typeMap.getBranch().put(co2.getName(), c);
		List<RecordDtoImpl> ruRecords = new ArrayList<>();
		ruRecords.add(new SimpleRecordDto(now,stats.getRegisteredUsers(),DEFAULT_PERIOD));
		ru.setData(ruRecords);
		typeMap.getBranch().put(registeredUsers.getName(), ru);
		List<RecordDtoImpl> ctRecords = new ArrayList<>();
		ctRecords.add(new SimpleRecordDto(now,stats.getConfirmedTrips(),DEFAULT_PERIOD));
		ct.setData(ctRecords);
		typeMap.getBranch().put(confirmedTrips.getName(), ct);
		List<RecordDtoImpl> dRecords = new ArrayList<>();
		dRecords.add(new SimpleRecordDto(now,stats.getDrivers(),DEFAULT_PERIOD));
		d.setData(dRecords);
		typeMap.getBranch().put(drivers.getName(), d);
		List<RecordDtoImpl> pRecords = new ArrayList<>();
		pRecords.add(new SimpleRecordDto(now,stats.getPassengers(),DEFAULT_PERIOD));
		p.setData(pRecords);
		typeMap.getBranch().put(passengers.getName(), p);
		List<RecordDtoImpl> disRecords = new ArrayList<>();
		disRecords.add(new SimpleRecordDto(now,stats.getDistance(),DEFAULT_PERIOD));
		dis.setData(disRecords);
		typeMap.getBranch().put(distance.getName(), dis);

		dataMap.getBranch().put(INNOVIE_STATION_IDENTIFIER, typeMap);

		return dataMap;
	}

}
