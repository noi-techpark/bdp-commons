package it.bz.idm.bdp.dto.bikesharing;

import java.util.List;

public class DataResult extends BikeResponseEntity{
	private static final long serialVersionUID = 4713532244096695257L;
	private List<StationParameter> result;

	public List<StationParameter> getResult() {
		return result;
	}

	public void setResult(List<StationParameter> result) {
		this.result = result;
	}

}
