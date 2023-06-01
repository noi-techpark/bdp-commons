// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.fos.noibz.skyalps.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.ProvenanceDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.json.NonBlockingJSONPusher;

//This is the pushing data class. It has been structured with the respect of the meteorology-eurac data collector.
@Lazy
@Service
public class ODHClient extends NonBlockingJSONPusher {

	@Value("${odh_client.stationtype}")
	private String stationtype;

	@Value("${odh_client.provenance.origin}")
	private String provenanceOrigin;

	@Value("${odh_client.provenance.version}")
	private String provenanceVersion;

	@Value("${odh_client.provenance.name}")
	private String provenanceName;

	// define station type.
	@Override
	public String initIntegreenTypology() {
		return stationtype;
	}

	public ProvenanceDto defineProvenance() {
		return new ProvenanceDto(null, provenanceName, provenanceVersion, provenanceOrigin);
	}

	public ProvenanceDto getProvenance() {
		return provenance;
	}

	@Override
	public <T> DataMapDto<RecordDtoImpl> mapData(T data) {
		return null;
	}

	public String getStationtype() {
		return stationtype;
	}

	public void setStationtype(String stationtype) {
		this.stationtype = stationtype;
	}

	public String getProvenanceOrigin() {
		return provenanceOrigin;
	}

	public void setProvenanceOrigin(String provenanceOrigin) {
		this.provenanceOrigin = provenanceOrigin;
	}

	public String getProvenanceVersion() {
		return provenanceVersion;
	}

	public void setProvenanceVersion(String provenanceVersion) {
		this.provenanceVersion = provenanceVersion;
	}

	public String getProvenanceName() {
		return provenanceName;
	}

	public void setProvenanceName(String provenanceName) {
		this.provenanceName = provenanceName;
	}

}
