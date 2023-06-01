// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;
import it.bz.idm.bdp.service.ChargePusher;
import it.bz.idm.bdp.service.dto.ChargerDtoV2;
import it.bz.idm.bdp.service.dto.ChargingPointsDtoV2;
import it.bz.idm.bdp.service.dto.ChargingPositionDto;
import it.bz.idm.bdp.service.dto.OutletDtoV2;

@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml" })
public class PusherTestIT extends AbstractJUnit4SpringContextTests{

	@Autowired
	private ChargePusher pusher;

	private List<ChargerDtoV2> charger = null;

	@Before
	public void setup() {
		charger = new ArrayList<ChargerDtoV2>();
		ChargerDtoV2 o = new ChargerDtoV2();
		o.setCode("thisisacode");
		o.setCategories(new String[] {"hey","to"});
		o.setAccessType("Got to know");
		o.setAccessInfo("Kind of accessInfo");
		o.setIsOnline(true);
		ChargingPositionDto position = new ChargingPositionDto();
		position.setCity("Chicago");
		position.setAddress("Baverlz 23");
		position.setCountry("usa");
		o.setPosition(position);
		o.setIsReservable(false);
		o.setLatitude(45.2313);
		o.setLongitude(42.2313);
		o.setModel("TESLA");
		o.setPaymentInfo("INfo for payment");
		o.setProvider("Patrick");
		o.setOrigin("Unknown");
		o.setName("Hello world");
		List<ChargingPointsDtoV2> chargingPoints = new ArrayList<>();
		ChargingPointsDtoV2 cp = new ChargingPointsDtoV2();
		cp.setId("huibu");
		cp.setRechargeState("ACTIVE");
		cp.setState("ACTIVE");
		List<OutletDtoV2> outlets = new ArrayList<>();
		OutletDtoV2 out = new OutletDtoV2();
		out.setHasFixedCable(true);
		out.setId("yeah");
		out.setMaxCurrent(20.5);
		out.setMinCurrent(1.);
		out.setMaxPower(2000.);
		out.setOutletTypeCode("Outlettype");
		outlets.add(out);
		cp.setOutlets(outlets);
		chargingPoints.add(cp);
		o.setChargingPoints(chargingPoints);
		charger.add(o);

	}
	@Test
	public void testMappingData(){
		DataMapDto<RecordDtoImpl> parseData = pusher.mapData(charger);
		assertNotNull(parseData);
		for(Map.Entry<String,DataMapDto<RecordDtoImpl>> entry: parseData.getBranch().entrySet()) {
			DataMapDto<RecordDtoImpl> dataMapDto = entry.getValue().getBranch().get(DataTypeDto.NUMBER_AVAILABE);
			assertNotNull(dataMapDto);
			assertNotNull(dataMapDto.getData());
			assertFalse(dataMapDto.getData().isEmpty());
			RecordDtoImpl recordDtoImpl = dataMapDto.getData().get(0);
			assertNotNull(recordDtoImpl);
			assertTrue(recordDtoImpl instanceof SimpleRecordDto);
			SimpleRecordDto dto = (SimpleRecordDto) recordDtoImpl;
			assertNotNull(dto.getTimestamp());
			assertNotNull(dto.getValue());
		}
	}
	@Test
	public void testMappingStations() {
		StationList stationList = pusher.mapStations2bdp(charger);
		assertFalse(stationList.isEmpty());
		assertNotNull(stationList.get(0));
		assertTrue(stationList.get(0) instanceof StationDto);
		StationDto eStation=  stationList.get(0);

		assertEquals(new Double(42.2313) , eStation.getLongitude());
		assertEquals(new Double(45.2313) , eStation.getLatitude());
		assertEquals("thisisacode",eStation.getId());

	}
}
