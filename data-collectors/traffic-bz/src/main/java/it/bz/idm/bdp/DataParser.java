package it.bz.idm.bdp;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import cleanroadsdatatype.cleanroadswebservices.ArrayOfXmlImAnagraficaStazioneStationType.StationType;
import cleanroadsdatatype.cleanroadswebservices.ArrayOfXmlImClassConfigXmlClassificazioneClasse.Classe;
import cleanroadsdatatype.cleanroadswebservices.ArrayOfXmlImDataTypesXmlDataTypeClassifiSpec.ClassifiSpec;
import cleanroadsdatatype.cleanroadswebservices.GetClassifConfigResult.XmlClassificazione;
import cleanroadsdatatype.cleanroadswebservices.GetDataResult.XmlRwData;
import cleanroadsdatatype.cleanroadswebservices.GetDataTypesResult.XmlDataType;
import cleanroadsdatatype.cleanroadswebservices.GetMetadataStationResult;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;

@Component
@PropertySource("classpath:/META-INF/spring/types.properties")
public class DataParser {

	private static final Integer DRIVING_DIRECTION = new Integer(0);

	public static final String DATA_ORIGIN = "FAMAS-traffic";

	private static final String MUNICIPALITYBZ_NAMESPACE = "municipalitybz:";

	@Autowired
	private SoapClient client;

	@Autowired
	private Environment environment;

