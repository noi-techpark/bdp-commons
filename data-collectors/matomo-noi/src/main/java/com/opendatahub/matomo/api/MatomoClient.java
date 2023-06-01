// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.matomo.api;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class MatomoClient {

    @Value("${matomo.api.token}")
    private String token;

    @Value("${matomo.api.baseurl}")
    private String baseUrl;

    @Value("${matomo.api.siteid}")
    private int siteId;

    @Value("${matomo.api.reportid}")
    private Integer reportId;

    @Value("#{'${matomo.api.pages}'.split(',')}")
    private List<String> pages;

    private String pagesMethod = "Actions.getPageUrl";

    private String reportMethod = "Actions.CustomReports.getCustomReport";

    // for simplicity yesterday is used
    // works also for period year/month/week and uses current year/month/week
    private String date = "yesterday";

    private RestTemplate restTemplate;

    @PostConstruct
    private void postConstruct() {
        restTemplate = new RestTemplate();

        // make sure base url ends with /
        if (!baseUrl.endsWith("/"))
            baseUrl += "/";

        // append base parameters for all calls
        baseUrl += "?idSite=" + siteId;
        baseUrl += "&date=" + date;
        baseUrl += "&token_auth=" + token;

        // needs to be last param, because method will be different
        baseUrl += "&format=JSON&flat=1&module=API&method=";
    }

    public CustomReportDto[] getReportData(String period) {

        if (reportId == null || reportId <= 0) {
            CustomReportDto[] emptyReport = new CustomReportDto[0];
            return emptyReport;
        }

        String url = this.baseUrl + this.reportMethod;

        url += "&period=" + period;
        url += "&idCustomReport=" + reportId;

        return restTemplate.getForObject(url, CustomReportDto[].class);
    }

    public List<GetPageUrlDto> getPageUrlData(String period) {

        List<GetPageUrlDto> pagesData = new ArrayList<>();

        for (String page : pages) {
            String url = this.baseUrl + this.pagesMethod;

            url += "&period=" + period;
            url += "&pageUrl=" + page;

            GetPageUrlDto[] getPageUrlDtos = restTemplate.getForObject(url, GetPageUrlDto[].class);

            if (getPageUrlDtos != null && getPageUrlDtos.length > 0) {

                // use page defined in .env as id, because multiple urls could be used for same
                // site like
                // https://noi.bz.it/it/societa-trasparente
                // https://noi.dexanet.biz/it/societa-trasparente
                GetPageUrlDto pageDto = getPageUrlDtos[0];
                pageDto.setUrl(page.replace("https://", ""));
                pagesData.add(pageDto);

            }
        }

        return pagesData;
    }
}
