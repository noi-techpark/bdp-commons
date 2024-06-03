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
import com.opendatahub.bdp.radelt.dto.organisationen.OrganisationenResponseDto;
import com.opendatahub.bdp.radelt.dto.utils.MappingUtilsAktionen;
import com.opendatahub.bdp.radelt.dto.utils.MappingUtilsOrganisationen;
import com.opendatahub.bdp.radelt.dto.utils.DataTypeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.utils.URIBuilder;
import java.net.URI;

@Service
public class SyncScheduler {

	private static final Logger LOG = LoggerFactory.getLogger(SyncScheduler.class);

    @Lazy
    @Autowired
    private OdhClient odhClient;

	@PostConstruct
	private void syncDataTypes() {
		DataTypeUtils.setupDataType(odhClient, LOG);
	}

	/**
     * Scheduled job Aktionen
     */
    @Scheduled(cron = "${scheduler.syncJobAktionen}")
	public void syncJobAktionen() {
		LOG.info("Cron job syncJobAktionen started: Sync Stations with type {} and data types", odhClient.getIntegreenTypology());
		// Define base URL for challenges
		String baseUrlChallenges = "https://www.altoadigepedala.bz.it/dashboard/api/opendata/challenges";
		// Fetch and process challenges
		AktionenResponseDto challengeResponseDto;
		try {
			challengeResponseDto = fetchChallenges(baseUrlChallenges, "true", "5", "0", "");
		} catch (Exception e) {
			LOG.error("Error fetching challenges: {}", e.getMessage());
			return; // Exit the method if fetching challenges fails
		}

		MappingUtilsAktionen.mapToStationList(challengeResponseDto, odhClient, LOG);

		LOG.info("Cron job syncJobAktionen completed successfully");
	}
	/**
     * Scheduled job Organisationen
     */
    @Scheduled(cron = "${scheduler.syncJobOrganisationen}")
	public void syncJobOrganisationen() {
		LOG.info("Cron job syncJobOrganisationen started: Pushing measurements for {}", odhClient.getIntegreenTypology());

		// Define base URL for organizations
		String baseUrlOrganizations = "https://www.suedtirolradelt.bz.it/dashboard/api/opendata/organisations";
		// Fetch and process organizations
		OrganisationenResponseDto organizationResponseDto;
		try {
			organizationResponseDto = fetchOrganizations(baseUrlOrganizations, "280", "", "", "10", "0");
		} catch (Exception e) {
			LOG.error("Error fetching organizations: {}", e.getMessage());
			return; // Exit the method if fetching organizations fails
		}

		MappingUtilsOrganisationen.mapToStationList(organizationResponseDto, odhClient, LOG);

		LOG.info("Cron job Organisationen completed successfully");
	}

	public static AktionenResponseDto fetchChallenges(String baseUrl, String active, String limit, String offset, String type) throws Exception {
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
					System.out.println(result);
					ObjectMapper mapper = new ObjectMapper();
					return mapper.readValue(result, AktionenResponseDto.class);
				}
			} else {
				System.out.println("HTTP Request failed with status code: " + response.getStatusLine().getStatusCode());
			}
		}
		return null;
	}

	public static OrganisationenResponseDto fetchOrganizations(String baseUrl, String challengeId, String type, String query, String limit, String offset) throws Exception {
		URIBuilder uriBuilder = new URIBuilder(baseUrl);
		uriBuilder.setParameter("challengeId", String.valueOf(challengeId));
		uriBuilder.setParameter("type", type);
		uriBuilder.setParameter("query", query);
		uriBuilder.setParameter("limit", String.valueOf(limit));
		uriBuilder.setParameter("offset", String.valueOf(offset));

		URI uri = uriBuilder.build();
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet request = new HttpGet(uri);

		try (CloseableHttpResponse response = httpClient.execute(request)) {
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					String result = EntityUtils.toString(entity);
					//System.out.println(result);
					ObjectMapper mapper = new ObjectMapper();
					return mapper.readValue(result, OrganisationenResponseDto.class);
				}
			} else {
				System.out.println("HTTP Request failed with status code: " + response.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error fetching organizations", e);
		}
		return null;
	}
}

