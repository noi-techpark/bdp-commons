package it.bz.idm.bdp.dcbikesharingmoqo.dto;

import java.io.Serializable;
import java.util.Date;

public class AvailabilityDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean available ;
    private Date from         ;
    private Date until        ;
    private Long duration     ;
    private String strFrom    ; 
    private String strUntil   ; 

    public AvailabilityDto() {
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getUntil() {
        return until;
    }

    public void setUntil(Date until) {
        this.until = until;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getStrFrom() {
        return strFrom;
    }

    public void setStrFrom(String strFrom) {
        this.strFrom = strFrom;
    }

    public String getStrUntil() {
        return strUntil;
    }

    public void setStrUntil(String strUntil) {
        this.strUntil = strUntil;
    }

    @Override
    public String toString() {
        return "AvailabilityDto [available=" + available + ", from=" + from + ", until=" + until + ", duration=" + duration + ", strFrom=" + strFrom + ", strUntil=" + strUntil + "]";
    }

}
