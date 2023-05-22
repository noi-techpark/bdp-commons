package com.opendatahub.matomo.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetPageUrlDto {

    @JsonProperty("label")
    private String label;

    @JsonProperty("nb_visits")
    private Integer nbVisits;

    @JsonProperty("nb_hits")
    private Integer nbHits;

    @JsonProperty("sum_time_spent")
    private Integer sumTimeSpent;

    @JsonProperty("entry_nb_visits")
    private Integer entryNbVisits;

    @JsonProperty("entry_nb_actions")
    private Integer entryNbActions;

    @JsonProperty("entry_sum_visit_length")
    private Integer entrySumVisitLength;

    @JsonProperty("entry_bounce_count")
    private Integer entryBounceCount;

    @JsonProperty("exit_nb_visits")
    private Integer exitNbVisits;

    @JsonProperty("sum_daily_nb_uniq_visitors")
    private Integer sumDailyNbUniqVisitors;

    @JsonProperty("sum_daily_entry_nb_uniq_visitors")
    private Integer sumDailyEntryNbUniqVisitors;

    @JsonProperty("sum_daily_exit_nb_uniq_visitors")
    private Integer sumDailyExitNbUniqVisitors;

    @JsonProperty("avg_time_on_page")
    private Integer avgTimeOnPage;

    @JsonProperty("bounce_rate")
    private String bounceRate;

    @JsonProperty("exit_rate")
    private String exitRate;

    @JsonProperty("url")
    private String url;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getNbVisits() {
        return nbVisits;
    }

    public void setNbVisits(Integer nbVisits) {
        this.nbVisits = nbVisits;
    }

    public Integer getNbHits() {
        return nbHits;
    }

    public void setNbHits(Integer nbHits) {
        this.nbHits = nbHits;
    }

    public Integer getSumTimeSpent() {
        return sumTimeSpent;
    }

    public void setSumTimeSpent(Integer sumTimeSpent) {
        this.sumTimeSpent = sumTimeSpent;
    }

    public Integer getEntryNbVisits() {
        return entryNbVisits;
    }

    public void setEntryNbVisits(Integer entryNbVisits) {
        this.entryNbVisits = entryNbVisits;
    }

    public Integer getEntryNbActions() {
        return entryNbActions;
    }

    public void setEntryNbActions(Integer entryNbActions) {
        this.entryNbActions = entryNbActions;
    }

    public Integer getEntrySumVisitLength() {
        return entrySumVisitLength;
    }

    public void setEntrySumVisitLength(Integer entrySumVisitLength) {
        this.entrySumVisitLength = entrySumVisitLength;
    }

    public Integer getEntryBounceCount() {
        return entryBounceCount;
    }

    public void setEntryBounceCount(Integer entryBounceCount) {
        this.entryBounceCount = entryBounceCount;
    }

    public Integer getExitNbVisits() {
        return exitNbVisits;
    }

    public void setExitNbVisits(Integer exitNbVisits) {
        this.exitNbVisits = exitNbVisits;
    }

    public Integer getSumDailyNbUniqVisitors() {
        return sumDailyNbUniqVisitors;
    }

    public void setSumDailyNbUniqVisitors(Integer sumDailyNbUniqVisitors) {
        this.sumDailyNbUniqVisitors = sumDailyNbUniqVisitors;
    }

    public Integer getSumDailyEntryNbUniqVisitors() {
        return sumDailyEntryNbUniqVisitors;
    }

    public void setSumDailyEntryNbUniqVisitors(Integer sumDailyEntryNbUniqVisitors) {
        this.sumDailyEntryNbUniqVisitors = sumDailyEntryNbUniqVisitors;
    }

    public Integer getSumDailyExitNbUniqVisitors() {
        return sumDailyExitNbUniqVisitors;
    }

    public void setSumDailyExitNbUniqVisitors(Integer sumDailyExitNbUniqVisitors) {
        this.sumDailyExitNbUniqVisitors = sumDailyExitNbUniqVisitors;
    }

    public Integer getAvgTimeOnPage() {
        return avgTimeOnPage;
    }

    public void setAvgTimeOnPage(Integer avgTimeOnPage) {
        this.avgTimeOnPage = avgTimeOnPage;
    }

    public String getBounceRate() {
        return bounceRate;
    }

    public void setBounceRate(String bounceRate) {
        this.bounceRate = bounceRate;
    }

    public String getExitRate() {
        return exitRate;
    }

    public void setExitRate(String exitRate) {
        this.exitRate = exitRate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
