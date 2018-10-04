package it.bz.idm.bdp.dto.bikesharing;

import java.io.Serializable;

public class BikeResponseEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6715847922797266475L;
	/**
	 * 
	 */
	private String status;
	private BikeInfo info;
	
	public BikeResponseEntity() {
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public BikeInfo getInfo() {
		return info;
	}
	public void setInfo(BikeInfo info) {
		this.info = info;
	}
	
}
