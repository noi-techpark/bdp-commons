package com.opendatahub.bdp.radelt;

import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opendatahub.bdp.radelt.dto.aktionen.AktionenResponseDto;
import com.opendatahub.bdp.radelt.dto.organisationen.OrganisationenResponseDto;

@Component
public class RadeltAPIClient {

    private final Logger LOG = LoggerFactory.getLogger(RadeltAPIClient.class);

    private final String URL_CHALLENGES = "https://www.altoadigepedala.bz.it/dashboard/api/opendata/challenges";
    private final String URL_ORGANIZATIONS = "https://www.suedtirolradelt.bz.it/dashboard/api/opendata/organisations";

    public AktionenResponseDto fetchChallenges(String active, String limit, String offset, String type)
            throws Exception {
        URIBuilder uriBuilder = new URIBuilder(URL_CHALLENGES);
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
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error fetching actions", e);
        }
        return null;
    }

    public OrganisationenResponseDto fetchOrganizations(String challengeId, String type,
            String query, String limit, String offset) throws Exception {
        URIBuilder uriBuilder = new URIBuilder(URL_ORGANIZATIONS);
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
