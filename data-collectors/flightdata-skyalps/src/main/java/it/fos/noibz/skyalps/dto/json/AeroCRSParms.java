package it.fos.noibz.skyalps.dto.json;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * @author Thierry BODHUIN, bodhuin@gmail.com
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AeroCRSParms implements Serializable {

	private Serializable parms;

	public AeroCRSParms() {
	}

	public AeroCRSParms(Serializable parms) {
		this.parms = parms;
	}

	public Serializable getParms() {
		return parms;
	}

	public void setParms(Serializable parms) {
		this.parms = parms;
	}

}
