// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.fos.noibz.skyalps.dto.json.schedule;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * @author Thierry BODHUIN, bodhuin@gmail.com
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetScheduleRequest implements Serializable {

	@JsonFormat(pattern = "yyyy/MM/dd")
	private Date fltsFROMperiod; // YYYY/MM/DD start period of the required data
	@JsonFormat(pattern = "yyyy/MM/dd")
	private Date fltsTOperiod; // YYYY/MM/DD end period of the required data
	private String codeformat; // IATA / ICAO / INTERNAL / AIRLINE – output of codes
	// IATA - the system will generate the output only for routes with IATA codes,
	// and the output codes will be IATA
	// ICAO - the system will generate the output only for routes with ICAO codes,
	// and the output codes will be ICAO
	// INTERNAL - The system will generate the output with internal assigned codes
	// for non IATA destinations, note that AeroCRS internal codes can conflict with
	// real IATA destinations, the rest of the destinations will be in IATA codes.
	// AIRLINE - The system will output the destinations in the airline defined
	// codes
	private String companycode; // Company short code (as supplied to you by the team) - Adding this element
								// will narrow the search of the data for the specific airline.
	private boolean soldonline; // when set to true will show only flights which are sold online.

	private boolean ssim;

	public GetScheduleRequest() {
	}

	public GetScheduleRequest(Date fltsFROMperiod, Date fltsTOperiod, String codeformat, String companycode,
			boolean soldonline, boolean ssim) {
		this.fltsFROMperiod = fltsFROMperiod;
		this.fltsTOperiod = fltsTOperiod;
		this.codeformat = codeformat;
		this.companycode = companycode;
		this.soldonline = soldonline;
		this.ssim = ssim;
	}

	public Date getFltsFROMperiod() {
		return fltsFROMperiod;
	}

	public void setFltsFROMperiod(Date fltsFROMperiod) {
		this.fltsFROMperiod = fltsFROMperiod;
	}

	public Date getFltsTOperiod() {
		return fltsTOperiod;
	}

	public void setFltsTOperiod(Date fltsTOperiod) {
		this.fltsTOperiod = fltsTOperiod;
	}

	public String getCodeformat() {
		return codeformat;
	}

	public void setCodeformat(String codeformat) {
		this.codeformat = codeformat;
	}

	public String getCompanycode() {
		return companycode;
	}

	public void setCompanycode(String companycode) {
		this.companycode = companycode;
	}

	public boolean isSoldonline() {
		return soldonline;
	}

	public void setSoldonline(boolean soldonline) {
		this.soldonline = soldonline;
	}

	public boolean isSsim() {
		return ssim;
	}

	public void setSsim(boolean ssim) {
		this.ssim = ssim;
	}

}
