package it.bz.noi.a22.traveltimes;

import it.bz.idm.bdp.dto.*;
import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@DisallowConcurrentExecution
public class MainA22Traveltimes implements Job
{

	private static Logger log = Logger.getLogger(MainA22Traveltimes.class);

	private final A22Properties datatypesProperties;
	private final A22Properties a22TraveltimesProperties;
	private HashMap<String, Long> stationIdLastTimestampMap;
	A22TraveltimesJSONPusher pusher = new A22TraveltimesJSONPusher();

	private List<DataTypeDto> dataTypeDtoList;
	private StationList stationList;

	public MainA22Traveltimes() {
		this.datatypesProperties = new A22Properties("a22traveltimesdatatypes.properties");
		this.a22TraveltimesProperties = new A22Properties("a22traveltimes.properties");

	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException
	{
		long startTime = System.currentTimeMillis();
		try
		{
			log.info("Start MainA22Traveltimes");

			// step 1
			// create a Connector instance: this will perform authentication and store the session
			//
			// the session will last 24 hours unless de-authenticated before - however, if a user
			// de-authenticates one session, all sessions of the same user will be de-authenticated
			Connector a22Service = setupA22ServiceConnector();

			setupDataType();


			// step 2
			// get the list of segments
			stationList = new StationList();
			try {
				ArrayList<HashMap<String, String>> segments = a22Service.getTravelTimeSegments();
				log.debug("got " + segments.size() + " segments");
				if (segments.size() > 0) {
					log.debug("the first segment is:");
					log.debug(segments.get(0));
					segments.forEach(segment -> {
						StationDto edge = new StationDto(segment.get("idtratto"),
								segment.get("descrizione"),
								null,
								null);
						edge.setOrigin(a22TraveltimesProperties.getProperty("origin"));
						edge.setStationType(a22TraveltimesProperties.getProperty("stationtype"));

						// add coordinates ad metadata
						edge.getMetaData().put("latitudineinizio", Double.parseDouble(segment.get("latitudineinizio")));
						edge.getMetaData().put("longitudininizio", Double.parseDouble(segment.get("longitudininizio")));
						edge.getMetaData().put("latitudinefine", Double.parseDouble(segment.get("latitudinefine")));
						edge.getMetaData().put("longitudinefine", Double.parseDouble(segment.get("longitudinefine")));

						// add other metadata
						String idDirezione = "";
						switch(Integer.parseInt(segment.get("iddirezione"))) {
							case 1:
								idDirezione = "Sud";
								break;
							case 2:
								idDirezione = "Nord";
								break;
							case 3:
								idDirezione = "Entrmbe";
								break;
							default:
								idDirezione = "Non definito";
								break;
						}
						edge.getMetaData().put("iddirezione", idDirezione);
						edge.getMetaData().put("metroinizio", Integer.parseInt(segment.get("metroinizio")));
						edge.getMetaData().put("metrofine", Integer.parseInt(segment.get("metrofine")));
						edge.getMetaData().put("lunghezza", Math.abs(Integer.parseInt(segment.get("metrofine")) - Integer.parseInt(segment.get("metroinizio"))));
						stationList.add(edge);
					});
					pusher.syncStations(pusher.initIntegreenTypology(), stationList);

				}
			} catch (Exception e) {
				log.error("step 2 failed, continuing anyway to de-auth...", e);
			}

			// step 3
			// get the list of travel times
			try {

				long scanWindowSeconds = Long.parseLong(a22TraveltimesProperties.getProperty("scanWindowSeconds"));
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				for (int i = 0; i < stationList.size(); i++) {
					String id = stationList.get(i).getId();

					long lastTimeStamp = getLastTimestampOfStationInSeconds(id);

					do {
					DataMapDto<RecordDtoImpl> ldsDataMapDto = new DataMapDto<>();
					DataMapDto<RecordDtoImpl> ldsDescDataMapDto = new DataMapDto<>();
					DataMapDto<RecordDtoImpl> ldsValDataMapDto = new DataMapDto<>();
					DataMapDto<RecordDtoImpl> tempoDataMapDto = new DataMapDto<>();
					DataMapDto<RecordDtoImpl> velocitaDataMapDto = new DataMapDto<>();


					ArrayList<HashMap<String, String>> traveltimes = a22Service.getTravelTimes(lastTimeStamp, lastTimeStamp + scanWindowSeconds, id);

					log.debug("got " + traveltimes.size() + " traveltime data records for " + simpleDateFormat.format(new Date(lastTimeStamp * 1000)) + ", " + simpleDateFormat.format(new Date((lastTimeStamp + scanWindowSeconds) * 1000)) + ", " + id + ":");
					if (!traveltimes.isEmpty()) {
						log.debug("the first travel time is:");
						log.debug(traveltimes.get(0));
						traveltimes.forEach(traveltime -> {

							// lds
							String ldsKey = datatypesProperties.getProperty("a22traveltimes.datatype.lds.key");
							String ldsRaw = traveltime.get("lds");
							SimpleRecordDto lds = new SimpleRecordDto(Long.parseLong(traveltime.get("data")) * 1000,
									ldsRaw,
									1);
							ldsDataMapDto.addRecord(traveltime.get("idtratto"),
									ldsKey,
									lds);
							SimpleRecordDto ldsDesc = new SimpleRecordDto(Long.parseLong(traveltime.get("data")) * 1000,
									datatypesProperties.getProperty("a22traveltimes.datatype.lds.mapping." + ldsRaw + ".desc"),
									1);
							ldsDescDataMapDto.addRecord(traveltime.get("idtratto"),
									ldsKey + "_desc",
									ldsDesc);
							SimpleRecordDto ldsVal = new SimpleRecordDto(Long.parseLong(traveltime.get("data")) * 1000,
									Double.parseDouble(datatypesProperties.getProperty("a22traveltimes.datatype.lds.mapping." + ldsRaw + ".val")),
									1);
							ldsValDataMapDto.addRecord(traveltime.get("idtratto"),
									ldsKey + "_val",
									ldsVal);

							// tempo
							SimpleRecordDto tempo = new SimpleRecordDto(Long.parseLong(traveltime.get("data")) * 1000L,
									Double.parseDouble(traveltime.get("tempo")),
									1);
							tempoDataMapDto.addRecord(traveltime.get("idtratto"),
									datatypesProperties.getProperty("a22traveltimes.datatype.tempo.key"),
									tempo);

							// velocita
							SimpleRecordDto velocita = new SimpleRecordDto(Long.parseLong(traveltime.get("data")) * 1000L,
									Double.parseDouble(traveltime.get("velocita")),
									1);
							velocitaDataMapDto.addRecord(traveltime.get("idtratto"),
									datatypesProperties.getProperty("a22traveltimes.datatype.velocita.key"),
									velocita);
						});

						log.debug("pushing all ldsDataMapDto data: " + stationList.size() + " records");
						pusher.pushData(ldsDataMapDto);
						log.debug("pushing all ldsDescDataMapDto data: " + stationList.size() + " records");
						pusher.pushData(ldsDescDataMapDto);
						log.debug("pushing all ldsValDataMapDto data: " + stationList.size() + " records");
						pusher.pushData(ldsValDataMapDto);
						log.debug("pushing all tempoDataMapDto data: " + stationList.size() + " records");
						pusher.pushData(tempoDataMapDto);
						log.debug("pushing all velocitaDataMapDto data: " + stationList.size() + " records");
						pusher.pushData(velocitaDataMapDto);
					}
						lastTimeStamp += scanWindowSeconds;
					} while (lastTimeStamp < System.currentTimeMillis() / 1000);
				}


			} catch (Exception e) {
				log.error("step 3 failed, continuing anyway to read de-auth...", e);
			}

			// step 4
			// de-authentication
			a22Service.close();
		}
		catch (Exception e)
		{
			log.error(e);
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

	private void setupDataType()
	{
		List<DataTypeDto> dataTypeDtoList = new ArrayList<>();
		DataTypeDto lds = new DataTypeDto(datatypesProperties.getProperty("a22traveltimes.datatype.lds.key"),
				datatypesProperties.getProperty("a22traveltimes.datatype.lds.unit"),
				datatypesProperties.getProperty("a22traveltimes.datatype.lds.description"),
				datatypesProperties.getProperty("a22traveltimes.datatype.lds.rtype"));
		DataTypeDto ldsDesc = new DataTypeDto(datatypesProperties.getProperty("a22traveltimes.datatype.lds.key") + "_desc",
				datatypesProperties.getProperty("a22traveltimes.datatype.lds.unit"),
				datatypesProperties.getProperty("a22traveltimes.datatype.lds.description"),
				datatypesProperties.getProperty("a22traveltimes.datatype.lds.rtype"));
		DataTypeDto ldsVal = new DataTypeDto(datatypesProperties.getProperty("a22traveltimes.datatype.lds.key") + "_val",
				datatypesProperties.getProperty("a22traveltimes.datatype.lds.unit"),
				datatypesProperties.getProperty("a22traveltimes.datatype.lds.description"),
				datatypesProperties.getProperty("a22traveltimes.datatype.lds.rtype"));
		DataTypeDto tempo = new DataTypeDto(datatypesProperties.getProperty("a22traveltimes.datatype.tempo.key"),
				datatypesProperties.getProperty("a22traveltimes.datatype.tempo.unit"),
				datatypesProperties.getProperty("a22traveltimes.datatype.tempo.description"),
				datatypesProperties.getProperty("a22traveltimes.datatype.tempo.rtype"));
		DataTypeDto velocita = new DataTypeDto(datatypesProperties.getProperty("a22traveltimes.datatype.velocita.key"),
				datatypesProperties.getProperty("a22traveltimes.datatype.velocita.unit"),
				datatypesProperties.getProperty("a22traveltimes.datatype.velocita.description"),
				datatypesProperties.getProperty("a22traveltimes.datatype.velocita.rtype"));
		dataTypeDtoList.add(lds);
		dataTypeDtoList.add(ldsDesc);
		dataTypeDtoList.add(ldsVal);
		dataTypeDtoList.add(tempo);
		dataTypeDtoList.add(velocita);
		pusher.syncDataTypes(dataTypeDtoList);
	}

	private long getLastTimestampOfStationInSeconds(String stationId) {

		if(stationIdLastTimestampMap == null) {
			readLastTimestampsForAllStations();
		}
		try {
			long ret = stationIdLastTimestampMap.getOrDefault(stationId,
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(a22TraveltimesProperties.getProperty("lastTimestamp")).getTime());

			log.debug("getLastTimestampOfStationInSeconds(" + stationId + "): " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ret));

			return ret / 1000;
		} catch (ParseException e) {
			log.error("Invalid lastTimestamp: " + a22TraveltimesProperties.getProperty("lastTimestamp"), e);
			throw new RuntimeException("Invalid lastTimestamp: " + a22TraveltimesProperties.getProperty("lastTimestamp"), e);
		}
	}

	private void readLastTimestampsForAllStations()
	{
		stationIdLastTimestampMap = new HashMap<>();

		for(StationDto stationDto: this.stationList) {
			String stationCode = stationDto.getId();
			long lastTimestamp = ((Date) pusher.getDateOfLastRecord(stationCode, null, null)).getTime();
			log.debug("Station Code: " + stationCode + ", lastTimestamp: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(lastTimestamp));
			if(stationIdLastTimestampMap.getOrDefault(stationCode, 0L) < lastTimestamp) {
				stationIdLastTimestampMap.put(stationCode, lastTimestamp);
			}
		}
	}

	/*
	 * Method used only for development/debugging
	 */
	public static void main(String[] args) throws JobExecutionException
	{
		new MainA22Traveltimes().execute(null);
	}

}
