package it.bz.idm.bdp.dto.bikesharing;

import java.util.List;

public class TypesResult extends BikeResponseEntity{
	private List<TypeParameter> result;

	public List<TypeParameter> getResult() {
		return result;
	}

	public void setResult(List<TypeParameter> result) {
		this.result = result;
	}

	
}
