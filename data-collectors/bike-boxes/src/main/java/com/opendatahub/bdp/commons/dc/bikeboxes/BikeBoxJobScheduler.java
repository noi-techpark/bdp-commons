package com.opendatahub.bdp.commons.dc.bikeboxes;

import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.opendatahub.bdp.commons.dc.bikeboxes.config.DataConfig;
import com.opendatahub.bdp.commons.dc.bikeboxes.config.DataTypes;
import com.opendatahub.bdp.commons.dc.bikeboxes.config.ProvenanceConfig;
import com.opendatahub.bdp.commons.dc.bikeboxes.dto.BikeService;
import com.opendatahub.bdp.commons.dc.bikeboxes.dto.BikeStation;
import com.opendatahub.bdp.commons.dc.bikeboxes.dto.BikeStation.Place;
import com.opendatahub.bdp.commons.dc.bikeboxes.services.IBikeBoxesService;
import com.opendatahub.bdp.commons.dc.bikeboxes.services.OdhClient;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;

@Service
public class BikeBoxJobScheduler {
	private static final Logger LOG = LoggerFactory.getLogger(BikeBoxJobScheduler.class);

	@Lazy
	@Autowired
	private OdhClient odhClient;

	@Lazy
	@Autowired
	private IBikeBoxesService bikeBoxesService;

	@Autowired
	private DataConfig dataC;

	@Autowired
	private ProvenanceConfig provC;

	@Scheduled(cron = "${scheduler.job}")
	public void collectBikeBoxData() {
		LOG.info("Cron job started");
		LOG.debug("Getting bike services");
		List<BikeService> services = bikeBoxesService.getBikeServices();

		try {
			LOG.debug("Getting stations");
			List<BikeStation> stations = getAllBikeStations(services);

			if(LOG.isTraceEnabled()){
				LOG.trace("Dumping retrieved stations list:");
				stations.stream().forEach(s -> LOG.trace(s.toString()));
			}

			LOG.debug("Mapping to ODH objects");
			StationList odhStations = new StationList();
			DataMapDto<RecordDtoImpl> odhData = new DataMapDto<>();

			for (BikeStation bs : stations) {
				// create station dto
				StationDto stationDto = new StationDto(bs.idStation, bs.name, bs.latitude, bs.longitude);
				stationDto.setMetaData(Map.of(
						"type", switch (bs.type) {
							case 1 -> "Sharing";
							case 2 -> "Parking";
							default -> "Unknown type " + bs.type;
							// default -> throw new Exception("Unknown mapping station.type: " + bs.type);
						},
						"urlGuide", bs.urlGuide,
						"totalPlaces", bs.totalPlaces,
						"stationPlaces", bs.stationPlaces,
						"maxDistanceRent", bs.maxDistanceRent));
				stationDto.setOrigin(provC.origin);
				odhStations.add(stationDto);

				// create station level measurements
				var stationData = Map.of(
						DataTypes.state.key, mapState(bs.state),
						DataTypes.availableMuscularBikes.key, bs.countMuscularBikesAvailable,
						DataTypes.availableAssistedBikes.key, bs.countAssistedBikesAvailable,
						DataTypes.availableVehicles.key, bs.countFreePlacesAvailable);
				// add the created measurements to odh data list
				stationData.forEach((t, v) -> odhData.addRecord(stationDto.getId(), t, mapSimple(v)));

				// create station and measurement for sub stations (parking bays)
				for (Place bay : bs.stationPlaces) {
					StationDto bayDto = new StationDto(
							stationDto.getId() + "/" + bay.position,
							stationDto.getName() + "/" + bay.position,
							stationDto.getLatitude(),
							stationDto.getLongitude());
					bayDto.setOrigin(stationDto.getOrigin());
					// this parking bay is a child of the parking station
					bayDto.setParentStation(stationDto.getId());

					bayDto.setMetaData(Map.of("type", switch (bay.type) {
						case 1 -> "Normal bay";
						case 2 -> "Bike box";
						default -> "Unknown type " + bay.type;
						// default -> throw new Exception("Unknown mapping station.places.type: " + bay.type);
					}));
					odhStations.add(bayDto);

					// add bay level measurement
					odhData.addRecord(bayDto.getId(), DataTypes.usageState.key, mapSimple(mapState(bay.state)));
				}
			}

			LOG.debug("Pushing data to ODH");
			odhClient.syncStations(odhStations);
			odhClient.syncDataTypes(
					Arrays.stream(DataTypes.values())
							.map(DataTypes::toDataTypeDto)
							.toList());
			odhClient.pushData(odhData);
			LOG.info("Cron job successful");
		} catch (Exception e) {
			LOG.error("Cron job failed: exception: {}", e.getMessage(), e);
		}
	}

	private List<BikeStation> getAllBikeStations(List<BikeService> services) {
		List<BikeStation> stations = services.stream()
				.flatMap(service -> Arrays.stream(service.cities))
				.map(city -> city.idCity)
				.distinct()
				// now we have a unique list of city IDs where the service is available
				// let's get the stations for all cities
				.flatMap(city -> bikeBoxesService.getBikeStations(city).stream())
				// The stationS service does not give us all the detail we need.
				// let's get the detailed info with real time data
				.map(station -> bikeBoxesService.getBikeStation(station.idStation))
				.collect(Collectors.toList());
		return stations;
	}

	private String mapState(int state) throws Exception {
		return switch (state) {
			case 1 -> "FREE";
			case 2 -> "OCCUPIED";
			case 3 -> "OUT OF SERVICE";
			default -> "UNKNOWN STATE " + state;
			// default -> throw new Exception("Unable to map Station.place.state : " + state);
		};
	}

	private SimpleRecordDto mapSimple(Object value) {
		return new SimpleRecordDto(System.currentTimeMillis(), value, dataC.period);
	}
}
