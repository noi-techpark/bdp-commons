package it.bz.noi.a22.vms;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

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
	static final String KEY_ESPOSIZIONE = "esposizione";
	static final String KEY_STATO = "stato";
	
	static final String ORIGIN = "a22";

	private static Logger log = Logger.getLogger(MainA22Sign.class);

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException
	{
		long startTime = System.currentTimeMillis();
		try
		{
			log.info("Start A22SignMain");

			A22SignJSONPusher pusher = new A22SignJSONPusher();

			// 2019-06-26 d@vide.bz: now that fetchstations works, should I find what???
			long lastTimestampSeconds = readLastTimestampSeconds(pusher);
			long delaySeconds = 3600; // 2019-06-21 d@vide.bz: a22 data realtime delay
			long nowSeconds = System.currentTimeMillis() / 1000 - delaySeconds;

			Connector a22Service = setupA22ServiceConnector();

			setupDataType(pusher);

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
							esposizioniDataMapDto.addRecord(virtualStationId, KEY_ESPOSIZIONE, esposizione);
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
							statoDataMapDto.addRecord(virtualStationId, KEY_STATO, stato);
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
							station.setOrigin(ORIGIN); // 2019-06-26 d@vide.bz: required to make fetchStations work!
							// add other metadata
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
		try (Reader in = new InputStreamReader(getClass().getResourceAsStream("a22connector.properties")))
		{
			Properties prop = new Properties();
			prop.load(in);
			url = prop.getProperty("url");
			user = prop.getProperty("user");
			password = prop.getProperty("password");
		}

		Connector a22Service = new Connector(url, user, password);

		return a22Service;
	}

	private static long readLastTimestampSeconds(A22SignJSONPusher pusher)
	{
		/*
		  2019-06-21 d@vide.bz: not working, i got an exception
		  
		  List<StationDto> stations = pusher.fetchStations(null, null);
		
		 */
		List<StationDto> stations = pusher.fetchStations(pusher.initIntegreenTypology(), ORIGIN);
		
		int size = stations.size();
		
		System.out.println(size);

		Calendar calendar = Calendar.getInstance();
		calendar.set(2019, Calendar.JUNE, 20, 0, 0, 0); // 2019-06-21 d@vide.bz: conventional date when no data was already saved
		calendar.set(Calendar.MILLISECOND, 0);

		long lastTimestamp = calendar.getTimeInMillis();
		return lastTimestamp / 1000;
	}

	private void setupDataType(A22SignJSONPusher pusher)
	{
		List<DataTypeDto> dataTypeDtoList = new ArrayList<>();
		DataTypeDto esportazione = new DataTypeDto(KEY_ESPOSIZIONE, "", "Messaggio esposto su display",
				"Instananteous");
		dataTypeDtoList.add(esportazione);
		DataTypeDto stato = new DataTypeDto(KEY_STATO, "", "Stato del display (acceso / spento)", "Instananteous");
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
