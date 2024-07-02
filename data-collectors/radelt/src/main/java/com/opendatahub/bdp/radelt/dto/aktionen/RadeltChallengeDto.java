// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.bdp.radelt.dto.aktionen;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class RadeltChallengeDto {
    private int id;
    private String name;
    private String shortName;
    private String headerImage;
    private long start;
    private long end;
    private long registrationStart;
    private long entryStart;
    private long registrationEnd;
    private long entryEnd;
    private String type;
	@JsonProperty("isExternal")
    private boolean isExternal;
	@JsonProperty("canOrganisationsSignup")
    private boolean canOrganisationsSignup;
    private RadeltStatisticDto statistics;
	private String subType;

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

    public long getStart() {
        return start;
    }

    public void setStart(String start) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			// Parse the date string to a Date object
			Date date = sdf.parse(start);
			this.start = date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
    }

	public long getEntryStart() {
		return entryStart;
	}

	public void setEntryStart(String entryStart) {
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			// Parse the date string to a Date object
			Date date = sdf.parse(entryStart);
			this.entryStart = date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

    public long getEnd() {
        return end;
    }

    public void setEnd(String end) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			// Parse the date string to a Date object
			Date date = sdf.parse(end);
			this.end = date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
    }

	public long getEntryEnd() {
		return entryEnd;
	}

	public void setEntryEnd(String entryEnd) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			// Parse the date string to a Date object
			Date date = sdf.parse(entryEnd);
			this.registrationStart = date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

    public long getRegistrationStart() {
        return registrationStart;
    }

    public void setRegistrationStart(String registrationStart) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			// Parse the date string to a Date object
			Date date = sdf.parse(registrationStart);
			this.registrationStart = date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
    }

    public long getRegistrationEnd() {
        return registrationEnd;
    }

    public void setRegistrationEnd(String registrationEnd) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			// Parse the date string to a Date object
			Date date = sdf.parse(registrationEnd);
			this.registrationEnd = date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
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
}
