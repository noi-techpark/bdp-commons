package it.bz.idm.bdp.augeg4;


import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import it.bz.idm.bdp.augeg4.dto.fromauge.AugeG4ElaboratedDataDto;
import it.bz.idm.bdp.augeg4.face.DataRetrieverFace;
import it.bz.idm.bdp.augeg4.fun.retrieve.DataRetriever;

/**
 * Requires real Auge MQTT.
 */
public class DataRetrievalConnectIT {

	private DataRetrieverFace dr;


	@Test
	public void tests_fetch_data_retrieval_from_mqtt_after_one_minute() {
		dr =  new DataRetriever(new ConnectorConfig());
		try {
			Thread.sleep(30 * 1000 * 1);
			List<AugeG4ElaboratedDataDto> data = dr.fetchData();
			printReceived(data);
			dr.stop();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}


	private void printReceived(List<AugeG4ElaboratedDataDto> data) {
		for (AugeG4ElaboratedDataDto dato: data) {
			System.out.println("----------------------------------- AugeG4ElaboratedDataDto ");
			System.out.println(dato.toString());
		}
	}

}
