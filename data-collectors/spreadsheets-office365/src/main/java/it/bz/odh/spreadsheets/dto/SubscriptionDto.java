package it.bz.odh.spreadsheets.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SubscriptionDto {


    private String changeType;
    private String clientState;
    private String notificationUrl;
    private String resource;
    private String expirationDateTime;


    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public String getClientState() {
        return clientState;
    }

    public void setClientState(String clientState) {
        this.clientState = clientState;
    }

    public String getNotificationUrl() {
        return notificationUrl;
    }

    public void setNotificationUrl(String notificationUrl) {
        this.notificationUrl = notificationUrl;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getExpirationDateTime() {
        return expirationDateTime;
    }

    public void setExpirationDateTime(String expirationDateTime) {
        this.expirationDateTime = expirationDateTime;
    }
}
