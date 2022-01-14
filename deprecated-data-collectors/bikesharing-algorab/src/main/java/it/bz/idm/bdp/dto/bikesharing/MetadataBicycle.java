package it.bz.idm.bdp.dto.bikesharing;

import java.util.List;

public class MetadataBicycle extends BikeResponseEntity {
	private static final long serialVersionUID = 790807023651641383L;
	private List<StationParameter> result;
	private String value;

	public List<StationParameter> getResult() {
		return result;
	}

	public void setResult(List<StationParameter> result) {
		this.result = result;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}



}
