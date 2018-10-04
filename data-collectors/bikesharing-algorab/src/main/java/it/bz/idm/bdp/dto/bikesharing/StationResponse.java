package it.bz.idm.bdp.dto.bikesharing;

import java.util.List;


public class StationResponse  extends BikeResponseEntity{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StationResponse() {
	}
	private List<String> result;

	public List<String> getResult() {
		return result;
	}

	public void setResult(List<String> result) {
		this.result = result;
	}
	
	

}
