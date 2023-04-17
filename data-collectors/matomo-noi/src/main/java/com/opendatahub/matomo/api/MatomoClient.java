package com.opendatahub.matomo.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class MatomoClient {

    @Value("${matomo.api.token}")
    private String token;

    @Value("${matomo.api.url}")
    private String url;

    private RestTemplate restTemplate;

    public MatomoClient() {
        restTemplate = new RestTemplate();
    }

    public CustomReportDto[] getReportData() {
        return restTemplate.getForObject(url + token, CustomReportDto[].class);
    }
}
