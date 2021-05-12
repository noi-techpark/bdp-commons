package it.bz.idm.bdp;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tempuri.xmlresponewebservice.ArrayOfXmlImAnagraficaImpiantoSensore.Sensore;
import org.tempuri.xmlresponewebservice.GetDataResult.XmlRwData;
import org.tempuri.xmlresponewebservice.GetMetadataStationResult;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;

@Component
public class DataParser {

	private static final String VEHICLE_COUNT_TYPE = "vehicle detection (count)";

	public static final String DATA_ORIGIN = "FAMAS";

	private static final String MUNICIPALITYBZ_NAMESPACE = "municipalitybz:bikecounter:";

	@Autowired
	private SoapClient client;
	
	@Value("${stationtype:BikeCounter}")
	private String stationtype;

	/**
	 *
	 * @return a map containing different types of stations
	 */
	public StationList retrieveStations() {
		StationList stations = new StationList();

		for (int num : client.getStationIdentifiers()) {
			GetMetadataStationResult metaData = client.getStationMetaData(num);
			for (Sensore sensor : metaData.getSensori().getSensore()) {
			StationDto stationDto = new StationDto();
			stationDto.setId(buildStationId(Integer.toString(num), Integer.toString(sensor.getSensorId())));
			stationDto.setName(metaData.getNome() + sensor.getDescrizione());
			stationDto.setLatitude(metaData.getLatit());
			stationDto.setLongitude(metaData.getLongit());
			stationDto.setOrigin(DATA_ORIGIN);
			stationDto.setStationType(stationtype);
			stationDto.getMetaData().put("city", metaData.getCitta());
			stationDto.getMetaData().put("phone", metaData.getTelefono());
			stationDto.getMetaData().put("notes", metaData.getNotes());
			stations.add(stationDto);
			}
		}
		return stations;
	}
	/**
	 *
	 * @return a list of simple types
	 */
	public List<DataTypeDto> retrieveDataTypes() {
		Set<DataTypeDto> types = new HashSet<DataTypeDto>();
		types.add(new DataTypeDto(VEHICLE_COUNT_TYPE, null, null, "Count"));
		return new ArrayList<DataTypeDto>(types);
	}
	public DataMapDto<RecordDtoImpl> retrieveLiveData() {
		DataMapDto<RecordDtoImpl> map = new DataMapDto<RecordDtoImpl>();
		for (int num : client.getStationIdentifiers()) {
			try {
				List<XmlRwData> currentData = client.getCurrentData(num);
				if (currentData.isEmpty())
					continue;
				for (XmlRwData rawdata : currentData) {
					GregorianCalendar calendar = rawdata.getTs().toGregorianCalendar();
					calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
					Double value = Integer.valueOf(rawdata.getTotale()).doubleValue();
					String idAsString = Integer.toString(num);
					long timestamp = calendar.getTimeInMillis();
					SimpleRecordDto dto = new SimpleRecordDto();
					dto.setTimestamp(timestamp);
					dto.setPeriod(1);
					dto.setValue(value);
					String stationDefinition = buildStationId(idAsString, Integer.toString(rawdata.getSensorId()));
					DataMapDto<RecordDtoImpl> stationMap = retrieveOrCreateStationLevel(map, stationDefinition);
					List<RecordDtoImpl> list = retrieveOrCreateDataList(VEHICLE_COUNT_TYPE, stationMap);
					list.add(dto);
				}
			} catch (IllegalStateException ie) {
				continue;
			} catch (Exception ex) {
				ex.printStackTrace();
				continue;
			}
		}
		return map;
	}
	public DataMapDto<RecordDtoImpl> retrieveHistoricData(XMLGregorianCalendar from,
			XMLGregorianCalendar to) {
		DataMapDto<RecordDtoImpl> map = new DataMapDto<RecordDtoImpl>();
		List<Integer> stationIdentifiers = client.getStationIdentifiers();
		for (int num : stationIdentifiers) {
			try {
				List<org.tempuri.xmlresponewebservice.GetDataHistoricalResult.XmlRwData> historyData = client
						.getHistoryData(num, from, to);
				TimeZone timeZone = TimeZone.getTimeZone("UTC");
				for (org.tempuri.xmlresponewebservice.GetDataHistoricalResult.XmlRwData rawdata : historyData) {
					GregorianCalendar calendar = rawdata.getTs().toGregorianCalendar();
					calendar.setTimeZone(timeZone);
					String stationIdAsString = String.valueOf(num);
					long timestamp = calendar.getTime().getTime();
					SimpleRecordDto dto = new SimpleRecordDto();
					dto.setTimestamp(timestamp);
					dto.setPeriod(1);
					dto.setValue(rawdata.getTotale());
					String stationDefinition = buildStationId(stationIdAsString, Integer.toString(rawdata.getSensorId()));
					DataMapDto<RecordDtoImpl> stationMap = retrieveOrCreateStationLevel(map, stationDefinition);
					List<RecordDtoImpl> list = retrieveOrCreateDataList(VEHICLE_COUNT_TYPE, stationMap);
					list.add(dto);
				}
			} catch (IllegalStateException ie) {
				continue;
			} catch (Exception ex) {
				ex.printStackTrace();
				continue;
			}
		}
		map.clean();
		return map;
	}
	/**
	 *
	 * @param typeDefinition
	 * @param typeLevelMap
	 * @return the list associated with given type and stationmap or new instance if not existing. It binds this list correctly than
	 */
	private List<RecordDtoImpl> retrieveOrCreateDataList(String typeDefinition,
			DataMapDto<RecordDtoImpl> typeLevelMap) {
		DataMapDto<RecordDtoImpl> currentRecordMapDto = typeLevelMap.getBranch().get(typeDefinition);
		List<RecordDtoImpl> list = currentRecordMapDto != null ? currentRecordMapDto.getData() : null;
		if (list == null) {
			list = new ArrayList<RecordDtoImpl>();
			DataMapDto<RecordDtoImpl> recordMapDto = new DataMapDto<>();
			recordMapDto.setData(list);
			typeLevelMap.getBranch().put(typeDefinition, recordMapDto);
		}
		return list;
	}

	private DataMapDto<RecordDtoImpl> retrieveOrCreateStationLevel(DataMapDto<RecordDtoImpl> data,
			String stationDefiniton) {
		DataMapDto<RecordDtoImpl> stationMap = data.getBranch().get(stationDefiniton);
		if (stationMap == null) {
			stationMap = new DataMapDto<RecordDtoImpl>();
			data.getBranch().put(stationDefiniton, stationMap);
		}
		return stationMap;
	}

	/**
	 * This method is meant to create a unique id for each station for each lane and each direction, it also contains a namespace for it's origin
	 * @param stationId
	 * @param lane
	 * @param direction
	 * @return
	 */
	private String buildStationId(String stationId, String sensorId) {
		StringBuffer buffer = null;
		if (stationId != null && !stationId.isEmpty()) {
			buffer = new StringBuffer(MUNICIPALITYBZ_NAMESPACE);
			buffer.append(stationId);
		}
		if (sensorId != null && !sensorId.isEmpty()) {
			buffer.append(":");
			buffer.append(sensorId);
		}
		
		return buffer != null ? buffer.toString() : null;
	}

}
