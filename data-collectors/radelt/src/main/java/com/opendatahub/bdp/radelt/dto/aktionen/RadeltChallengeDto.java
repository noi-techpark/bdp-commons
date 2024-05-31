package it.bz.idm.bdp.radelt.dto.aktionen;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RadeltChallengeDto {
    private int id;
    private String name;
    private String shortName;
    private String headerImage;
    private Date start;
    private Date end;
    private Date registrationStart;
    private Date registrationEnd;
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

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Date getRegistrationStart() {
        return registrationStart;
    }

    public void setRegistrationStart(Date registrationStart) {
        this.registrationStart = registrationStart;
    }

    public Date getRegistrationEnd() {
        return registrationEnd;
    }

    public void setRegistrationEnd(Date registrationEnd) {
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
}
