package it.bz.noi.a22.vms;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Component;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;

import static net.logstash.logback.argument.StructuredArguments.v;

@Component
@Configuration
@PropertySources({
	@PropertySource("classpath:it/bz/noi/a22/vms/a22connector.properties"),
	@PropertySource("classpath:it/bz/noi/a22/vms/a22sign.properties"),
})
public class MainA22Sign {
	private static final Logger LOG = LoggerFactory.getLogger(MainA22Sign.class);

	@Value("${a22url}")
	private String a22ConnectorURL;

	@Value("${a22user}")
	private String a22ConnectorUsr;

	@Value("${a22password}")
	private String a22ConnectorPwd;

	@Value("${scanWindowSeconds}")
	private long scanWindowSeconds;

	private final A22Properties datatypesProperties;
	private final A22Properties a22stationProperties;
	private HashMap<String, Long> signIdLastTimestampMap;

	@Autowired
	private A22SignJSONPusher pusher;
	private StreetSignalsImporter signalsUtil = new StreetSignalsImporter();

	public MainA22Sign() {
		this.datatypesProperties = new A22Properties("a22vmsdatatypes.properties");
		this.a22stationProperties = new A22Properties("a22sign.properties");
	}

	public void execute() {
		long startTime = System.currentTimeMillis();
		try {
			LOG.info("Start A22SignMain");

			long nowSeconds = System.currentTimeMillis() / 1000;

			Connector a22Service = setupA22ServiceConnector();

			setupDataType(pusher);

			readLastTimestampsForAllSigns(pusher);

			StationList stationList = new StationList();
			DataMapDto<RecordDtoImpl> esposizioniDataMapDto = new DataMapDto<>();
			DataMapDto<RecordDtoImpl> statoDataMapDto = new DataMapDto<>();

			List<HashMap<String, String>> signs = a22Service.getSigns();
			LOG.info("got " + signs.size() + " signs");
			for (int i = 0; i < signs.size(); i++) {
				HashMap<String, String> sign = signs.get(i);
				String sign_id = sign.get("id");
				LOG.debug("sign_id: " + sign_id);

				try {

					String sign_descr = sign.get("descr");
					String road = sign.get("road");
					String direction_id = sign.get("direction_id");
					String pmv_type = sign.get("pmv_type");
					String segment_start = sign.get("segment_start");
					String segment_end = sign.get("segment_end");
					String position_m = sign.get("position_m");
					double sign_lat = Double.parseDouble(sign.get("lat"));
					double sign_lon = Double.parseDouble(sign.get("long"));

					long searchEventFrom = getLastTimestampOfSignInSeconds(pusher, road + ":" + sign_id);

					while (searchEventFrom < nowSeconds) {
						long searchEventTo = searchEventFrom + scanWindowSeconds;
						List<HashMap<String, Object>> events = a22Service.getEvents(
							searchEventFrom,
							searchEventTo < nowSeconds ? searchEventTo : nowSeconds,
							Long.parseLong(sign_id));
						LOG.info(String.format("Sign %04d of %04d: Got %04d events", i + 1, signs.size(), events.size()));
						for (HashMap<String, Object> event : events) {
							String event_timestamp = (String) event.get("timestamp");

							HashMap<String, SimpleRecordDto> esposizioneByComponentId = new HashMap<>();
							HashMap<String, SimpleRecordDto> statoByComponentId = new HashMap<>();

							@SuppressWarnings("unchecked")
							List<HashMap<String, Object>> components_pages = (ArrayList<HashMap<String, Object>>) event.get("component");
							for (HashMap<String, Object> component_page : components_pages) {
								String component_id = (String) component_page.get("component_id");
								String virtualStationId = road + ":" + sign_id + ":" + component_id;

								// esposizione
								String normalizedEsposizione = normalizeData(component_page.get("data").toString());
								SimpleRecordDto esposizione = esposizioneByComponentId.get(component_id);
								if (esposizione == null) {
									esposizione = new SimpleRecordDto(Long.parseLong(event_timestamp) * 1000, normalizedEsposizione, 1);
									esposizioniDataMapDto.addRecord(virtualStationId, datatypesProperties.getProperty("a22vms.datatype.esposizione.key"), esposizione);
									esposizioneByComponentId.put(component_id, esposizione);
								} else {
									concatenateValues(esposizione, normalizedEsposizione);
								}

								// stato
								String normalizedStato = normalizeData(component_page.get("status").toString());
								SimpleRecordDto stato = statoByComponentId.get(component_id);
								if (stato == null) {
									stato = new SimpleRecordDto(Long.parseLong(event_timestamp) * 1000, normalizedStato, 1);
									statoDataMapDto.addRecord(virtualStationId, datatypesProperties.getProperty("a22vms.datatype.stato.key"), stato);
									statoByComponentId.put(component_id, stato);
								} else  {
									concatenateValues(stato, normalizedStato);
								}

								// check when virtualStation alredy exists
								boolean exists = stationList.stream()
									.anyMatch((StationDto station) -> station.getId().equals(virtualStationId));
								if (!exists) {
									String virtualStationIdName = sign_descr + " - component:" + component_id;
									StationDto station = new StationDto(virtualStationId, virtualStationIdName, sign_lat,
										sign_lon);
									station.getMetaData().put("pmv_type", pmv_type);
									station.setOrigin(a22stationProperties.getProperty("origin")); // 2019-06-26 d@vide.bz: required to make fetchStations work!
									station.setStationType(a22stationProperties.getProperty("stationtype"));
									// add other metadata
									station.getMetaData().put("direction_id", direction_id);
									station.getMetaData().put("segment_start", segment_start);
									station.getMetaData().put("segment_end", segment_end);
									station.getMetaData().put("position_m", position_m);
									stationList.add(station);
								}

							}
						}
						searchEventFrom += scanWindowSeconds + 1;
					}
				} catch (Exception e) {
					LOG.warn(
						"ERROR while processing sign #{} with ID {} and exception '{}'... Skipping!",
						i + 1,
						sign_id,
						e.getMessage(),
						v("sign", sign),
						v("stacktrace", Arrays.toString(e.getStackTrace()))
					);
				}
			}
			pusher.syncStations(stationList);
			pusher.pushData(esposizioniDataMapDto);
			pusher.pushData(statoDataMapDto);
		} catch (Exception e) {
			LOG.error("ERROR while pushing data: {}", e.getMessage(), v("stacktrace", Arrays.toString(e.getStackTrace())));
		} finally {
			long stopTime = System.currentTimeMillis();
			LOG.debug("elaboration time (millis): " + (stopTime - startTime));
		}
	}

