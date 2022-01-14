package it.bz.idm.bdp.dto.bikesharing;

import java.util.ArrayList;
import java.util.List;

public class MetaDataResult extends BikeResponseEntity{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<StationParameter> result = new ArrayList<StationParameter>();
	
	private Object value;
	

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public List<StationParameter> getResult() {
		return result;
	}

	public void setResult(List<StationParameter> result) {
		this.result = result;
	}
}
