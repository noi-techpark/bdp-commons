package it.bz.idm.bdp.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.ProvenanceDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.json.JSONPusher;
import it.bz.tis.zrx2json.MeteoStationDataPoint;
import it.bz.tis.zrx2json.Meteostation;

@Service
public class MeteoPusher extends JSONPusher{

	public static final String NAME = "SNAME";
	public static final Object CNAME = "CNAME";
	public static final Object CUNIT = "CUNIT";
	public static final Object VALUE_TYPE = "RTYPE";
	public static final Integer PERIOD = 600;
	public static final String CMW = "CMW";
	private static final String ZEUS_ID = "SANR";
	@Override
	public String initIntegreenTypology() {
		return "MeteoStation";
	}
	@Autowired
	private Environment environment;
	private static final DateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmmss");

	@Override
	public <T> DataMapDto<RecordDtoImpl> mapData(T data) {
		@SuppressWarnings("unchecked")
		List<Meteostation> stationsData = (List<Meteostation>) data;
		DataMapDto<RecordDtoImpl> dataMap = new DataMapDto<>();
		for (Meteostation station : stationsData){
			//TODO get timezone dynamically with station coordinates
			dateFormatter.setTimeZone(TimeZone.getTimeZone(environment.getProperty("default_timezone"))); //set the timezone in which your station is currently located (not defined in zrx)
			Map<String, String> params = station.getMetaData();
			String stationId = params.get(ZEUS_ID);
			String type = params.get(CNAME);
			String existingType = environment.getProperty(type);
			if (existingType == null)
					existingType = type;
			DataMapDto<RecordDtoImpl> typeMapDto = dataMap.getBranch().get(stationId);
			if (typeMapDto == null){
				typeMapDto = new DataMapDto<>();
				dataMap.getBranch().put(stationId, typeMapDto);
			}
			Date dateOfLastRecord = (Date) this.getDateOfLastRecord(stationId, existingType, null);
			List<RecordDtoImpl> list = new ArrayList<>();
			typeMapDto.getBranch().put(existingType, new DataMapDto<>(list));
			for (MeteoStationDataPoint point: station.getDataPoints()){
				Date date = null;
				try {
					date = dateFormatter.parse(point.getDate());
					if (dateOfLastRecord.getTime()>=date.getTime())
						continue;
					SimpleRecordDto simpleRecordDto = new SimpleRecordDto(date.getTime(),point.getDataPoint());
					Integer cmw = Integer.valueOf(params.get(CMW));
					Integer period = PERIOD;
					if (cmw != null)
						period = 3600 / (cmw / 24);
					simpleRecordDto.setPeriod(period);
					list.add(simpleRecordDto);
				} catch (ParseException e) {
					e.printStackTrace();
					continue;
				}
			}
		}
		return dataMap;
	}

	@Override
	public ProvenanceDto defineProvenance() {
		// TODO Auto-generated method stub
		return null;
	}
}
