package it.bz.idm.bdp;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;

@Service
public class ParkingClient {
	private String origin;
	private static final String PROTOCOLL = "http://";
	private static final String P_GUIDE_GET_PARKING_METADATA = "pGuide.getCaratteristicheParcheggio";
	private static final String P_GUIDE_GET_FREE_SLOTS = "pGuide.getPostiLiberiParcheggio";
	private static final String P_GUIDE_GET_VERSION = "pGuide.getVersion";
	private static final int PARKING_ID_KEY = 0;
	private String defaultServerHost;
	private String defaultServerPort;
	private String defaultSiteName;
	private static final int NUMBER_OF_FREE_PARKING_PLACES_KEY = 1;
	private static final int XMLRPCREPLYTIMEOUT = 10000;
	private static final int XMLRPCCONNECTIONTIMEOUT = 8000;
	private static final String P_GUIDE_GET_POSTI_LIBERI_PARCHEGGIO_EXT = "pGuide.getPostiLiberiParcheggioExt";

	private XmlRpcClient client;

	@Autowired
	public ParkingClient(@Value("${pbz_origin}") String origin,
			@Value("${pbz_default_server_host}") String defaultServerHost,
			@Value("${pbz_default_server_port}") String defaultServerPort,
			@Value("${pbz_default_site_name}") String defaultSiteName) {
		this.origin = origin;
		this.defaultServerHost = defaultServerHost;
		this.defaultServerPort = defaultServerPort;
		this.defaultSiteName = defaultSiteName;
	}

	public void connect(String serverHost, String serverPort, String siteName) {
		if (serverHost == null)
			serverHost = defaultServerHost;
		if (serverPort == null)
			serverPort = defaultServerPort;
		if (siteName == null)
			siteName = defaultSiteName;
		if (client == null) {
			XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
			try {
				config.setServerURL(new URL(PROTOCOLL + serverHost + ":" + serverPort + siteName));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			config.setEnabledForExtensions(true);
			config.setReplyTimeout(XMLRPCREPLYTIMEOUT);
			config.setConnectionTimeout(XMLRPCCONNECTIONTIMEOUT);
			client = new XmlRpcClient();
			client.setConfig(config);
		}
	}

	public void connect() {
		connect(null, null, null);
	}

	public String getServerVersion() throws XmlRpcException {
		return getString(P_GUIDE_GET_VERSION);
	}

	public Integer getNumberOfFreeParkingSlots(Integer identifier) throws XmlRpcException {
		Integer value = null;
		List<Object> pParams = new ArrayList<Object>();
		pParams.add(identifier);
		List<Object> freeParkingSlots = Arrays.asList(getArray(P_GUIDE_GET_FREE_SLOTS, pParams));
		if (freeParkingSlots != null) {
			Integer parkingId = (Integer) freeParkingSlots.get(PARKING_ID_KEY);
			if (parkingId != null && parkingId.equals(identifier))
				value = (Integer) freeParkingSlots.get(NUMBER_OF_FREE_PARKING_PLACES_KEY);
		}
		return value == null ? new Integer(-1) : value;
	}

	public StationDto getParkingMetaData(Integer identifier) {
		StationDto stationDto = new StationDto();
		List<Object> pParams = new ArrayList<Object>();
		pParams.add(identifier);
		List<Object> metaDataParkingPlace = null;
		try {
			metaDataParkingPlace = Arrays.asList(getArray(P_GUIDE_GET_PARKING_METADATA, pParams));
			stationDto.setId(metaDataParkingPlace.get(0).toString());
			stationDto.setName(metaDataParkingPlace.get(1).toString());
			stationDto.getMetaData().put("slots",Integer.valueOf(metaDataParkingPlace.get(2).toString()));
			stationDto.setOrigin(origin);
		} catch (XmlRpcException e) {
			e.printStackTrace();
		}

		return stationDto;
	}

	public Integer[] getIdentifiersOfParkingPlaces() throws XmlRpcException {
		return getArrayOfInteger("pGuide.getElencoIdentificativiParcheggi");
	}

	public Object[] getArray(String method, List<Object> pParams) throws XmlRpcException {
		Object object = client.execute(method, pParams);
		Object[] objects = (Object[]) object;
		return objects;
	}

	public Integer[] getArrayOfInteger(String method) throws XmlRpcException {
		Object object = client.execute(method, (Object[]) null);
		Object[] objects = (Object[]) object;
		return Arrays.copyOf(objects, objects.length, Integer[].class);
	}

	public Integer getInteger(String method) throws XmlRpcException {
		Object object = client.execute(method, (Object[]) null);
		return (Integer) object;
	}

	public String getString(String method) throws XmlRpcException {
		Object object = client.execute(method, (Object[]) null);
		return (String) object;
	}

	public List<Object> getData(Integer identifier) {
		Object[] params = new Object[] { identifier };
		try {
			Object object = client.execute(P_GUIDE_GET_POSTI_LIBERI_PARCHEGGIO_EXT, params);
			return Arrays.asList((Object[]) object);
		} catch (XmlRpcException e) {
			e.printStackTrace();
		}
		return null;
	}
	public Integer[] getIdentifers() {
		try {
			return this.getIdentifiersOfParkingPlaces();

		} catch (XmlRpcException e) {
			e.printStackTrace();
		}
		return null;
	}
	public void insertDataInto(DataMapDto<RecordDtoImpl> sMap) {
		Integer[] identifers = this.getIdentifers();
		if (identifers!=null){
			for (Integer identifier:identifers){
				List<Object> objects = this.getData(identifier);
				DataMapDto<RecordDtoImpl> dataMap = new DataMapDto<>();
				List<RecordDtoImpl> records = new ArrayList<RecordDtoImpl>();
				SimpleRecordDto record = new SimpleRecordDto();
				if (objects!=null && objects.size()>=15){
					Boolean communicationState = Byte.valueOf(objects.get(7).toString())==1?true:false;
					Boolean controlUnit = Byte.valueOf(objects.get(9).toString())==1?true:false;
					Boolean totalChangeAllarm = Byte.valueOf(objects.get(11).toString())==1?true:false;
					Boolean inactiveAllarm = Byte.valueOf(objects.get(12).toString())==1?true:false;
					Boolean occupiedSlotsAllarm = Byte.valueOf(objects.get(13).toString())==1?true:false;
					if(!communicationState && !controlUnit && !totalChangeAllarm && !inactiveAllarm && !occupiedSlotsAllarm) {
						record.setValue(objects.get(5));
						record.setTimestamp((Integer) objects.get(6)*1000l);
						records.add(record);
						dataMap.setData(records);
						sMap.getBranch().put(identifier.toString(), dataMap);
					}
				}

			}
		}
	}

	public void insertParkingMetaDataInto(List<StationDto> stations) {
		Integer[] identifers = this.getIdentifers();
		if (identifers != null) {
			for (Integer identifier:identifers){
				StationDto parkingMetaData = this.getParkingMetaData(identifier);
				if (parkingMetaData != null)
					stations.add(parkingMetaData);
			}
		}
	}		
}
