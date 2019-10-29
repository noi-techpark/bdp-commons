package it.bz.idm.bdp.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;
import it.bz.idm.bdp.service.OddsPusher;
import it.bz.idm.bdp.util.BluetoothMappingUtil;

@RequestMapping("/trigger")
@Controller
@EnableWebMvc
public class TriggerController {

	@Autowired
	private OddsPusher pusher;
	
	@Autowired
	private BluetoothMappingUtil metaUtil;


	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody void post(){
		metaUtil.setCachedData(null);
		StationList stations = new StationList();
		for (Map<String,String> entry : metaUtil.getValidEntries()) {
			String stationId = entry.get("id");
			Double[] coordinatesByIdentifier = metaUtil.getCoordinatesByIdentifier(stationId);
			Map<String, Object> metaDataByIdentifier = metaUtil.getMetaDataByIdentifier(stationId);

			StationDto dto = new StationDto();
			dto.setName(stationId);
			dto.setId(stationId);
			dto.setLongitude(coordinatesByIdentifier[0]);
			dto.setLatitude(coordinatesByIdentifier[1]);
			if (!metaDataByIdentifier.isEmpty())
				dto.setMetaData(metaDataByIdentifier);
			stations.add(dto);
		}
		if (!stations.isEmpty())
			pusher.syncStations(stations);
	}

}
