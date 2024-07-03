// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.bdp.radelt.utils;

import org.springframework.stereotype.Component;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import com.opendatahub.bdp.radelt.dto.organisationen.RadeltChallengeStatisticDto;
import com.opendatahub.bdp.radelt.dto.organisationen.RadeltOrganisationenDto;
import com.opendatahub.bdp.radelt.dto.organisationen.OrganisationenResponseDto;
import com.opendatahub.bdp.radelt.dto.organisationen.RadeltStatisticsDto;
import com.opendatahub.bdp.radelt.dto.common.RadeltGeoDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

@Component
public class MappingUtilsOrganisationen {

	private final Logger LOG = LoggerFactory.getLogger(MappingUtilsOrganisationen.class);

	public final String DATA_ORIGIN = "SuedtirolRadelt_AltoAdigePedala";
	public final String DATA_TYPE = "CompanyGamificationAction";

	public void mapData(OrganisationenResponseDto responseDto,
			Map<String, RadeltGeoDto> organizationCoordinates, StationList stationList,
			DataMapDto<RecordDtoImpl> rootMap) {

		for (RadeltOrganisationenDto organisationenDto : responseDto.getData().getOrganisations()) {
			StationDto stationDto = mapToStationDto(organisationenDto, organizationCoordinates);
			if (stationDto.getId() == null) {// TODO: handle duplicated entries?
				LOG.debug("Skipping station with empty statistics");
				continue;
			}
			stationList.add(stationDto);

			// Create measurement records
			DataMapDto<RecordDtoImpl> stationMap = rootMap.upsertBranch(stationDto.getId());

			for (RadeltChallengeStatisticDto challengeStatisticDto : organisationenDto.getStatistics()
					.getChallengeStatistics()) {
				long timestamp = challengeStatisticDto.getCreated_at();

				DataTypeUtils.addMeasurement(stationMap, "km_total", timestamp, challengeStatisticDto.getKm_total());
				DataTypeUtils.addMeasurement(stationMap, "height_meters_total", timestamp,
						challengeStatisticDto.getHeight_meters_total());
				DataTypeUtils.addMeasurement(stationMap, "km_average", timestamp,
						challengeStatisticDto.getKm_average());
				DataTypeUtils.addMeasurement(stationMap, "kcal", timestamp, challengeStatisticDto.getKcal());
				DataTypeUtils.addMeasurement(stationMap, "co2", timestamp, challengeStatisticDto.getCo2());
				DataTypeUtils.addMeasurement(stationMap, "m2_trees", timestamp, challengeStatisticDto.getM2_trees());
				DataTypeUtils.addMeasurement(stationMap, "money_saved", timestamp,
						challengeStatisticDto.getMoney_saved());
				DataTypeUtils.addMeasurement(stationMap, "number_of_people", timestamp,
						challengeStatisticDto.getNumber_of_people());
			}

		}

	}

	private StationDto mapToStationDto(RadeltOrganisationenDto organisation,
			Map<String, RadeltGeoDto> organizationCoordinates) {
		StationDto stationDto = new StationDto();

		RadeltStatisticsDto statisticsDto = organisation.getStatistics();

		if (statisticsDto != null) {
			RadeltChallengeStatisticDto challengeStatistics = statisticsDto.getChallengeStatistics().get(0);
			if (challengeStatistics.getChallenge_id() != null) {
				stationDto.setId(String.valueOf(challengeStatistics.getId()) + "-"
						+ String.valueOf(challengeStatistics.getChallenge_id()));
				stationDto.setName(challengeStatistics.getName() + '-' + challengeStatistics.getChallenge_name());
				stationDto.setStationType(DATA_TYPE);

				// METADATA
				stationDto.getMetaData().put("type", organisation.getType());
				stationDto.getMetaData().put("logo", organisation.getLogo());
				stationDto.getMetaData().put("website", organisation.getWebsite());
				stationDto.getMetaData().put("peopleTotal", organisation.getPeopleTotal());
				stationDto.getMetaData().put("challenge_type", challengeStatistics.getChallenge_type());

				// Additional
				stationDto.setOrigin(DATA_ORIGIN);

				// Geo info from csv
				RadeltGeoDto organizationGeoDto = organizationCoordinates.get(stationDto.getId());
				if (organizationGeoDto != null) {
					stationDto.setLongitude(organizationGeoDto.getLongitude());
					stationDto.setLatitude(organizationGeoDto.getLatitude());
					LOG.info("Coordinates saved for station: {}, lat: {}, lon: {}", stationDto.getId(),
							organizationGeoDto.getLatitude(), organizationGeoDto.getLongitude());
				} else {
					LOG.debug("No coordinates found on csv for station with id: #" + stationDto.getId());
				}

				stationDto.setStationType(DATA_TYPE);
			}
		}

		return stationDto;
	}

}
