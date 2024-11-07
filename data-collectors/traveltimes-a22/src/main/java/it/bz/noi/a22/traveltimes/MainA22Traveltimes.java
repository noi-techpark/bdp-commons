// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.noi.a22.traveltimes;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;

@Component
@Configuration
@PropertySource("classpath:it/bz/noi/a22/traveltimes/a22connector.properties")
public class MainA22Traveltimes
{
	@Value("${a22url}")
    private String a22ConnectorURL;

    @Value("${a22user}")
    private String a22ConnectorUsr;

    @Value("${a22password}")
    private String a22ConnectorPwd;

	private static final Logger LOG = LoggerFactory.getLogger(MainA22Traveltimes.class);

	private final A22Properties datatypesProperties;
	private final A22Properties a22TraveltimesProperties;
	private HashMap<String, Long> stationIdLastTimestampMap;
	@Autowired
	A22TraveltimesJSONPusher pusher;

	private StationList stationList;

	public MainA22Traveltimes() {
		this.datatypesProperties = new A22Properties("a22traveltimesdatatypes.properties");
		this.a22TraveltimesProperties = new A22Properties("a22traveltimes.properties");
	}

	public void execute()
	{
		long startTime = System.currentTimeMillis();
		try
		{
			LOG.info("Start MainA22Traveltimes");

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
				List<HashMap<String, String>> segments = a22Service.getTravelTimeSegments();
				LOG.debug("got " + segments.size() + " segments");
				if (!segments.isEmpty()) {
					LOG.debug("the first segment is: {}", segments.get(0));
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
				LOG.error("step 2 failed, continuing anyway to de-auth...", e);
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
						DataMapDto<RecordDtoImpl> recs = new DataMapDto<>();

						List<HashMap<String, String>> traveltimes = a22Service.getTravelTimes(lastTimeStamp, lastTimeStamp + scanWindowSeconds, id);

						LOG.debug("got " + traveltimes.size() + " traveltime data records for " + simpleDateFormat.format(new Date(lastTimeStamp * 1000)) + ", " + simpleDateFormat.format(new Date((lastTimeStamp + scanWindowSeconds) * 1000)) + ", " + id + ":");
						if (!traveltimes.isEmpty()) {
							LOG.debug("the first travel time is: {}", traveltimes.get(0));
							traveltimes.forEach(traveltime -> {
								try{
									String stationId = traveltime.get("idtratto");
									long ts = Long.parseLong(traveltime.get("data")) * 1000;
									
									// ########## LIGHT VEHICLES ##########
									// lds
									String ldsLightKey = datatypesProperties.getProperty("a22traveltimes.datatype.lds_leggeri.key");
									String ldsLightRaw = traveltime.get("lds");
									recs.addRecord(stationId, ldsLightKey, new SimpleRecordDto(ts, ldsLightRaw, 1));
									String ldsLightDesc = datatypesProperties.getProperty("a22traveltimes.datatype.lds_leggeri.mapping." + ldsLightRaw + ".desc");
									recs.addRecord(stationId, ldsLightKey + "_desc", new SimpleRecordDto(ts, ldsLightDesc, 1));
									Double ldsLightVal = Double.parseDouble(datatypesProperties.getProperty("a22traveltimes.datatype.lds_leggeri.mapping." + ldsLightRaw + ".val"));
									recs.addRecord(stationId, ldsLightKey + "_val",new SimpleRecordDto(ts, ldsLightVal, 1));

									// tempo
									String tempoLightKey = datatypesProperties.getProperty("a22traveltimes.datatype.tempo_leggeri.key");
									recs.addRecord(stationId, tempoLightKey, new SimpleRecordDto(ts, Double.parseDouble(traveltime.get("tempo")), 1));
									// velocita
									String velocitaLightKey = datatypesProperties.getProperty("a22traveltimes.datatype.velocita_leggeri.key");
									recs.addRecord(stationId, velocitaLightKey,new SimpleRecordDto(ts, Double.parseDouble(traveltime.get("velocita")), 1));

									// ########## HEAVY VEHICLES ##########
									// lds
									String ldsHeavyKey = datatypesProperties.getProperty("a22traveltimes.datatype.lds_pesanti.key");
									String ldsHeavyRaw = traveltime.get("pesanti_lds");
									recs.addRecord(stationId, ldsHeavyKey, new SimpleRecordDto(ts, ldsHeavyRaw, 1));
									String ldsHeavyDesc = datatypesProperties.getProperty("a22traveltimes.datatype.lds_pesanti.mapping." + ldsHeavyRaw + ".desc");
									recs.addRecord(stationId, ldsHeavyKey + "_desc", new SimpleRecordDto(ts, ldsHeavyDesc, 1));
									Double ldsHeavyVal = Double.parseDouble(datatypesProperties.getProperty("a22traveltimes.datatype.lds_pesanti.mapping." + ldsHeavyRaw + ".val"));
									recs.addRecord(stationId, ldsHeavyKey + "_val",new SimpleRecordDto(ts, ldsHeavyVal, 1));

									// tempo
									String tempoHeavyKey = datatypesProperties.getProperty("a22traveltimes.datatype.tempo_pesanti.key");
									recs.addRecord(stationId, tempoHeavyKey, new SimpleRecordDto(ts, Double.parseDouble(traveltime.get("pesanti_tempo")), 1));
									// velocita
									String velocitaHeavyKey = datatypesProperties.getProperty("a22traveltimes.datatype.velocita_pesanti.key");
									recs.addRecord(stationId, velocitaHeavyKey,new SimpleRecordDto(ts, Double.parseDouble(traveltime.get("pesanti_velocita")), 1));
								} catch (Exception e) {
									LOG.error("Error during traveltime elaboration. Dumping current traveltime data structure:", e);
									LOG.error(traveltime.toString());
									throw e;
								}
							});

							LOG.debug("pushing all data: " + stationList.size() + " records");
							pusher.pushData(recs);
						}
						lastTimeStamp += scanWindowSeconds;
					} while (lastTimeStamp < System.currentTimeMillis() / 1000);
				}


			} catch (Exception e) {
				LOG.error("step 3 failed, continuing anyway to read de-auth...", e);
			}

