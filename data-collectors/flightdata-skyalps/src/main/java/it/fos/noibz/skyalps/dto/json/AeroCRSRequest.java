package it.fos.noibz.skyalps.dto.json;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * @author Thierry BODHUIN, bodhuin@gmail.com
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AeroCRSRequest implements Serializable {

	private Serializable aerocrs;

	public AeroCRSRequest() {
	}

	public AeroCRSRequest(Serializable aerocrs) {
		this.aerocrs = aerocrs;
	}

	public Serializable getAerocrs() {
		return aerocrs;
	}

	public void setAerocrs(Serializable aerocrs) {
		this.aerocrs = aerocrs;
	}

}
