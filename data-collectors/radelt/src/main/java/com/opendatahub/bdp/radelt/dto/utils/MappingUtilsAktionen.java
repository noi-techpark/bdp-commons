// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.bdp.radelt.dto.utils;

import java.util.List;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import java.util.ArrayList;
import java.time.ZoneOffset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;


import com.opendatahub.bdp.radelt.dto.aktionen.AktionenResponseDto;
import com.opendatahub.bdp.radelt.dto.aktionen.RadeltChallengeDto;
import com.opendatahub.bdp.radelt.dto.aktionen.RadeltStatisticDto;
import com.opendatahub.bdp.radelt.dto.aktionen.RadeltChallengeMetric;
import com.opendatahub.bdp.radelt.dto.common.RadeltGeoDto;

import com.opendatahub.bdp.radelt.OdhClient;
import org.slf4j.Logger;
import java.util.HashMap;
import java.util.Map;

public class MappingUtilsAktionen {

	public static final String DATA_ORIGIN = "SuedtirolRadelt_AltoAdigePedala";
	public static final String DATA_TYPE = "GamificationAction";

	public static StationDto mapToStationDto(RadeltChallengeDto challengeDto, Map<String, RadeltGeoDto> actionCoordinates, Logger LOG) {
		StationDto stationDto = new StationDto();
		stationDto.setId(String.valueOf(challengeDto.getId()));
		stationDto.setName(challengeDto.getName());

		//METADATA
		stationDto.getMetaData().put("shortName", challengeDto.getShortName());
		stationDto.getMetaData().put("Slug", SlugUtils.getSlug(challengeDto.getShortName()));
		stationDto.getMetaData().put("headerImage", challengeDto.getHeaderImage());
		stationDto.getMetaData().put("Start", challengeDto.getStart());
		stationDto.getMetaData().put("End", challengeDto.getEnd());
		stationDto.getMetaData().put("registrationStart", challengeDto.getRegistrationStart());
		stationDto.getMetaData().put("registrationEnd", challengeDto.getRegistrationEnd());
		stationDto.getMetaData().put("type", challengeDto.getType());
		stationDto.getMetaData().put("isExternal", challengeDto.isExternal());
		stationDto.getMetaData().put("canOrganisationsSignup", challengeDto.isCanOrganisationsSignup());

		//Additional
		stationDto.setOrigin(DATA_ORIGIN);
		//Geo info from csv
		RadeltGeoDto actionGeoDto = actionCoordinates.get(stationDto.getId());
		if (actionGeoDto != null) {
			stationDto.setLongitude(actionGeoDto.getLongitude());
			stationDto.setLatitude(actionGeoDto.getLatitude());
			LOG.info("Coordinates saved for station: #" + stationDto.getId());
			LOG.info("Longitude " + actionGeoDto.getLongitude());
			LOG.info("Latitude " + actionGeoDto.getLatitude());
		}else{
			LOG.info("No coordinates found on csv for station with id: #" + stationDto.getId());
		}
		stationDto.setStationType(DATA_TYPE);

		return stationDto;
	}

	public static void mapToStationList(AktionenResponseDto responseDto, OdhClient odhClient, Map<String, RadeltGeoDto> actionCoordinates, Logger LOG) {
		StationList stationListAktionen = new StationList();
		List<DataTypeDto> odhDataTypeList = new ArrayList<>();

		for (RadeltChallengeDto challengeDto : responseDto.getData().getChallenges()) {
			//Create Station
			StationDto stationDto = mapToStationDto(challengeDto, actionCoordinates, LOG);

			if(stationDto.getId() != null){//TODO: handle duplicated entries?
				stationListAktionen.add(stationDto);
				LOG.info("Add station with id #" + stationDto.getId());
			}

			// Create measurement records
			DataMapDto<RecordDtoImpl> dataMap = new DataMapDto<>();
			DataMapDto<RecordDtoImpl> stationMap = dataMap.upsertBranch(stationDto.getId());

			RadeltStatisticDto statistics = challengeDto.getStatistics();

			if(statistics != null && statistics.getChallenge() != null){
				RadeltChallengeMetric challenge = statistics.getChallenge();
				long timestamp = challenge.getCreated_at();
				LOG.info("add measurement with ts: " + timestamp);
				DataTypeUtils.addMeasurement(stationMap, "km_total", timestamp, challenge.getKm_total());
				DataTypeUtils.addMeasurement(stationMap, "height_meters_total", timestamp, challenge.getHeight_meters_total());
				DataTypeUtils.addMeasurement(stationMap, "km_average", timestamp, challenge.getKm_average());
				DataTypeUtils.addMeasurement(stationMap, "kcal", timestamp, challenge.getKcal());
				DataTypeUtils.addMeasurement(stationMap, "co2", timestamp, challenge.getCo2());
				DataTypeUtils.addMeasurement(stationMap, "m2_trees", timestamp, challenge.getM2_trees());
				DataTypeUtils.addMeasurement(stationMap, "money_saved", timestamp, challenge.getMoney_saved());
				DataTypeUtils.addMeasurement(stationMap, "number_of_people", timestamp, challenge.getNumber_of_people());
				DataTypeUtils.addMeasurement(stationMap, "organisation_count", timestamp, challenge.getOrganisation_count());
				DataTypeUtils.addMeasurement(stationMap, "workplace_count", timestamp, challenge.getWorkplace_count());
				DataTypeUtils.addMeasurement(stationMap, "school_count", timestamp, challenge.getSchool_count());
				DataTypeUtils.addMeasurement(stationMap, "municipality_count", timestamp, challenge.getMunicipality_count());
				DataTypeUtils.addMeasurement(stationMap, "association_count", timestamp, challenge.getAssociation_count());
				DataTypeUtils.addMeasurement(stationMap, "university_count", timestamp, challenge.getUniversity_count());

				// Push data
				try {
					odhClient.pushData(stationMap);
					LOG.info("Pushing data successful");
				} catch (WebClientRequestException e) {
					LOG.error("Pushing data failed: Request exception: {}", e.getMessage());
				}
			}
		}

		// Sync stations
		try {
			odhClient.syncStations(stationListAktionen);
			LOG.info("Syncing stations successful");
		} catch (WebClientRequestException e) {
			LOG.error("Syncing stations failed: Request exception: {}", e.getMessage());
		}
	}
}
