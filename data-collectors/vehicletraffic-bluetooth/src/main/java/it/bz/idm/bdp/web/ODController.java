package it.bz.idm.bdp.web;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

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
import it.bz.idm.bdp.service.OddsPusher;
import it.bz.idm.bdp.util.IntegreenException;

@RequestMapping("/json")
@Controller
@EnableWebMvc
public class ODController {

	@Autowired
	private Environment env;

	@Autowired
	private OddsPusher pusher;

	@ExceptionHandler({ RestClientException.class })
    public ResponseEntity<Object> handleException(RestClientException ex, WebRequest request) {
		ExceptionDto dto = new ExceptionDto();
		dto.setStatus(HttpStatus.GATEWAY_TIMEOUT.value());
		dto.setDescription(ex.getMessage());
		dto.setName(HttpStatus.GATEWAY_TIMEOUT.toString());
		return new ResponseEntity<>(dto,HttpStatus.GATEWAY_TIMEOUT);
    }

	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody void post(@RequestBody RecordList records){
		if (records == null || records.isEmpty()) {
			return;
		}

		List<OddsRecordDto> recs = records;
		OddsRecordDto.removeCorruptedData(recs);
		DataMapDto<RecordDtoImpl> dataMap = pusher.mapData(records);
		if (!recs.isEmpty()) {

			StationList stationList = new StationList();
			for (String stationName : dataMap.getBranch().keySet()) {
				StationDto station = new StationDto();
				station.setName(stationName);
				station.setId(stationName);
				station.setStationType(env.getRequiredProperty("stationtype"));
				stationList.add(station);
			}
			pusher.syncStations(stationList);

			List<DataTypeDto> dataTypes = new ArrayList<DataTypeDto>();
			DataTypeDto dataType = new DataTypeDto();
			dataType.setName(env.getRequiredProperty("datatype"));
			dataTypes.add(dataType);
			pusher.syncDataTypes(dataTypes);

			pusher.pushData(dataMap);
		}
	}

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody Date getLastRecord(@RequestParam("station-id")String id, HttpServletResponse httpResponse) throws MalformedURLException {
		Object bdpResponse = pusher.getDateOfLastRecord(id, env.getRequiredProperty("datatype"), null);
		if (bdpResponse instanceof Date)
			return (Date) bdpResponse;

		if (bdpResponse instanceof IntegreenException)
			httpResponse.setStatus(HttpServletResponse.SC_NO_CONTENT);
		return null;
	}
}
