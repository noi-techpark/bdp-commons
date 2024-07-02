// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.bdp.radelt.dto.utils;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import com.opendatahub.bdp.radelt.OdhClient;
import it.bz.idm.bdp.dto.DataTypeDto;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import org.springframework.web.reactive.function.client.WebClientRequestException;


public class DataTypeUtils {

	@Value("${odh_client.period}")
	private static Integer period;

	@Value("${odh_client.period}")
	public void setPeriod(Integer period) {
		DataTypeUtils.period = period;
	}

	public static void setupDataType(OdhClient odhClient, Logger LOG) {

		List<DataTypeDto> odhDataTypeList = new ArrayList<>();

		odhDataTypeList.add(
			new DataTypeDto(
				"km_total",
				"km",
				"Total amount of kilometers registered",
				"total"
			)
		);

		odhDataTypeList.add(
			new DataTypeDto(
				"height_meters_total",
				"meters",
				"Total amount of height meters registered",
				"total"
			)
		);

		odhDataTypeList.add(
			new DataTypeDto(
				"km_average",
				"km",
				"Average number of kilometers registered",
				"average"
			)
		);

		odhDataTypeList.add(
			new DataTypeDto(
				"kcal",
				"kcal",
				"Total amount of kilocalories registered",
				"total"
			)
		);

		odhDataTypeList.add(
			new DataTypeDto(
				"co2",
				"kg",
				"Total amount of CO2 emissions savings",
				"total"
			)
		);

		odhDataTypeList.add(
			new DataTypeDto(
				"m2_trees",
				"m2",
				"Total equivalent tree area created with CO2 savings",
				"total"
			)
		);

		odhDataTypeList.add(
			new DataTypeDto(
				"money_saved",
				"EUR",
				"Total amount of money saved",
				"total"
			)
		);

		odhDataTypeList.add(
			new DataTypeDto(
				"number_of_people",
				"count",
				"Total number of registered users",
				"total"
			)
		);

		try {
			odhClient.syncDataTypes(odhDataTypeList);
			LOG.info("Sync data type completed successfully");
		} catch (WebClientRequestException e) {
			LOG.error("Sync data types failed: Request exception: {}", e.getMessage());
		}
	}

	public static void addMeasurement(DataMapDto<RecordDtoImpl> map, String dataType, long timestamp, double value) {
		DataMapDto<RecordDtoImpl> metricMap = map.upsertBranch(dataType);
		SimpleRecordDto measurement = new SimpleRecordDto(timestamp, value, period);
		List<RecordDtoImpl> values = metricMap.getData();
		values.add(measurement);
	}
}
