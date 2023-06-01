// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.noi.trafficeventroadworkbz.model;

import java.time.LocalDate;
import java.time.ZoneId;

public class TrafficEventRoadworkBZModel {

    private String json_featuretype;
    private String publisherDateTime;
    private LocalDate beginDate;
    private LocalDate endDate;
    private String descriptionDe;
    private String descriptionIt;
    private String tycodeValue;
    private String tycodeDe;
    private String tycodeIt;
    private String subTycodeValue;
    private String subTycodeDe;
    private String subTycodeIt;
    private String placeDe;
    private String placeIt;
    private Integer actualMail;
    private Integer messageId;
    private Integer messageStatus;
    private Integer messageZoneId;
    private String messageZoneDescDe;
    private String messageZoneDescIt;
    private Integer messageGradId;
    private String messageGradDescDe;
    private String messageGradDescIt;
    private Integer messageStreetId;
    private String messageStreetWapDescDe;
    private String messageStreetWapDescIt;
    private String messageStreetInternetDescDe;
    private String messageStreetInternetDescIt;
    private String messageStreetNr;
    private Integer messageStreetHierarchie;
    private Integer messageTypeId;
    private String messageTypeDescDe;
    private String messageTypeDescIt;
    private Double x;
    private Double y;

    public TrafficEventRoadworkBZModel() {
    }

    public String getJson_featuretype() {
        return json_featuretype;
    }

    public void setJson_featuretype(String json_featuretype) {
        this.json_featuretype = json_featuretype;
    }

    public String getPublisherDateTime() {
        return publisherDateTime;
    }

    public void setPublisherDateTime(String publisherDateTime) {
        this.publisherDateTime = publisherDateTime;
    }

    public LocalDate getBeginDate() {
        return beginDate;
    }

	public long getBeginDateAsEpochMillis() {
		return beginDate
			.atStartOfDay(ZoneId.systemDefault())
			.toInstant()
			.toEpochMilli();
	}

