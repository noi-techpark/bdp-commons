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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opendatahub.bdp.radelt.dto.aktionen.AktionenResponseDto;
import com.opendatahub.bdp.radelt.dto.aktionen.RadeltChallengeDto;
import com.opendatahub.bdp.radelt.dto.organisationen.OrganisationenResponseDto;
import com.opendatahub.bdp.radelt.utils.MappingUtilsAktionen;
import com.opendatahub.bdp.radelt.utils.MappingUtilsOrganisationen;
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
import java.util.HashMap;
import java.util.Map;

@Service
public class SyncScheduler {

	private static final Logger LOG = LoggerFactory.getLogger(SyncScheduler.class);

	@Lazy
	@Autowired
	private OdhClient odhClient;

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
	@Scheduled(cron = "${scheduler.actions_and_organization}")
	public void syncJobAktionen() {
		LOG.info("Cron job syncJobAktionen started: Sync Stations with type {} and data types",
				odhClient.getIntegreenTypology());
		// Define base URL for challenges
		String baseUrlChallenges = "https://www.altoadigepedala.bz.it/dashboard/api/opendata/challenges";
		int limit = 5;
		int offset = 0;

		AktionenResponseDto challengeResponseDto;

		while (true) {
			try {
				challengeResponseDto = fetchChallenges(baseUrlChallenges, "true", String.valueOf(limit), String.valueOf(offset), "DISTANCE");
				if (challengeResponseDto == null || challengeResponseDto.getData().getChallenges().size() == 0) {
					break; // No more data to fetch
				}
				MappingUtilsAktionen.mapToStationList(challengeResponseDto, odhClient, this.actionCoordinates, LOG);
				offset += limit; // Increment offset for next page

				for (RadeltChallengeDto challengeDto : challengeResponseDto.getData().getChallenges())
				{
					syncJobOrganisationen(challengeDto);
				}
			} catch (Exception e) {
				e.printStackTrace();
				LOG.error("Error fetching challenges:", e.getMessage());
				break; // Exit the loop if fetching challenges fails
			}
		}

		LOG.info("Cron job syncJobAktionen completed successfully");
	}

	private void syncJobOrganisationen(RadeltChallengeDto challengeDto) {

		LOG.info("Cron job syncJobOrganisationen started: Pushing challenge with id: #" + challengeDto.getId(),
			odhClient.getIntegreenTypology());

		// Define base URL for organizations
		String baseUrlOrganizations = "https://www.suedtirolradelt.bz.it/dashboard/api/opendata/organisations";
		// Initialize pagination variables
		String limit = "5"; // Number of records per page
		String offset = "0"; // Starting offset

		// Fetch and process organizations
		OrganisationenResponseDto organizationResponseDto;
		while (true) {
			try {
				organizationResponseDto = fetchOrganizations(baseUrlOrganizations, String.valueOf(challengeDto.getId()), "", "", limit, offset);
				if (organizationResponseDto == null || organizationResponseDto.getData().getOrganisations().size() == 0) {
					break; // Exit the loop if no more data
				}

				MappingUtilsOrganisationen.mapToStationList(organizationResponseDto, odhClient, this.organizationCoordinates , LOG);

				// Update offset for next page
				offset = String.valueOf(Integer.parseInt(offset) + Integer.parseInt(limit));

			} catch (Exception e) {
				e.printStackTrace();
				LOG.error("Error fetching organizations with challenge id #" + + challengeDto.getId() + " : ", e.getMessage());
				return; // Exit the method if fetching organizations fails
			}
		}

		LOG.info("Cron job Organisationen completed successfully");
	}

	public static AktionenResponseDto fetchChallenges(
		String baseUrl,
		String active,
		String limit,
		String offset,
		String type
	) throws Exception {
		URIBuilder uriBuilder = new URIBuilder(baseUrl);
		uriBuilder.setParameter("active", active);
		uriBuilder.setParameter("limit", limit);
		uriBuilder.setParameter("offset", offset);
		uriBuilder.setParameter("type", type);

		URI uri = uriBuilder.build();
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet request = new HttpGet(uri);

		try (CloseableHttpResponse response = httpClient.execute(request)) {
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					String result = EntityUtils.toString(entity);
					LOG.debug(result);
					ObjectMapper mapper = new ObjectMapper();
					return mapper.readValue(result, AktionenResponseDto.class);
				}
			} else {
				LOG.error("HTTP Request failed with status code: {}", response.getStatusLine().getStatusCode());
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error fetching actions", e);
		}
		return null;
	}

	public static OrganisationenResponseDto fetchOrganizations(
		String baseUrl,
		String challengeId,
		String type,
		String query,
		String limit,
		String offset
	) throws Exception {
		URIBuilder uriBuilder = new URIBuilder(baseUrl);
		uriBuilder.setParameter("challengeId", String.valueOf(challengeId));
		uriBuilder.setParameter("type", type);
		uriBuilder.setParameter("query", query);
		uriBuilder.setParameter("limit", String.valueOf(limit));
		uriBuilder.setParameter("offset", String.valueOf(offset));

		URI uri = uriBuilder.build();
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet request = new HttpGet(uri);
		LOG.info(uri.toString());

		try (CloseableHttpResponse response = httpClient.execute(request)) {
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					String result = EntityUtils.toString(entity);
					ObjectMapper mapper = new ObjectMapper();
					return mapper.readValue(result, OrganisationenResponseDto.class);
				}
			} else {
				LOG.error("HTTP Request failed with status code: {}", response.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error fetching organizations", e);
		}
		return null;
	}
}
