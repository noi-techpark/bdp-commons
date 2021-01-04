package it.bz.idm.bdp.airquality;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import it.bz.idm.bdp.airquality.dto.AQBlockDto;
import it.bz.idm.bdp.airquality.dto.AQStationDto;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.ProvenanceDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.json.NonBlockingJSONPusher;

@Service
public class DataPusher extends NonBlockingJSONPusher {

	private static final Logger log = LogManager.getLogger(DataPusher.class.getName());

	@Autowired
	private Environment env;

	private DataMapDto<RecordDtoImpl> rootMap = null;

	@Autowired
	private DataModel dataModel;

	public void setDataModel(DataModel dataModel) {
		this.dataModel = dataModel;
	}

	public void pushData() {
		pushData(this.integreenTypology, rootMap);
	}

	@Override
	public String initIntegreenTypology() {
		return "EnvironmentStation";// env.getRequiredProperty("odh.station.type");
	}

	@Override
	public <T> DataMapDto<RecordDtoImpl> mapData(T data) {

		@SuppressWarnings("unchecked")
		List<AQStationDto> stations = ((List<AQStationDto>) data);

		rootMap = new DataMapDto<RecordDtoImpl>();

		for (AQStationDto station : stations) {
			String stationID = dataModel.validStations.get(station.getStation());
			DataMapDto<RecordDtoImpl> stationMap = rootMap.upsertBranch(stationID);

			for (AQBlockDto param : station.getBlocks()) {
				for (Map.Entry<Character, Double> metric : param.getKeyValue().entrySet()) {
					try {
						String paramMetricID = "" + param.getParameterType() + metric.getKey();
						String fullName = "";
						try {
							fullName = dataModel.validParametersFull.get(paramMetricID).getName();
						} catch (NullPointerException e) {
							/*
							 * We get here, if the parameter/metric combination is not mapped in
							 * our mapping files. We can safely skip it and continue...
							 */
							continue;
						}

						DataMapDto<RecordDtoImpl> metricMap = stationMap.upsertBranch(fullName);
						List<RecordDtoImpl> values = metricMap.getData();
						SimpleRecordDto simpleRecordDto = new SimpleRecordDto(station.getTimestamp(),
								metric.getValue());
						simpleRecordDto.setPeriod(env.getRequiredProperty("odh.datatype.period", Integer.class));
						values.add(simpleRecordDto);
						// log.debug("Add new measurement for station {} and param/metric {}: {}", stationID,
						// paramMetricID, simpleRecordDto);
					} catch (Exception e) {
						log.warn("Problem during data map creation: " + e.getMessage());
						e.printStackTrace();
						/* Despite all errors, continue to insert what we got... */
					}
				}
			}
		}
		return rootMap;
	}

	@Override
	public ProvenanceDto defineProvenance() {
		return new ProvenanceDto(null, env.getProperty("provenance_name"), env.getProperty("provenance_version"), env.getProperty("odh.station.origin"));
	}

	/*
	 * XXX This method is not used currently, we will use it as soon as a three-level data type storage has been
	 * implemented within the bdp-core package.
	 */
	// public <T> DataMapDto<RecordDtoImpl> mapData3Levels(T data) {
	// @SuppressWarnings("unchecked")
	// List<AQStationDto> stations = ((List<AQStationDto>) data);
	// rootMap = new DataMapDto<RecordDtoImpl>();
	//
	// for (AQStationDto station : stations) {
	// String stationID = dataModel.validStations.get(station.getStation());
	// DataMapDto<RecordDtoImpl> stationMap = rootMap.upsertBranch(stationID);
	//
	// for (AQBlockDto param : station.getBlocks()) {
	// String paramName = dataModel.validParameters.get(param.getParameterType());
	// DataMapDto<RecordDtoImpl> parameterMap = stationMap.upsertBranch(paramName);
	//
	// for (Entry<Character, Double> metric : param.getKeyValue().entrySet()) {
	// String metricName = dataModel.validMetrics.get(metric.getKey());
	// DataMapDto<RecordDtoImpl> metricMap = parameterMap.upsertBranch(metricName);
	// List<RecordDtoImpl> values = metricMap.getData();
	// SimpleRecordDto simpleRecordDto = new SimpleRecordDto(station.getTimestamp(), metric.getValue());
	// simpleRecordDto.setPeriod(PropertyTools.getInt("odh.datatypes.period"));
	// values.add(simpleRecordDto);
	// log.debug("Add new measurement for station {}, param {} and metric {}: {}", stationID, paramName,
	// metricName, simpleRecordDto);
	// }
	// }
	// }
	// return rootMap;
	// }
}
