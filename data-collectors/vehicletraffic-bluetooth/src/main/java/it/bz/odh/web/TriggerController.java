package it.bz.odh.web;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;
import it.bz.odh.service.OddsPusher;
import it.bz.odh.util.BluetoothMappingUtil;

@RequestMapping("/trigger")
@Controller
@EnableWebMvc
public class TriggerController {

	private static final String GOOGLE_CONTENT_ID = "content";

	private static final int MINIMAL_SYNC_PAUSE_SECONDS = 60;

	private Logger logger = LoggerFactory.getLogger(TriggerController.class);

	private static Long lastRequest;

	@Autowired
	private OddsPusher pusher;

	@Autowired
	private BluetoothMappingUtil metaUtil;

	/**
	 * Endpoint call for google notification service
	 * https://developers.google.com/drive/api/v3/push. As soon as the call is
	 * triggered the cache gets invalidated and station synchronize with the
	 * existing BluetoothStations in ODH.
	 *
	 * @param gDto request body of push notification by google as defined in
	 *             https://developers.google.com/drive/api/v3/push
	 */
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody void post(@RequestBody(required = false) GooglePushDto gDto,
			@RequestHeader(required = false, value = "x-goog-changed") String whatChanged) {
		logger.debug("Sync triggered");
		List<String> changeDetails = Arrays.asList(whatChanged.split(","));
		Long now = new Date().getTime();
		if (lastRequest == null || lastRequest < now - (MINIMAL_SYNC_PAUSE_SECONDS *1000)) {
			lastRequest = now;
			if (changeDetails.contains(GOOGLE_CONTENT_ID)) {
				logger.debug("Call is content related");
				metaUtil.setCachedData(null);
				logger.debug("Fetch Stations from odh");
				List<StationDto> odhStations = pusher.fetchStations(pusher.getIntegreenTypology(), null);
				List<String> stationIds = odhStations.stream().map(StationDto::getId).collect(Collectors.toList());
				logger.debug("Found "+stationIds.size()+" Bluetoothboxes");
				StationList stations = new StationList();
				for (Map<String, String> entry : metaUtil.getValidEntries()) {
					String stationId = entry.get("id");
					logger.debug("Extract station with id: "+stationId);
					if (!stationIds.contains(stationId))
						continue;
					logger.debug("Map station with id "+stationId);
					Double[] coordinatesByIdentifier = metaUtil.getCoordinatesByIdentifier(stationId);
					Map<String, Object> metaDataByIdentifier = metaUtil.getMetaDataByIdentifier(stationId);
					logger.debug("Start merging translations");
					Map<String,Object> cleanedMap = metaUtil.mergeTranslations(metaDataByIdentifier);
					StationDto dto = new StationDto();
					dto.setName(stationId);
					dto.setId(stationId);
					dto.setLongitude(coordinatesByIdentifier[0]);
					dto.setLatitude(coordinatesByIdentifier[1]);
					if (!cleanedMap.isEmpty())
						dto.setMetaData(cleanedMap);
					stations.add(dto);
					logger.debug("Dto created and added");
				}
				if (!stations.isEmpty()) {
					logger.debug("Push data to odh");
					pusher.syncStations(stations, false, false);
					logger.debug("Finished pushing to odh");
				}
			}
		}
	}

}