	/**
	 *
	 * @return a map containing different types of stations
	 */
	public Map<String, StationList> retrieveStations() {
		Map<String, StationList> stations = new HashMap<String, StationList>();
		StationList meteostations = new StationList();
		StationList trafficStations = new StationList();
		StationList environmentStations = new StationList();

		for (int num : client.getStationIdentifiers()) {
			GetMetadataStationResult metaData = client.getStationMetaData(num);
			if (metaData.getStationTypeList() != null && !metaData.getStationTypeList().getStationType().isEmpty())
				for (StationType type : metaData.getStationTypeList().getStationType()) {
					if (type.getType() == 2) { // 2 is a weatherstation
						StationDto stationDto = new StationDto();
						stationDto.setId(MUNICIPALITYBZ_NAMESPACE + num);
						stationDto.setName(metaData.getNome());
						stationDto.setLatitude(metaData.getLatit());
						stationDto.setLongitude(metaData.getLongit());
						stationDto.setOrigin(DATA_ORIGIN);
						stationDto.setStationType(TrafficPusher.METEOSTATION_IDENTIFIER);
						meteostations.add(stationDto);
					} else if (type.getType() == 1) { // 1 is a traffic sensor
						Set<Integer> stationLanes = this.getStationLanes(num);
						StationDto stationDto = new StationDto();
						stationDto.setId(MUNICIPALITYBZ_NAMESPACE + num);
						stationDto.setName(metaData.getNome());
						stationDto.setLatitude(metaData.getLatit());
						stationDto.setLongitude(metaData.getLongit());
						stationDto.setOrigin(DATA_ORIGIN);
						stationDto.setStationType(TrafficPusher.TRAFFIC_SENSOR_IDENTIFIER);
						trafficStations.add(stationDto);
						for (Integer lane : stationLanes) { // create one station for each lane
							int i;
							for (i = 0; i < 2; i++) { // and one station for each direction
								StationDto laneDto = new StationDto();
								laneDto.setId(MUNICIPALITYBZ_NAMESPACE + num + "l" + lane + "d" + i);
								laneDto.setName(metaData.getNome() + " Lane " + lane + " Direction " + i);
								laneDto.setLatitude(metaData.getLatit());
								laneDto.setLongitude(metaData.getLongit());
								laneDto.setOrigin(DATA_ORIGIN);
								laneDto.setStationType(TrafficPusher.TRAFFIC_SENSOR_IDENTIFIER);
								trafficStations.add(laneDto);
							}
						}
					} else if (type.getType() == 3) { // 3 is environmentstation
						StationDto stationDto = new StationDto();
						stationDto.setId(MUNICIPALITYBZ_NAMESPACE + num);
						stationDto.setName(metaData.getNome());
						stationDto.setLatitude(metaData.getLatit());
						stationDto.setLongitude(metaData.getLongit());
						stationDto.setOrigin(DATA_ORIGIN);
						stationDto.setStationType(TrafficPusher.ENVIRONMENTSTATION_IDENTIFIER);
						environmentStations.add(stationDto);

					}
				}
		}
		stations.put("meteo", meteostations);
		stations.put("traffic", trafficStations);
		stations.put("environment", environmentStations);
		return stations;
	}
	/**
	 *
	 * @return a list of simple types and complex types of all typologgies of
	 *         stations divided in weather types, environment types and traffic
	 *         types
	 */
	public List<DataTypeDto> retrieveDataTypes() {
		Set<DataTypeDto> types = new HashSet<DataTypeDto>();
		for (int num : client.getStationIdentifiers()) {
			try {
				List<XmlDataType> stationDataTypes = client.getStationDataTypes(num);
				List<XmlClassificazione> stationConfig = client.getStationConfig(num);
				for (XmlDataType type : stationDataTypes) {
					if (type.getClassificazioni() == null) { // this means that there are no classifications and
						// therefore it's a simple type like air-temperature or
						// co2-polution
						DataTypeDto dto = new DataTypeDto();
						String key = String.valueOf(type.getId());
						String knownType = environment.getProperty(key);
						if (knownType != null) {
							dto.setName(knownType);
							dto.setDescription(type.getDescr());
							dto.setUnit(type.getUm());
							types.add(dto);
						}
					} else { // in this case we have a traffic classification, which means complex types like
						// speed of motorcicles, in the property file they are described like this
						// [number]_[number]
						for (ClassifiSpec spec : type.getClassificazioni().getClassifiSpec()) {
							for (XmlClassificazione classification : stationConfig) {
								if (spec.getIdGruppo() == classification.getIdGruppo()) {
									for (Classe classe : classification.getXmlClassi().getClasse()) {
										DataTypeDto dto = new DataTypeDto();
										dto.setDescription(classification.getDescr());
										String key = String
												.valueOf(classification.getIdGruppo() + "_" + classe.getNr()); //
										String knownType = environment.getProperty(key);
										if (knownType != null) {
											dto.setName(knownType);
											StringBuffer description = new StringBuffer(type.getDescr());
											description.append(" ").append(classe.getDescr()).append(" ");
											if (classe.getLowerBound() != null) {
												description.append(classe.getLowerBound()).append("-");
												description
												.append(classe.getUpperBound() != null ? classe.getUpperBound()
														: "???");

											}
											dto.setDescription(description.toString());
											dto.setUnit(type.getUm());
											types.add(dto);
										}
									}
								}
							}
						}
					}

				}
			} catch (Exception ex) {
				continue;
			}
		}
		return new ArrayList<DataTypeDto>(types);
	}
	public Map<String, DataMapDto<RecordDtoImpl>> retrieveLiveData() {
		Map<String, DataMapDto<RecordDtoImpl>> map = new HashMap<>();
		DataMapDto<RecordDtoImpl> trafficData = new DataMapDto<RecordDtoImpl>();
		DataMapDto<RecordDtoImpl> meteoData = new DataMapDto<RecordDtoImpl>();
		DataMapDto<RecordDtoImpl> environmentData = new DataMapDto<RecordDtoImpl>();
		map.put(TrafficPusher.TRAFFIC_SENSOR_IDENTIFIER, trafficData);
		map.put(TrafficPusher.METEOSTATION_IDENTIFIER, meteoData);
		map.put(TrafficPusher.ENVIRONMENTSTATION_IDENTIFIER, environmentData);
		for (int num : client.getStationIdentifiers()) {
			try {
				List<XmlDataType> stationDataTypes = client.getStationDataTypes(num);
				List<XmlRwData> currentData = client.getCurrentData(num, stationDataTypes);
				if (currentData.isEmpty())
					continue;
				for (XmlRwData rawdata : currentData) {
					GregorianCalendar calendar = rawdata.getTs().toGregorianCalendar();
					calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
					int simpleType = rawdata.getId();
					XmlDataType type = filterTypeById(stationDataTypes, simpleType);
					int acqInterv = type.getAcqInterv();
					Double value = rawdata.getValore();
					String idAsString = String.valueOf(String.valueOf(num));
					long time = calendar.getTimeInMillis();
					if (rawdata.getClassifDataList() == null && type != null && rawdata.getCorsia() != null //as long as lane and direction is not null and categorisazion is null the data is about the specific lane
							&& rawdata.getDir() != null) {
						aggregateData(trafficData, idAsString, MEASUREMENT_CONTEXT.LANE, String.valueOf(simpleType), rawdata.getCorsia(), rawdata.getDir(), acqInterv, time, value);
					} else if (rawdata.getClassifDataList() == null && type != null) {																//  if no lane is specified it means the data is of the station like air-temperature
						if (simpleType >= 38 && simpleType <= 42) {												//environment types
							aggregateData(environmentData, idAsString, MEASUREMENT_CONTEXT.STATION,
									String.valueOf(simpleType), rawdata.getCorsia(), rawdata.getDir(), acqInterv,
									time, rawdata.getValore());
						} else {																				// meteo types and potentially more :->
							aggregateData(meteoData, idAsString, MEASUREMENT_CONTEXT.STATION,
									String.valueOf(simpleType), rawdata.getCorsia(), rawdata.getDir(), acqInterv,
									time, rawdata.getValore());
						}
					}else {																						// in this case categorisazion is not null and therefore it's data about specific vehicles in this lane
						for (cleanroadsdatatype.cleanroadswebservices.GetDataResult.XmlRwData.ClassifDataList.ClassifData classif : rawdata
								.getClassifDataList().getClassifData()) {
							String typeDefinition = rawdata.getClassifDataList().getIdClassif() + "_"
									+ classif.getClasse();
							aggregateData(trafficData, idAsString, MEASUREMENT_CONTEXT.LANE_GROUP, typeDefinition,
									rawdata.getCorsia(), rawdata.getDir(), acqInterv, time, classif.getValore());
						}
					}
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
	public Map<String, DataMapDto<RecordDtoImpl>> retrieveHistoricData(XMLGregorianCalendar from,
			XMLGregorianCalendar to) {
		Map<String, DataMapDto<RecordDtoImpl>> map = new HashMap<>();
		DataMapDto<RecordDtoImpl> trafficData = new DataMapDto<RecordDtoImpl>();
		DataMapDto<RecordDtoImpl> meteoData = new DataMapDto<RecordDtoImpl>();
		DataMapDto<RecordDtoImpl> environmentData = new DataMapDto<RecordDtoImpl>();
		map.put(TrafficPusher.TRAFFIC_SENSOR_IDENTIFIER, trafficData);
		map.put(TrafficPusher.METEOSTATION_IDENTIFIER, meteoData);
		map.put(TrafficPusher.ENVIRONMENTSTATION_IDENTIFIER, environmentData);
		for (int num : client.getStationIdentifiers()) {
			List<XmlDataType> stationDataTypes = client.getStationDataTypes(num);
			try {
				List<cleanroadsdatatype.cleanroadswebservices.GetDataHistoricalResult.XmlRwData> historyData = client
						.getHistoryData(num, stationDataTypes, from, to);
				if (!historyData.isEmpty()) {
					TimeZone timeZone = TimeZone.getTimeZone("UTC");
					for (cleanroadsdatatype.cleanroadswebservices.GetDataHistoricalResult.XmlRwData rawdata : historyData) {
						GregorianCalendar calendar = rawdata.getTs().toGregorianCalendar();
						calendar.setTimeZone(timeZone);
						XmlDataType type = filterTypeById(stationDataTypes, rawdata.getId());
						int simpleType = rawdata.getId();
						String stationIdAsString = String.valueOf(num);
						long time = calendar.getTime().getTime();
						int acqInterv = type.getAcqInterv();
						if (rawdata.getClassifDataList() == null && type != null && rawdata.getCorsia() != null
								&& rawdata.getDir() != null) {													//as long as lane and direction is not null and categorisazion is null the data is about the specific lane
							aggregateData(trafficData, stationIdAsString, MEASUREMENT_CONTEXT.LANE,
									String.valueOf(simpleType), rawdata.getCorsia(), rawdata.getDir(), acqInterv, time,
									rawdata.getValore());
						} else if (rawdata.getClassifDataList() == null && type != null) {																//  if no lane is specified it means the data is of the station like air-temperature
							if (simpleType >= 38 && simpleType <= 42) {												//environment types
								aggregateData(environmentData, stationIdAsString, MEASUREMENT_CONTEXT.STATION,
										String.valueOf(simpleType), rawdata.getCorsia(), rawdata.getDir(), acqInterv,
										time, rawdata.getValore());
							} else {																				// meteo types and potentially more :->
								aggregateData(meteoData, stationIdAsString, MEASUREMENT_CONTEXT.STATION,
										String.valueOf(simpleType), rawdata.getCorsia(), rawdata.getDir(), acqInterv,
										time, rawdata.getValore());
							}
						} else {																						// in this case categorisazion is not null and therefore it's data about specific vehicles in this lane
							for (cleanroadsdatatype.cleanroadswebservices.GetDataHistoricalResult.XmlRwData.ClassifDataList.ClassifData classif : rawdata
									.getClassifDataList().getClassifData()) {

								String typeDefinition = rawdata.getClassifDataList().getIdClassif() + "_"
										+ classif.getClasse();
								aggregateData(trafficData, stationIdAsString, MEASUREMENT_CONTEXT.LANE_GROUP, typeDefinition,
										rawdata.getCorsia(), rawdata.getDir(), acqInterv, time, classif.getValore());
							}
						}
					}
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
	private Set<Integer> getStationLanes(int num) {
		Set<Integer> lanes = new HashSet<Integer>();
		List<XmlDataType> stationDataTypes = client.getStationDataTypes(num);
		for (XmlDataType type : stationDataTypes) {
			if (type.getCorsia() != null)
				lanes.add(type.getCorsia());
		}
		return lanes;

	}

	private XmlDataType filterTypeById(List<XmlDataType> stationDataTypes, int typeId) {
		for (XmlDataType type : stationDataTypes)
			if (type.getId() == typeId)
				return type;
		return null;
	}

	/**
	 *
	 * @param data
	 * @param stationId
	 * @param mContext
	 * @param typeDefinition
	 * @param lane
	 * @param direction
	 * @param acquisitionInterval
	 * @param timestamp
	 * @param value
	 * @return a map sorted on the first level by station and in the next by type
	 */
	private void aggregateData(DataMapDto<RecordDtoImpl> data, String stationId,
			MEASUREMENT_CONTEXT mContext, String typeDefinition, Integer lane, Integer direction,
			Integer acquisitionInterval, Long timestamp, Double value) {
		SimpleRecordDto dto = new SimpleRecordDto();
		dto.setTimestamp(timestamp);
		dto.setPeriod(Long.valueOf(TimeUnit.MINUTES.toSeconds(acquisitionInterval)).intValue());
		dto.setValue(value);
		String type = environment.getProperty(typeDefinition);

		switch (mContext) {
		case LANE: {
			if (type != null && lane != null && direction != null) { // single value for lane, like average speed of all
				// vehicles
				String stationDefiniton = buildStationId(stationId, lane, direction);
				DataMapDto<RecordDtoImpl> firstLevelMap = retrieveOrCreateStationLevel(data, stationDefiniton);
				List<RecordDtoImpl> list = retrieveOrCreateDataList(type, firstLevelMap);
				if (isNotZeroValueOfOppositeDrivingDirection(direction, value))
					list.add(dto);
			}
			break;
		}
		case STATION: {
			if (type != null) {
				String stationDefinition = buildStationId(stationId, null, null);
				DataMapDto<RecordDtoImpl> stationMap = retrieveOrCreateStationLevel(data, stationDefinition);
				List<RecordDtoImpl> list = retrieveOrCreateDataList(type, stationMap);
				list.add(dto);
			}
			break;
		}
		case LANE_GROUP: {
			if (type != null) {
				String stationDefiniton = buildStationId(stationId, lane, direction);
				DataMapDto<RecordDtoImpl> stationMap = retrieveOrCreateStationLevel(data, stationDefiniton);
				List<RecordDtoImpl> list = retrieveOrCreateDataList(type, stationMap);
				if (isNotZeroValueOfOppositeDrivingDirection(direction, value))
					list.add(dto);
			}

			break;
		}
		default:
			break;
		}
	}
	/**
	 *
	 * @param typeDefinition
	 * @param firstLevelMap
	 * @return the list associated with given type and stationmap or new instance if not existing. It binds this list correctly than
	 */
	private List<RecordDtoImpl> retrieveOrCreateDataList(String typeDefinition,
			DataMapDto<RecordDtoImpl> firstLevelMap) {
		DataMapDto<RecordDtoImpl> typeMapDto = firstLevelMap.getBranch().get(typeDefinition);
		List<RecordDtoImpl> list = typeMapDto != null ? typeMapDto.getData() : null;
		if (list == null) {
			list = new ArrayList<RecordDtoImpl>();
			DataMapDto<RecordDtoImpl> recordMapDto = new DataMapDto<>();
			recordMapDto.setData(list);
			firstLevelMap.getBranch().put(typeDefinition, recordMapDto);
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
	private String buildStationId(String stationId, Integer lane, Integer direction) {
		StringBuffer buffer = null;
		if (stationId != null && !stationId.isEmpty()) {
			buffer = new StringBuffer(MUNICIPALITYBZ_NAMESPACE);
			buffer.append(stationId);
			if (lane != null && direction != null && direction != null)
				buffer.append("l").append(lane).append("d").append(direction);
		}
		return buffer != null ? buffer.toString() : null;
	}

	/**
	 *
	 * @param direction
	 * @param value
	 * @return true if the number is measured of a vehicle driving in the right
	 *         direction or if the measured number is not 0
	 */
	private boolean isNotZeroValueOfOppositeDrivingDirection(Integer direction, Double value) {
		return (DRIVING_DIRECTION.equals(direction) || value != 0); // exclude data in opposite driving directions when
		// there is no vehicles
	}
}
