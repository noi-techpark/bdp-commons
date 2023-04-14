package com.opendatahub.matomo.api;

import com.fasterxml.jackson.annotation.JsonProperty;


public class CustomReportDto {

    public String label;

    @JsonProperty("nb_uniq_visitors")
    public int uniqueVisitors;

    @JsonProperty("nb_visits")
    public int visits;

    public int level;

    @JsonProperty("CoreHome_VisitsCount")
    public String coreHomeVisitsCount;

    @JsonProperty("Actions_PageUrl")
    public String actionsPageUrl;
}
