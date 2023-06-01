// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.airquality.dto;

import java.util.HashMap;
import java.util.Map;

public class AQBlockDto {
	int parameterType;
	Map<Character, Double> keyValue;

	public AQBlockDto(int type) {
		parameterType = type;
		keyValue = new HashMap<Character, Double>();
	}

	public int getParameterType() {
		return parameterType;
	}

	public void setParameterType(int parameterType) {
		this.parameterType = parameterType;
	}

	@Override
	public String toString() {
		return "AQBlockDto [parameterType=" + parameterType + ", keyValue=" + keyValue + "]";
	}

	public Map<Character, Double> getKeyValue() {
		return keyValue;
	}

	public void addKeyValue(Character key, Double value) {
		keyValue.put(key, value);
	}

	public int getSize() {
		return keyValue.size();
	}

}
