/*
carsharing-ds: car sharing datasource for the integreen cloud

Copyright (C) 2015 TIS Innovation Park - Bolzano/Bozen - Italy

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package it.bz.tis.integreen.carsharingbzit.tis;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.ProvenanceDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.json.JSONPusher;

/**
 *
 * @author Davide Montesin <d@vide.bz>
 */
public class CarSharingPusher extends JSONPusher
{
	
	private String provenanceName;
	private String provenanceVersion;

	public CarSharingPusher() {
		Properties props = new Properties();
		try {
			URL resource = getClass().getClassLoader().getResource("app.properties");
			props.load(new FileInputStream(resource.getFile()));
			provenanceName = props.getProperty("provenance.name");
			provenanceVersion = props.getProperty("provenance.version");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	@Override
	public Object pushData(String datasourceName, DataMapDto<?> dto) {
		return super.pushData(datasourceName, dto);
	}

	@Override
	public String initIntegreenTypology() {
		return "CarsharingStation";
	}

	@Override
	public <T> DataMapDto<RecordDtoImpl> mapData(T data) {
		return null;
	}

	@Override
	public ProvenanceDto defineProvenance() {
		return new ProvenanceDto(null,provenanceName,provenanceVersion,"HAL-API");
	}

}
