package com.opendatahub.matomo.api;

import com.fasterxml.jackson.annotation.JsonProperty;


public class CustomReportDto {

    private String label;

    @JsonProperty("nb_uniq_visitors")
    private int uniqueVisitors;

    @JsonProperty("nb_visits")
    private int visits;

    private int level;

    @JsonProperty("CoreHome_VisitsCount")
    private String coreHomeVisitsCount;

    @JsonProperty("Actions_PageUrl")
    private String actionsPageUrl;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getUniqueVisitors() {
        return uniqueVisitors;
    }

    public void setUniqueVisitors(int uniqueVisitors) {
        this.uniqueVisitors = uniqueVisitors;
    }

    public int getVisits() {
        return visits;
    }

    public void setVisits(int visits) {
        this.visits = visits;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getCoreHomeVisitsCount() {
        return coreHomeVisitsCount;
    }

    public void setCoreHomeVisitsCount(String coreHomeVisitsCount) {
        this.coreHomeVisitsCount = coreHomeVisitsCount;
    }

    public String getActionsPageUrl() {
        return actionsPageUrl;
    }

    public void setActionsPageUrl(String actionsPageUrl) {
        this.actionsPageUrl = actionsPageUrl;
    }

    
}
