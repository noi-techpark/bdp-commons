package it.bz.idm.bdp.dto.bikesharing;

import java.util.List;

public class DataResult extends BikeResponseEntity{
	private List<StationParameter> result;

	public List<StationParameter> getResult() {
		return result;
	}

	public void setResult(List<StationParameter> result) {
		this.result = result;
	}

}