    public void setBeginDate(LocalDate beginDate) {
        this.beginDate = beginDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

	public long getEndDateAsEpochMillis() {
		return endDate
			.atStartOfDay(ZoneId.systemDefault())
			.toInstant()
			.toEpochMilli();
	}

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getDescriptionDe() {
        return descriptionDe;
    }

    public void setDescriptionDe(String descriptionDe) {
        this.descriptionDe = descriptionDe;
    }

    public String getDescriptionIt() {
        return descriptionIt;
    }

    public void setDescriptionIt(String descriptionIt) {
        this.descriptionIt = descriptionIt;
    }

    public String getTycodeValue() {
        return tycodeValue;
    }

    public void setTycodeValue(String tycodeValue) {
        this.tycodeValue = tycodeValue;
    }

    public String getSubTycodeValue() {
        return subTycodeValue;
    }

    public void setSubTycodeValue(String subTycodeValue) {
        this.subTycodeValue = subTycodeValue;
    }

    public String getTycodeDe() {
        return tycodeDe;
    }

    public void setTycodeDe(String tycodeDe) {
        this.tycodeDe = tycodeDe;
    }

    public String getTycodeIt() {
        return tycodeIt;
    }

    public void setTycodeIt(String tycodeIt) {
        this.tycodeIt = tycodeIt;
    }

    public String getSubTycodeDe() {
        return subTycodeDe;
    }

    public void setSubTycodeDe(String subTycodeDe) {
        this.subTycodeDe = subTycodeDe;
    }

    public String getSubTycodeIt() {
        return subTycodeIt;
    }

    public void setSubTycodeIt(String subTycodeIt) {
        this.subTycodeIt = subTycodeIt;
    }

    public String getPlaceDe() {
        return placeDe;
    }

    public void setPlaceDe(String placeDe) {
        this.placeDe = placeDe;
    }

    public String getPlaceIt() {
        return placeIt;
    }

    public void setPlaceIt(String placeIt) {
        this.placeIt = placeIt;
    }

    public Integer getActualMail() {
        return actualMail;
    }

    public void setActualMail(Integer actualMail) {
        this.actualMail = actualMail;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public Integer getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(Integer messageStatus) {
        this.messageStatus = messageStatus;
    }

    public Integer getMessageZoneId() {
        return messageZoneId;
    }

    public void setMessageZoneId(Integer messageZoneId) {
        this.messageZoneId = messageZoneId;
    }

    public String getMessageZoneDescDe() {
        return messageZoneDescDe;
    }

    public void setMessageZoneDescDe(String messageZoneDescDe) {
        this.messageZoneDescDe = messageZoneDescDe;
    }

    public String getMessageZoneDescIt() {
        return messageZoneDescIt;
    }

    public void setMessageZoneDescIt(String messageZoneDescIt) {
        this.messageZoneDescIt = messageZoneDescIt;
    }

    public Integer getMessageGradId() {
        return messageGradId;
    }

    public void setMessageGradId(Integer messageGradId) {
        this.messageGradId = messageGradId;
    }

    public String getMessageGradDescDe() {
        return messageGradDescDe;
    }

    public void setMessageGradDescDe(String messageGradDescDe) {
        this.messageGradDescDe = messageGradDescDe;
    }

    public String getMessageGradDescIt() {
        return messageGradDescIt;
    }

    public void setMessageGradDescIt(String messageGradDescIt) {
        this.messageGradDescIt = messageGradDescIt;
    }

    public Integer getMessageStreetId() {
        return messageStreetId;
    }

    public void setMessageStreetId(Integer messageStreetId) {
        this.messageStreetId = messageStreetId;
    }

    public String getMessageStreetWapDescDe() {
        return messageStreetWapDescDe;
    }

    public void setMessageStreetWapDescDe(String messageStreetWapDescDe) {
        this.messageStreetWapDescDe = messageStreetWapDescDe;
    }

    public String getMessageStreetWapDescIt() {
        return messageStreetWapDescIt;
    }

    public void setMessageStreetWapDescIt(String messageStreetWapDescIt) {
        this.messageStreetWapDescIt = messageStreetWapDescIt;
    }

    public String getMessageStreetInternetDescDe() {
        return messageStreetInternetDescDe;
    }

    public void setMessageStreetInternetDescDe(String messageStreetInternetDescDe) {
        this.messageStreetInternetDescDe = messageStreetInternetDescDe;
    }

    public String getMessageStreetInternetDescIt() {
        return messageStreetInternetDescIt;
    }

    public void setMessageStreetInternetDescIt(String messageStreetInternetDescIt) {
        this.messageStreetInternetDescIt = messageStreetInternetDescIt;
    }

    public String getMessageStreetNr() {
        return messageStreetNr;
    }

    public void setMessageStreetNr(String messageStreetNr) {
        this.messageStreetNr = messageStreetNr;
    }

    public Integer getMessageStreetHierarchie() {
        return messageStreetHierarchie;
    }

    public void setMessageStreetHierarchie(Integer messageStreetHierarchie) {
        this.messageStreetHierarchie = messageStreetHierarchie;
    }

    public Integer getMessageTypeId() {
        return messageTypeId;
    }

    public void setMessageTypeId(Integer messageTypeId) {
        this.messageTypeId = messageTypeId;
    }

    public String getMessageTypeDescDe() {
        return messageTypeDescDe;
    }

    public void setMessageTypeDescDe(String messageTypeDescDe) {
        this.messageTypeDescDe = messageTypeDescDe;
    }

    public String getMessageTypeDescIt() {
        return messageTypeDescIt;
    }

    public void setMessageTypeDescIt(String messageTypeDescIt) {
        this.messageTypeDescIt = messageTypeDescIt;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "TrafficEventRoadworkBZModel{" +
                "json_featuretype='" + json_featuretype + '\'' +
                ", publisherDateTime='" + publisherDateTime + '\'' +
                ", beginDate=" + beginDate +
                ", endDate=" + endDate +
                ", descriptionDe='" + descriptionDe + '\'' +
                ", descriptionIt='" + descriptionIt + '\'' +
                ", tycodeValue='" + tycodeValue + '\'' +
                ", tycodeDe='" + tycodeDe + '\'' +
                ", tycodeIt='" + tycodeIt + '\'' +
                ", subTycodeValue='" + subTycodeValue + '\'' +
                ", subTycodeDe='" + subTycodeDe + '\'' +
                ", subTycodeIt='" + subTycodeIt + '\'' +
                ", placeDe='" + placeDe + '\'' +
                ", placeIt='" + placeIt + '\'' +
                ", actualMail=" + actualMail +
                ", messageId=" + messageId +
                ", messageStatus=" + messageStatus +
                ", messageZoneId=" + messageZoneId +
                ", messageZoneDescDe='" + messageZoneDescDe + '\'' +
                ", messageZoneDescIt='" + messageZoneDescIt + '\'' +
                ", messageGradId=" + messageGradId +
                ", messageGradDescDe='" + messageGradDescDe + '\'' +
                ", messageGradDescIt='" + messageGradDescIt + '\'' +
                ", messageStreetId=" + messageStreetId +
                ", messageStreetWapDescDe='" + messageStreetWapDescDe + '\'' +
                ", messageStreetWapDescIt='" + messageStreetWapDescIt + '\'' +
                ", messageStreetInternetDescDe='" + messageStreetInternetDescDe + '\'' +
                ", messageStreetInternetDescIt='" + messageStreetInternetDescIt + '\'' +
                ", messageStreetNr='" + messageStreetNr + '\'' +
                ", messageStreetHierarchie=" + messageStreetHierarchie +
                ", messageTypeId=" + messageTypeId +
                ", messageTypeDescDe='" + messageTypeDescDe + '\'' +
                ", messageTypeDescIt='" + messageTypeDescIt + '\'' +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