	private static String normalizeData(String data) {
		// Remove leading and trailing whitespace characters
		String normalizedData = data.trim();
		// 2019-06-26 d@vide.bz: replace multiple internal spaces with one space
		normalizedData = normalizedData.replaceAll("\\s+", " ");
		// We need to store also "empty" data cells, to avoid gaps in time series
		if (normalizedData.isEmpty())
			return " ";
		return normalizedData;
	}

	public static void concatenateValues(SimpleRecordDto rec, String newValue) {
		String cleanValue = normalizeData(rec.getValue().toString());
		newValue = normalizeData(newValue);

		LOG.debug("concatenateValues: ->" + cleanValue + "----" + newValue + "<-");

		if (cleanValue.equals(newValue) || newValue.equals(" "))
			return;
		if (cleanValue.equals(" ")) {
			rec.setValue(newValue);
		} else {
			rec.setValue(cleanValue + "|" + newValue);
		}
	}

	private Connector setupA22ServiceConnector() throws IOException {
		return new Connector(a22ConnectorURL, a22ConnectorUsr, a22ConnectorPwd);
	}

	private void readLastTimestampsForAllSigns(A22SignJSONPusher pusher) {
		signIdLastTimestampMap = new HashMap<>();
		List<StationDto> stations = pusher.fetchStations(pusher.initIntegreenTypology(), a22stationProperties.getProperty("origin"));

		for (StationDto stationDto : stations) {
			String stationCode = stationDto.getId();
			long lastTimestamp = ((Date) pusher.getDateOfLastRecord(stationCode, null, null)).getTime();
			LOG.debug("Station Code: " + stationCode + ", lastTimestamp: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(lastTimestamp));
			String signId = stationCode.substring(0, stationCode.lastIndexOf(":"));
			if (signIdLastTimestampMap.getOrDefault(signId, 0L) < lastTimestamp) {
				signIdLastTimestampMap.put(signId, lastTimestamp);
			}
		}
	}

	private long getLastTimestampOfSignInSeconds(A22SignJSONPusher pusher, String roadSignId) {

		if (signIdLastTimestampMap == null) {
			readLastTimestampsForAllSigns(pusher);
		}
		try {
			long ret = signIdLastTimestampMap.getOrDefault(roadSignId,
				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(a22stationProperties.getProperty("lastTimestamp")).getTime());

			LOG.debug("getLastTimestampOfSignInSeconds(" + roadSignId + "): " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ret));

			return ret / 1000;
		} catch (ParseException e) {
			LOG.error("Invalid lastTimestamp: " + a22stationProperties.getProperty("lastTimestamp"));
			throw new RuntimeException("Invalid lastTimestamp: " + a22stationProperties.getProperty("lastTimestamp"), e);
		}
	}

	private void setupDataType(A22SignJSONPusher pusher) {
		List<DataTypeDto> dataTypeDtoList = new ArrayList<>();
		Map<String, Object> map = null;
		try {
			map = Collections.singletonMap("signal-codes", signalsUtil.getStreetCodes());
		} catch (IOException e) {
			e.printStackTrace();
			LOG.warn("Unable to parse additional metadata from csv file");
		}
		DataTypeDto esportazione = new DataTypeDto(datatypesProperties.getProperty("a22vms.datatype.esposizione.key"),
			datatypesProperties.getProperty("a22vms.datatype.esposizione.unit"),
			datatypesProperties.getProperty("a22vms.datatype.esposizione.description"),
			datatypesProperties.getProperty("a22vms.datatype.esposizione.rtype"));
		esportazione.setMetaData(map);
		dataTypeDtoList.add(esportazione);
		DataTypeDto stato = new DataTypeDto(datatypesProperties.getProperty("a22vms.datatype.stato.key"),
			datatypesProperties.getProperty("a22vms.datatype.stato.unit"),
			datatypesProperties.getProperty("a22vms.datatype.stato.description"),
			datatypesProperties.getProperty("a22vms.datatype.stato.rtype"));
		dataTypeDtoList.add(stato);
		pusher.syncDataTypes(dataTypeDtoList);
	}

	/*
	 * Method used only for development/debugging
	 */
	public static void main(String[] args) {
		new MainA22Sign().execute();
	}

}
