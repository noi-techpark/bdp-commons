// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.bdp.radelt.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;

import com.opendatahub.bdp.radelt.dto.organisationen.RadeltChallengeStatisticDto;
import com.opendatahub.bdp.radelt.dto.organisationen.RadeltOrganisationenDto;
import com.opendatahub.bdp.radelt.dto.organisationen.OrganisationenResponseDto;
import com.opendatahub.bdp.radelt.dto.organisationen.RadeltStatisticsDto;
import com.opendatahub.bdp.radelt.dto.common.RadeltGeoDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@Component
public class MappingUtilsOrganisationen {

	private final Logger LOG = LoggerFactory.getLogger(MappingUtilsOrganisationen.class);

	@Value("${odh_client.period}")
	private Integer period;

	public final String DATA_ORIGIN = "SuedtirolRadelt_AltoAdigePedala";
	public final String DATA_TYPE = "CompanyGamificationAction";

	public void mapData(OrganisationenResponseDto responseDtoGer, OrganisationenResponseDto responseDtoIta,
			Map<String, RadeltGeoDto> organizationCoordinates, StationList stationList,
			DataMapDto<RecordDtoImpl> rootMap) {

		List<RadeltOrganisationenDto> organisationsGer = responseDtoGer.getData().getOrganisations();
		List<RadeltOrganisationenDto> organisationsIta = responseDtoIta.getData().getOrganisations();

		for (int i = 0; i < organisationsGer.size(); i++) {

			StationDto stationDto = mapToStationDto(organisationsGer.get(i), organisationsIta.get(i),
					organizationCoordinates);
			if (stationDto.getId() == null) {// TODO: handle duplicated entries?
				LOG.debug("Skipping station with empty statistics");
				continue;
			}
			stationList.add(stationDto);

			for (RadeltChallengeStatisticDto challengeStatisticDto : organisationsGer.get(i).getStatistics()
					.getChallengeStatistics()) {
				long timestamp = challengeStatisticDto.getCreated_at();

				rootMap.addRecord(stationDto.getId(), "km_total",
						new SimpleRecordDto(timestamp, challengeStatisticDto.getKm_total(), period));
				rootMap.addRecord(stationDto.getId(), "height_meters_total",
						new SimpleRecordDto(timestamp, challengeStatisticDto.getHeight_meters_total(), period));
				rootMap.addRecord(stationDto.getId(), "km_average",
						new SimpleRecordDto(timestamp, challengeStatisticDto.getKm_average(), period));
				rootMap.addRecord(stationDto.getId(), "kcal",
						new SimpleRecordDto(timestamp, challengeStatisticDto.getKcal(), period));
				rootMap.addRecord(stationDto.getId(), "co2",
						new SimpleRecordDto(timestamp, challengeStatisticDto.getCo2(), period));
				rootMap.addRecord(stationDto.getId(), "m2_trees",
						new SimpleRecordDto(timestamp, challengeStatisticDto.getM2_trees(), period));
				rootMap.addRecord(stationDto.getId(), "money_saved",
						new SimpleRecordDto(timestamp, challengeStatisticDto.getMoney_saved(), period));
				rootMap.addRecord(stationDto.getId(), "number_of_people",
						new SimpleRecordDto(timestamp, challengeStatisticDto.getNumber_of_people(), period));
			}

		}

	}

	private StationDto mapToStationDto(RadeltOrganisationenDto organisationGer, RadeltOrganisationenDto organisationIta,
			Map<String, RadeltGeoDto> organizationCoordinates) {
		StationDto stationDto = new StationDto();

		RadeltStatisticsDto statisticsDto = organisationGer.getStatistics();

		if (statisticsDto != null) {
			RadeltChallengeStatisticDto challengeStatistics = statisticsDto.getChallengeStatistics().get(0);
			if (challengeStatistics.getChallenge_id() != null) {
				stationDto.setId(String.valueOf(challengeStatistics.getId()) + "-"
						+ String.valueOf(challengeStatistics.getChallenge_id()));
				stationDto.setName(organisationGer.getName() + "_"
						+ organisationIta.getName() + '-' + challengeStatistics.getChallenge_name());
				stationDto.setStationType(DATA_TYPE);

				// METADATA
				stationDto.getMetaData().put("type", organisationGer.getType());
				stationDto.getMetaData().put("logo", organisationGer.getLogo());
				stationDto.getMetaData().put("website", organisationGer.getWebsite());
				stationDto.getMetaData().put("peopleTotal", organisationGer.getPeopleTotal());
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
