// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.bdp.radelt;

import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opendatahub.bdp.radelt.dto.aktionen.AktionenResponseDto;
import com.opendatahub.bdp.radelt.dto.aktionen.RadeltChallengeDto;
import com.opendatahub.bdp.radelt.dto.organisationen.OrganisationenResponseDto;
import com.opendatahub.bdp.radelt.utils.MappingUtilsAktionen;
import com.opendatahub.bdp.radelt.utils.MappingUtilsOrganisationen;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationList;

import com.opendatahub.bdp.radelt.utils.DataTypeUtils;
import com.opendatahub.bdp.radelt.utils.CsvImporter;
import com.opendatahub.bdp.radelt.dto.common.RadeltGeoDto;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.utils.URIBuilder;
import java.net.URI;
import java.util.Map;

@Service
public class SyncScheduler {

	private static final Logger LOG = LoggerFactory.getLogger(SyncScheduler.class);

	@Lazy
	@Autowired
	private OdhClient odhClient;

	@Autowired
	private RadeltAPIClient radelClient;

	@Autowired
	private MappingUtilsOrganisationen mappingUtilsOrganisationen;

	@Autowired
	private MappingUtilsAktionen mappingUtilsAktionen;

	private Map<String, RadeltGeoDto> actionCoordinates;
	private Map<String, RadeltGeoDto> organizationCoordinates;

	@PostConstruct
	private void syncDataTypes() {
		DataTypeUtils.setupDataType(odhClient, LOG);
	}

	@PostConstruct
	private void syncCsvInfo() {
		this.actionCoordinates = CsvImporter.syncCsvActions();
		this.organizationCoordinates = CsvImporter.syncCsvOrganizations();
	}

	/**
	 * Scheduled job Aktionen
	 */
	@Scheduled(cron = "${scheduler.cron}")
	public void syncJob() {
		LOG.info("Cron job syncJobAktionen started: Sync Stations with type {} and data types",
				odhClient.getIntegreenTypology());

		StationList stationsOrganisationen = new StationList();
		DataMapDto<RecordDtoImpl> dataOrganisationen = new DataMapDto<>();

		StationList stationsChallenges = new StationList();
		DataMapDto<RecordDtoImpl> dataChallenges = new DataMapDto<>();

		AktionenResponseDto challengeResponseDto;

		try {
			challengeResponseDto = radelClient.fetchChallenges("true", "DISTANCE");

			mappingUtilsAktionen.mapData(challengeResponseDto, this.actionCoordinates, stationsChallenges,
					dataChallenges);

			for (RadeltChallengeDto challengeDto : challengeResponseDto.getData().getChallenges()) {
				// Define base URL for organizations
				// Initialize pagination variables
				// Fetch and process organizations
				OrganisationenResponseDto organizationResponseDto;
				try {
					organizationResponseDto = radelClient.fetchOrganizations(
							String.valueOf(challengeDto.getId()),
							"", "");

					mappingUtilsOrganisationen.mapData(organizationResponseDto,
							this.organizationCoordinates, stationsOrganisationen, dataOrganisationen);

				} catch (Exception e) {
					e.printStackTrace();
					LOG.error("Error fetching organizations with challenge id {}. Error message: {}",
							challengeDto.getId(),
							e.getMessage());
					return; // Exit the method if fetching organizations fails
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Error fetching challenges:", e.getMessage());
		}

		// Sync with Open Data Hub
		try {
			odhClient.syncStations(stationsOrganisationen);
			odhClient.syncStations(stationsChallenges);
			LOG.info("Syncing stations successful");
			odhClient.pushData(dataOrganisationen);
			odhClient.pushData(dataChallenges);
			LOG.info("Pushing data successful");
		} catch (WebClientRequestException e) {
			LOG.error("Syncing stations failed: Request exception: {}", e.getMessage());
		}

		LOG.info("Cron job completed successfully");
	}
}
