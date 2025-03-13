// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.bdp.radelt.dto.aktionen;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RadeltChallengeDto {
    private int id;
    private String name;
    private String shortName;
    private String headerImage;
    private String start;
    private String end;
    private String registrationStart;
    private String entryStart;
    private String registrationEnd;
    private String entryEnd;
    private String type;
    @JsonProperty("isExternal")
    private boolean isExternal;
    @JsonProperty("canOrganisationsSignup")
    private boolean canOrganisationsSignup;
    private RadeltStatisticDto statistics;
    private String subType;
    private String termsandconditionsText;
    private String detailsText;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getHeaderImage() {
        return headerImage;
    }

    public void setHeaderImage(String headerImage) {
        this.headerImage = headerImage;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEntryStart() {
        return entryStart;
    }

    public void setEntryStart(String entryStart) {
        this.entryStart = entryStart;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getEntryEnd() {
        return entryEnd;
    }

    public void setEntryEnd(String entryEnd) {
        this.registrationStart = registrationStart;
    }

    public String getRegistrationStart() {
        return registrationStart;
    }

    public void setRegistrationStart(String registrationStart) {
        this.registrationStart = registrationStart;
    }

    public String getRegistrationEnd() {
        return registrationEnd;
    }

    public void setRegistrationEnd(String registrationEnd) {
        this.registrationEnd = registrationEnd;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isExternal() {
        return isExternal;
    }

    public void setExternal(boolean isExternal) {
        this.isExternal = isExternal;
    }

    public boolean isCanOrganisationsSignup() {
        return canOrganisationsSignup;
    }

    public void setCanOrganisationsSignup(boolean canOrganisationsSignup) {
        this.canOrganisationsSignup = canOrganisationsSignup;
    }

    public RadeltStatisticDto getStatistics() {
        return statistics;
    }

    public void setStatistics(RadeltStatisticDto statistics) {
        this.statistics = statistics;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getSubType() {
        return subType;
    }

    public String getTermsandconditionsText() {
        return termsandconditionsText;
    }

    public void setTermsandconditionsText(String termsandconditionsText) {
        this.termsandconditionsText = termsandconditionsText;
    }

    public String getDetailsText() {
        return detailsText;
    }

    public void setDetailsText(String detailsText) {
        this.detailsText = detailsText;
    }

}
