package it.bz.noi.a22.vms;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;

@DisallowConcurrentExecution
public class MainA22Sign implements Job
{
	
	private static Logger log = Logger.getLogger(MainA22Sign.class);

	private final A22Properties datatypesProperties;
	private final A22Properties a22stationProperties;
	private HashMap<String, Long> signIdLastTimestampMap;

	public MainA22Sign() {
		this.datatypesProperties = new A22Properties("a22vmsdatatypes.properties");
		this.a22stationProperties = new A22Properties("a22sign.properties");

	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException
	{
		long startTime = System.currentTimeMillis();
		try
		{
			log.info("Start A22SignMain");

			A22SignJSONPusher pusher = new A22SignJSONPusher();

			long delaySeconds = 3600; // 2019-06-21 d@vide.bz: a22 data realtime delay
			long nowSeconds = System.currentTimeMillis() / 1000 - delaySeconds;

			Connector a22Service = setupA22ServiceConnector();

			setupDataType(pusher);

			readLastTimestampsForAllSigns(pusher);

			StationList stationList = new StationList();
			DataMapDto<RecordDtoImpl> esposizioniDataMapDto = new DataMapDto<>();
			DataMapDto<RecordDtoImpl> statoDataMapDto = new DataMapDto<>();

			ArrayList<HashMap<String, String>> signs = a22Service.getSigns();
			log.debug("got " + signs.size() + " signs");
			for (int i = 0; i < signs.size(); i++)
			{
				HashMap<String, String> sign = signs.get(i);

				String sign_id = sign.get("id");
				log.debug("sign_id: " + sign_id);
				String sign_descr = sign.get("descr");
				String road = sign.get("road");
				String direction_id = sign.get("direction_id");
				String pmv_type = sign.get("pmv_type");
				String segment_start = sign.get("segment_start");
				String segment_end = sign.get("segment_end");
				String position_m = sign.get("position_m");
				double sign_lat = Double.parseDouble(sign.get("lat"));
				double sign_lon = Double.parseDouble(sign.get("long"));

				long lastTimestampSeconds = getLastTimestampOfSignInSeconds(pusher, road + ":" + sign_id);

				ArrayList<HashMap<String, Object>> events = a22Service.getEvents(lastTimestampSeconds, nowSeconds,
						Long.parseLong(sign_id));
				log.debug("got " + events.size() + " events");
				for (HashMap<String, Object> event : events)
				{
					String event_id = (String) event.get("id");
					String event_timestamp = (String) event.get("timestamp");

					HashMap<String, SimpleRecordDto> esposizioneByComponentId = new HashMap<>();
					HashMap<String, SimpleRecordDto> statoByComponentId = new HashMap<>();

					ArrayList<HashMap<String, Object>> components_pages = (ArrayList<HashMap<String, Object>>) event
							.get("component");
					for (HashMap<String, Object> component_page : components_pages)
					{
						String component_id = (String) component_page.get("component_id");
						String page_id = (String) component_page.get("page_id");
						String data = (String) component_page.get("data");
						String status = (String) component_page.get("status");

						String virtualStationId = road + ":" + sign_id + ":" + component_id;

						// esposizione

						SimpleRecordDto esposizione = esposizioneByComponentId.get(component_id);
						// 2019-06-26 d@vide.bz: replace multiple spaces with one space
						String normalizedData = data.replaceAll("\\s+", " ");
						if (esposizione == null)
						{
							esposizione = new SimpleRecordDto(Long.parseLong(event_timestamp) * 1000, normalizedData, 1);
							esposizioniDataMapDto.addRecord(virtualStationId, datatypesProperties.getProperty("a22vms.datatype.esposizione.key"), esposizione);
							esposizioneByComponentId.put(component_id, esposizione);
						}
						else
						{
							esposizione.setValue(esposizione.getValue() + "|" + normalizedData);
						}

						// stato

						SimpleRecordDto stato = statoByComponentId.get(component_id);
						if (stato == null)
						{
							stato = new SimpleRecordDto(Long.parseLong(event_timestamp) * 1000, status, 1);
							statoDataMapDto.addRecord(virtualStationId, datatypesProperties.getProperty("a22vms.datatype.stato.key"), stato);
							statoByComponentId.put(component_id, stato);
						}
						else
						{
							stato.setValue(stato.getValue() + "|" + status);
						}

						// check when virtualStation alredy exists
						boolean exists = stationList.stream()
								.anyMatch((StationDto station) -> station.getId().equals(virtualStationId));
						if (!exists)
						{
							String virtualStationIdName = sign_descr + " - component:" + component_id;
							StationDto station = new StationDto(virtualStationId, virtualStationIdName, sign_lat,
									sign_lon);
							station.getMetaData().put("pmv_type", pmv_type);
							station.setOrigin(a22stationProperties.getProperty("origin")); // 2019-06-26 d@vide.bz: required to make fetchStations work!
							// add other metadata
							station.getMetaData().put("direction_id", direction_id);
							station.getMetaData().put("segment_start", segment_start);
							station.getMetaData().put("segment_end", segment_end);
							station.getMetaData().put("position_m", position_m);
							stationList.add(station);
						}

					}
				}
			}
			pusher.syncStations(stationList);
			pusher.pushData(esposizioniDataMapDto);
			pusher.pushData(statoDataMapDto);
		}
		catch (IOException e)
		{
			throw new JobExecutionException(e);
		}
		finally
		{
			long stopTime = System.currentTimeMillis();
			log.debug("elaboration time (millis): " + (stopTime - startTime));
		}
	}

	private Connector setupA22ServiceConnector() throws IOException
	{
		String url;
		String user;
		String password;

		// read connector auth informations
		A22Properties prop = new A22Properties("a22connector.properties");
		url = prop.getProperty("url");
		user = prop.getProperty("user");
		password = prop.getProperty("password");

		Connector a22Service = new Connector(url, user, password);

		return a22Service;
	}

	private void readLastTimestampsForAllSigns(A22SignJSONPusher pusher)
	{
		/*
		  2019-06-21 d@vide.bz: not working, i got an exception
		  
		  List<StationDto> stations = pusher.fetchStations(null, null);


                 long lastTimestamp = ((java.util.Date) pusher.getDateOfLastRecord(stationcode, null, null)).getTime();
				if (lastTimestamp <= 0)
				{
					Calendar calendar = Calendar.getInstance();
					calendar.set(2019, Calendar.JUNE, 21, 0, 0, 0);
					calendar.set(Calendar.MILLISECOND, 0);

					lastTimestamp = calendar.getTimeInMillis();
				}
				else


			s1:c1   2019-07-10
			s1:c2   2019-07-09

			-> s1 si usa 2019-07-10

			s2:c1   2019-07-07

			-> s2 si usa 2019-07-07

 			HashMap stazione e timestamp

		
		 */
		signIdLastTimestampMap = new HashMap<>();
		List<StationDto> stations = pusher.fetchStations(pusher.initIntegreenTypology(), a22stationProperties.getProperty("origin"));

		for(StationDto stationDto: stations) {
			String stationCode = stationDto.getId();
			long lastTimestamp = ((Date) pusher.getDateOfLastRecord(stationCode, null, null)).getTime();
			log.debug("Station Code: " + stationCode + ", lastTimestamp: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(lastTimestamp));
			String signId = stationCode.substring(0, stationCode.lastIndexOf(":"));
			if(signIdLastTimestampMap.getOrDefault(signId, 0L) < lastTimestamp) {
				signIdLastTimestampMap.put(signId, lastTimestamp);
			}
		}
	}

	private long getLastTimestampOfSignInSeconds(A22SignJSONPusher pusher, String roadSignId) {

		// dalla hashmap prendere per questa stazione il timestamp from

		// però comunque non più vecchio di 1 settimana

		// if (lastTimestampSeconds < now - 1 settimana)
		//	lastTimestampSeconds = now - 1 settimana

		if(signIdLastTimestampMap == null) {
			readLastTimestampsForAllSigns(pusher);
		}
		try {
			long ret = signIdLastTimestampMap.getOrDefault(roadSignId,
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(a22stationProperties.getProperty("lastTimestamp")).getTime());

			long scanWindowMilliseconds = Long.parseLong(a22stationProperties.getProperty("scanWindowSeconds")) * 1000;

			if(ret < System.currentTimeMillis() - scanWindowMilliseconds) {
				ret = System.currentTimeMillis() - scanWindowMilliseconds;
			}

			log.debug("getLastTimestampOfSignInSeconds(" + roadSignId + "): " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ret));

			return ret / 1000;
		} catch (ParseException e) {
			log.error("Invalid lastTimestamp: " + a22stationProperties.getProperty("lastTimestamp"));
			throw new RuntimeException("Invalid lastTimestamp: " + a22stationProperties.getProperty("lastTimestamp"), e);
		}
	}

	private void setupDataType(A22SignJSONPusher pusher)
	{
		List<DataTypeDto> dataTypeDtoList = new ArrayList<>();
		DataTypeDto esportazione = new DataTypeDto(datatypesProperties.getProperty("a22vms.datatype.esposizione.key"),
				datatypesProperties.getProperty("a22vms.datatype.esposizione.unit"),
				datatypesProperties.getProperty("a22vms.datatype.esposizione.description"),
				datatypesProperties.getProperty("a22vms.datatype.esposizione.rtype"));
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
	public static void main(String[] args) throws JobExecutionException
	{
		new MainA22Sign().execute(null);
	}

}
