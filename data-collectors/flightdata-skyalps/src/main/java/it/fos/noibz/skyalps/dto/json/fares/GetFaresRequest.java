// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.fos.noibz.skyalps.dto.json.fares;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetFaresRequest implements Serializable {

    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date start;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date end;

    // From - to destination code like BZO - DUS
    private String from;
    private String to;

    private String currency;

    public GetFaresRequest() {
        // empty default constructor to be JSON serializable
    }

    public GetFaresRequest(Date start, Date end, String from, String to, String currency) {
        this.start = start;
        this.end = end;
        this.from = from;
        this.to = to;
        this.currency = currency;
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

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