			// step 4
			// de-authentication
			a22Service.close();
		}
		catch (Exception e)
		{
			throw new IllegalStateException(e);
		}
		finally
		{
			long stopTime = System.currentTimeMillis();
			LOG.debug("elaboration time (millis): " + (stopTime - startTime));
		}
	}

	private Connector setupA22ServiceConnector() throws IOException
	{
		return new Connector(a22ConnectorURL, a22ConnectorUsr, a22ConnectorPwd);
	}
	
	private DataTypeDto mkDt(String id, String postix) {
		return new DataTypeDto(datatypesProperties.getProperty("a22traveltimes.datatype."+id+".key") + postix,
				datatypesProperties.getProperty("a22traveltimes.datatype."+id+".unit"),
				datatypesProperties.getProperty("a22traveltimes.datatype."+id+".description"),
				datatypesProperties.getProperty("a22traveltimes.datatype."+id+".rtype"));
	}

	private void setupDataType() {
		List<DataTypeDto> dataTypeDtoList = new ArrayList<>();

		dataTypeDtoList.add(mkDt("lds_leggeri", ""));
		dataTypeDtoList.add(mkDt("lds_leggeri", "_desc"));
		dataTypeDtoList.add(mkDt("lds_leggeri", "_val"));
		dataTypeDtoList.add(mkDt("velocita_leggeri", ""));
		dataTypeDtoList.add(mkDt("tempo_leggeri", ""));

		dataTypeDtoList.add(mkDt("lds_pesanti", ""));
		dataTypeDtoList.add(mkDt("lds_pesanti", "_desc"));
		dataTypeDtoList.add(mkDt("lds_pesanti", "_val"));
		dataTypeDtoList.add(mkDt("velocita_pesanti", ""));
		dataTypeDtoList.add(mkDt("tempo_pesanti", ""));

		pusher.syncDataTypes(dataTypeDtoList);
	}

	private long getLastTimestampOfStationInSeconds(String stationId) {
		if(stationIdLastTimestampMap == null) {
			readLastTimestampsForAllStations();
		}
		try {
			long defaultTs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.parse(a22TraveltimesProperties.getProperty("lastTimestamp")).getTime();

			long ret = stationIdLastTimestampMap.getOrDefault(stationId, defaultTs);

			// Use default time as latest starting point. Remote API might not have data before that time
			ret = Math.max(ret, defaultTs);

			LOG.debug("getLastTimestampOfStationInSeconds(" + stationId + "): " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ret));

			return ret / 1000;
		} catch (ParseException e) {
			throw new RuntimeException("Invalid lastTimestamp: " + a22TraveltimesProperties.getProperty("lastTimestamp"), e);
		}
	}

	private void readLastTimestampsForAllStations()
	{
		stationIdLastTimestampMap = new HashMap<>();

		for(StationDto stationDto: this.stationList) {
			String stationCode = stationDto.getId();
			long lastTimestamp = ((Date) pusher.getDateOfLastRecord(stationCode, null, null)).getTime();
			LOG.debug("Station Code: " + stationCode + ", lastTimestamp: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(lastTimestamp));
			if(stationIdLastTimestampMap.getOrDefault(stationCode, 0L) < lastTimestamp) {
				stationIdLastTimestampMap.put(stationCode, lastTimestamp);
			}
		}
	}

	/*
	 * Method used only for development/debugging
	 */
	public static void main(String[] args)
	{
		new MainA22Traveltimes().execute();
	}

}
