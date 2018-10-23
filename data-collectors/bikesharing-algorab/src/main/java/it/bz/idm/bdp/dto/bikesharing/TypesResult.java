package it.bz.idm.bdp.dto.bikesharing;

import java.util.List;

public class TypesResult extends BikeResponseEntity{
	private static final long serialVersionUID = -6081459170216023741L;
	private List<TypeParameter> result;

	public List<TypeParameter> getResult() {
		return result;
	}

	public void setResult(List<TypeParameter> result) {
		this.result = result;
	}


}
