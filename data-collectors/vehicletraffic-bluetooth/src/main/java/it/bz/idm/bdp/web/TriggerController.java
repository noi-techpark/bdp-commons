package it.bz.idm.bdp.web;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
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

	/**
	 * Endpoint call for google notification service
	 * https://developers.google.com/drive/api/v3/push. As soon as the call is
	 * triggered the cache gets invalidated and station synchronize with
	 * the existing BluetoothStations in ODH.
	 *
	 * @param gDto request body of push notification by google as defined in
	 *             https://developers.google.com/drive/api/v3/push
	 */
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody void post(@RequestBody(required = false) GooglePushDto gDto){
		metaUtil.setCachedData(null);
		List<StationDto> odhStations = pusher.fetchStations(pusher.getIntegreenTypology(), null);
		List<String> stationIds= odhStations.stream().map(StationDto::getId).collect(Collectors.toList());
		StationList stations = new StationList();
		for (Map<String,String> entry : metaUtil.getValidEntries()) {
			String stationId = entry.get("id");
			if (!stationIds.contains(stationId))
				continue;
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
