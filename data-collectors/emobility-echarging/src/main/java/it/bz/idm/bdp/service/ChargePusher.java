package it.bz.idm.bdp.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.ProvenanceDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;
import it.bz.idm.bdp.json.NonBlockingJSONPusher;
import it.bz.idm.bdp.service.dto.ChargerDtoV2;
import it.bz.idm.bdp.service.dto.ChargingPointsDtoV2;

@Service
public class ChargePusher extends NonBlockingJSONPusher {

	private static final Logger LOG = LoggerFactory.getLogger(ChargePusher.class);

	private static final String E_CHARGING_PLUG_TYPOLOGY = "EChargingPlug";
	private static final String ORIGIN_KEY = "app_dataOrigin";

	@Autowired
	private Environment env;

	@Override
	public String initIntegreenTypology() {
		return "EChargingStation";
	}

	private static final List<DataTypeDto> EMOBILTYTYPES = new ArrayList<DataTypeDto>() {
		private static final long serialVersionUID = 1L;

		{
			add(new DataTypeDto(
					"number-available",
					"",
					"number of available vehicles / charging points", "Instantaneous"));
			add(new DataTypeDto(
					"echarging-plug-status",
					"",
					"the state can either be 0, which means that the plug is currently not available, or it can be 1 which means it is",
					""));
		}
	};

	@Override
	public <T> DataMapDto<RecordDtoImpl> mapData(T rawData) {
		if (rawData == null)
			return null;

		@SuppressWarnings("unchecked")
		List<ChargerDtoV2> data = (List<ChargerDtoV2>) rawData;
		DataMapDto<RecordDtoImpl> map = new DataMapDto<>();
		Date now = new Date();
		Integer period = env.getProperty("app_period", Integer.class);
		String plugStatusAvailable = env.getRequiredProperty("plug.status.available");
		for (ChargerDtoV2 dto : data) {

			if ("REMOVED".equals(dto.getState()))
				continue;

			DataMapDto<RecordDtoImpl> recordsByType = new DataMapDto<>();
			Integer availableStations = 0;
			for (ChargingPointsDtoV2 point : dto.getChargingPoints()) {
				if (plugStatusAvailable.equals(point.getState()))
					availableStations++;
			}
			List<RecordDtoImpl> records = new ArrayList<>();
			SimpleRecordDto rec = new SimpleRecordDto(now.getTime(), availableStations.doubleValue());
			rec.setPeriod(period);
			records.add(rec);
			DataMapDto<RecordDtoImpl> dataSet = new DataMapDto<>(records);
			recordsByType.getBranch().put(DataTypeDto.NUMBER_AVAILABE, dataSet);
			map.getBranch().put(dto.getId(), recordsByType);
		}
		return map;
	}

	public StationList map2bdp(List<ChargerDtoV2> fetchedStations) {
		StationList stations = new StationList();
		String origin = env.getProperty(ORIGIN_KEY);
		for (ChargerDtoV2 dto : fetchedStations) {

			if ("REMOVED".equals(dto.getState()))
				continue;

			StationDto s = new StationDto();
			s.setId(dto.getId());
			s.setLongitude(dto.getLongitude());
			s.setLatitude(dto.getLatitude());
			s.setName(getName(dto));
			s.getMetaData().put("city", dto.getPosition().getCity());
			s.getMetaData().put("provider", dto.getProvider());
			s.getMetaData().put("capacity", dto.getChargingPoints().size());
			s.getMetaData().put("state", dto.getState());
			s.getMetaData().put("accessInfo", dto.getAccessInfo());
			s.getMetaData().put("flashInfo", dto.getFlashInfo());
			s.getMetaData().put("locationServiceInfo", dto.getLocationServiceInfo());
			s.getMetaData().put("paymentInfo", dto.getPaymentInfo());
			s.getMetaData().put("address", dto.getAddress());
			s.getMetaData().put("reservable", dto.getIsReservable());
			s.getMetaData().put("accessType", dto.getAccessType());
			s.getMetaData().put("categories", dto.getCategories());
			s.setOrigin(origin);
			s.setStationType(this.integreenTypology);
			if (s.isValid()) {
				stations.add(s);
			} else {
				LOG.warn("Invalid StationDto (chargers) skipped: {}", s);
			}
		}
		return stations;
	}

	public StationList mapPlugs2Bdp(List<ChargerDtoV2> fetchedStations) {
		if (fetchedStations == null)
			return null;
		StationList stations = new StationList();
		String origin = env.getProperty(ORIGIN_KEY);
		for (ChargerDtoV2 dto : fetchedStations) {
			for (ChargingPointsDtoV2 point : dto.getChargingPoints()) {
				StationDto s = new StationDto();
				s.setId(dto.getId() + "-" + point.getOutlets().get(0).getId());
				s.setLongitude(dto.getLongitude());
				s.setLatitude(dto.getLatitude());
				s.setName(getName(dto) + "-" + point.getId());
				s.setParentStation(dto.getCode());
				s.getMetaData().put("outlets", point.getOutlets());
				s.setOrigin(origin);
				s.setStationType(E_CHARGING_PLUG_TYPOLOGY);
				if (s.isValid()) {
					stations.add(s);
				} else {
					LOG.warn("Invalid StationDto (plugs) skipped: {}", s);
				}
			}
		}
		return stations;
	}

	public DataMapDto<RecordDtoImpl> mapPlugData2Bdp(List<ChargerDtoV2> data) {
		if (data == null)
			return null;
		Integer period = env.getProperty("app_period", Integer.class);

		DataMapDto<RecordDtoImpl> map = new DataMapDto<>();
		Date now = new Date();
		for (ChargerDtoV2 dto : data) {
			for (ChargingPointsDtoV2 point : dto.getChargingPoints()) {
				if (point.getState() != null) {
					DataMapDto<RecordDtoImpl> recordsByType = new DataMapDto<>();
					List<RecordDtoImpl> records = new ArrayList<>();
					SimpleRecordDto rec = new SimpleRecordDto();
					rec.setTimestamp(now.getTime());
					rec.setValue(point.getState().equals("AVAILABLE") ? 1. : 0.);
					rec.setPeriod(period);
					records.add(rec);
					recordsByType.getBranch().put("echarging-plug-status", new DataMapDto<>(records));
					map.getBranch().put(dto.getId() + "-" + point.getOutlets().get(0).getId(), recordsByType);
				}
			}
		}
		return map;
	}

	public List<DataTypeDto> getDataTypes() {
		return EMOBILTYTYPES;
	}

	@Override
	public ProvenanceDto defineProvenance() {
		return new ProvenanceDto(null, env.getProperty("provenance_name"), env.getProperty("provenance_version"),
				env.getProperty(ORIGIN_KEY));
	}

	/**
	 * Returns the id as name, if name is null or empty
	 * @param dto
	 * @return
	 */
	private String getName(ChargerDtoV2 dto) {
		return (dto.getName() == null || dto.getName().isEmpty()) ? dto.getId()
				: dto.getName();
	}
}
