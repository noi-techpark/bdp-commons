package it.bz.odh.web;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.ExceptionDto;
import it.bz.idm.bdp.dto.OddsRecordDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;
import it.bz.idm.bdp.util.IntegreenException;
import it.bz.odh.service.OddsPusher;
import it.bz.odh.util.BluetoothMappingUtil;

@RequestMapping("/json")
@Controller
@EnableWebMvc
public class ODController {

	private Logger logger = LoggerFactory.getLogger(ODController.class);
	@Autowired
	private Environment env;

	@Autowired
	private OddsPusher pusher;

	@Autowired
	private BluetoothMappingUtil mappingUtil;

	@ExceptionHandler({ RestClientException.class })
    public ResponseEntity<Object> handleException(RestClientException ex, WebRequest request) {
		ExceptionDto dto = new ExceptionDto();
		dto.setStatus(HttpStatus.GATEWAY_TIMEOUT.value());
		dto.setDescription(ex.getMessage());
		dto.setName(HttpStatus.GATEWAY_TIMEOUT.toString());
		return new ResponseEntity<>(dto,HttpStatus.GATEWAY_TIMEOUT);
    }

	/**
	 * Endpoint for Bluetoothboxes which synchronizes single BluetoothStations and sends data records to ODH
	 *
	 * @param records
	 */
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody void post(@RequestBody RecordList records){
		if (records == null || records.isEmpty()) {
			logger.debug("No records present");
			return;
		}

		List<OddsRecordDto> recs = records;
		logger.debug("RemoveCorruptedRecords");
		OddsRecordDto.removeCorruptedData(recs);
		logger.debug(records.size()+"left to process");
		DataMapDto<RecordDtoImpl> dataMap = pusher.mapData(records);
		if (!recs.isEmpty()) {

			StationList stationList = new StationList();
			for (String stationName : dataMap.getBranch().keySet()) {
				StationDto station = new StationDto();
				Double[] coordinatesByIdentifier = mappingUtil.getCoordinatesByIdentifier(stationName);
				if (coordinatesByIdentifier != null) {
					station.setLongitude(coordinatesByIdentifier[0]);
					station.setLatitude(coordinatesByIdentifier[1]);
				}
				Map<String, Object> metaDataByIdentifier = mappingUtil.getMetaDataByIdentifier(stationName);
				if (metaDataByIdentifier != null) {
					Map<String, Object> cleanMap = mappingUtil.mergeTranslations(metaDataByIdentifier);
					if (cleanMap != null)
						station.getMetaData().putAll(cleanMap);
				}
				station.setName(stationName);
				station.setId(stationName);
				station.setStationType(env.getRequiredProperty("stationtype"));
				stationList.add(station);
			}
			pusher.syncStations(stationList);

			List<DataTypeDto> dataTypes = new ArrayList<>();
			DataTypeDto dataType = new DataTypeDto();
			dataType.setName(env.getRequiredProperty("datatype"));
			dataType.setPeriod(1);
			dataTypes.add(dataType);
			pusher.syncDataTypes(dataTypes);

			pusher.pushData(dataMap);
		}
	}

	/**
	 * @param id BluetoothStation identifier
	 * @param httpResponse
	 * @return unix timestamp of the last inserted record of that station and type
	 * @throws MalformedURLException
	 */
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody Date getLastRecord(@RequestParam("station-id")String id, HttpServletResponse httpResponse) {
		Object bdpResponse = pusher.getDateOfLastRecord(id, env.getRequiredProperty("datatype"), null);
		if (bdpResponse instanceof Date)
			return (Date) bdpResponse;

		if (bdpResponse instanceof IntegreenException)
			httpResponse.setStatus(HttpServletResponse.SC_NO_CONTENT);
		return null;
	}
	@RequestMapping(method = RequestMethod.POST,value="hash")
	public @ResponseBody List<String> hash(@RequestBody RecordList records){
	    return pusher.hash(records);
	}
}
