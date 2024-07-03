// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.bdp.radelt.dto.utils;

import org.springframework.web.reactive.function.client.WebClientRequestException;
import java.time.ZoneOffset;

import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import com.opendatahub.bdp.radelt.dto.organisationen.RadeltChallengeStatisticDto;
import com.opendatahub.bdp.radelt.dto.organisationen.RadeltOrganisationenDto;
import com.opendatahub.bdp.radelt.dto.organisationen.OrganisationenResponseDto;
import com.opendatahub.bdp.radelt.dto.organisationen.RadeltStatisticsDto;
import com.opendatahub.bdp.radelt.dto.common.RadeltGeoDto;
import com.opendatahub.bdp.radelt.OdhClient;
import org.slf4j.Logger;
import java.util.HashMap;
import java.util.Map;


public class MappingUtilsOrganisationen {

	public static final String DATA_ORIGIN = "SuedtirolRadelt_AltoAdigePedala";
	public static final String DATA_TYPE = "CompanyGamificationAction";

	public static StationDto mapToStationDto(RadeltOrganisationenDto organisation, Map<String, RadeltGeoDto> organizationCoordinates, Logger LOG) {
		StationDto stationDto = new StationDto();

		RadeltStatisticsDto statisticsDto = organisation.getStatistics();

		if(statisticsDto != null) {
			RadeltChallengeStatisticDto challengeStatistics = statisticsDto.getChallengeStatistics().get(0);
			if (challengeStatistics.getChallenge_id()  != null) {
				stationDto.setId(String.valueOf(challengeStatistics.getId()) + "-" + String.valueOf(challengeStatistics.getChallenge_id()));
				stationDto.setName(challengeStatistics.getName() + '-' + challengeStatistics.getChallenge_name());
				stationDto.setStationType(DATA_TYPE);

				//METADATA
				stationDto.getMetaData().put("type", organisation.getType());
				stationDto.getMetaData().put("logo", organisation.getLogo());
				stationDto.getMetaData().put("website", organisation.getWebsite());
				stationDto.getMetaData().put("peopleTotal", organisation.getPeopleTotal());
				stationDto.getMetaData().put("challenge_type", challengeStatistics.getChallenge_type());

				//Additional
				stationDto.setOrigin(DATA_ORIGIN);

				//Geo info from csv
				RadeltGeoDto organizationGeoDto = organizationCoordinates.get(stationDto.getId());
				if (organizationGeoDto != null) {
					stationDto.setLongitude(organizationGeoDto.getLongitude());
					stationDto.setLatitude(organizationGeoDto.getLatitude());
					LOG.info("Coordinates saved for station: #" + stationDto.getId());
					LOG.info("Longitude: " + organizationGeoDto.getLongitude());
					LOG.info("Latitude: " + organizationGeoDto.getLatitude());
				}else{
					LOG.info("No coordinates found on csv for station with id: #" + stationDto.getId());
				}

				stationDto.setStationType(DATA_TYPE);
			}
		}

		return stationDto;
	}

	public static void mapToStationList(OrganisationenResponseDto responseDto, OdhClient odhClient, Map<String, RadeltGeoDto> organizationCoordinates, Logger LOG) {
		LOG.info("Start mapping challenge");
		StationList stationListOrganisationen = new StationList();

		for (RadeltOrganisationenDto organisationenDto : responseDto.getData().getOrganisations()) {
			StationDto stationDto = mapToStationDto(organisationenDto, organizationCoordinates, LOG);

			if (stationDto.getId() == null) {//TODO: handle duplicated entries?
				LOG.error("Skipping station with empty statistics");
				return;
			}

			stationListOrganisationen.add(stationDto);
			LOG.info("Add station (organizazion) with id #" + stationDto.getId());

			// Create measurement records
			DataMapDto<RecordDtoImpl> rootMap = new DataMapDto<>();
			DataMapDto<RecordDtoImpl> stationMap = rootMap.upsertBranch(stationDto.getId());



			for (RadeltChallengeStatisticDto challengeStatisticDto : organisationenDto.getStatistics().getChallengeStatistics()){
				long timestamp = challengeStatisticDto.getCreated_at();

				LOG.info("add measurement with ts: " + timestamp);
				DataTypeUtils.addMeasurement(stationMap, "km_total", timestamp, challengeStatisticDto.getKm_total());
				DataTypeUtils.addMeasurement(stationMap, "height_meters_total", timestamp, challengeStatisticDto.getHeight_meters_total());
				DataTypeUtils.addMeasurement(stationMap, "km_average", timestamp, challengeStatisticDto.getKm_average());
				DataTypeUtils.addMeasurement(stationMap, "kcal", timestamp, challengeStatisticDto.getKcal());
				DataTypeUtils.addMeasurement(stationMap, "co2", timestamp, challengeStatisticDto.getCo2());
				DataTypeUtils.addMeasurement(stationMap, "m2_trees", timestamp, challengeStatisticDto.getM2_trees());
				DataTypeUtils.addMeasurement(stationMap, "money_saved", timestamp, challengeStatisticDto.getMoney_saved());
				DataTypeUtils.addMeasurement(stationMap, "number_of_people", timestamp, challengeStatisticDto.getNumber_of_people());
			}

			// Push data
			try {
				odhClient.pushData(stationMap);
				LOG.info("Pushing data successful");
			} catch (WebClientRequestException e) {
				LOG.error("Pushing data failed: Request exception:", e.getMessage());
			}
		}

		// Sync stations
		try {
			odhClient.syncStations(stationListOrganisationen);
			LOG.info("Syncing stations successful");
		} catch (WebClientRequestException e) {
			LOG.error("Syncing stations failed: Request exception: {}", e.getMessage());
		}
	}
}
