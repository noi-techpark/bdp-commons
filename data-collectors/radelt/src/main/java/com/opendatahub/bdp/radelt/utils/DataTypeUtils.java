// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.bdp.radelt.utils;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.json.NonBlockingJSONPusher;

public class DataTypeUtils {

	private static final Logger LOG = LoggerFactory.getLogger(DataTypeUtils.class);

	public static void setupDataType(NonBlockingJSONPusher odhClient) {

		List<DataTypeDto> odhDataTypeList = new ArrayList<>();

		odhDataTypeList.add(
				new DataTypeDto(
						"km_total",
						"km",
						"Total amount of kilometers registered",
						"total"));

		odhDataTypeList.add(
				new DataTypeDto(
						"height_meters_total",
						"meters",
						"Total amount of height meters registered",
						"total"));

		odhDataTypeList.add(
				new DataTypeDto(
						"km_average",
						"km",
						"Average number of kilometers registered",
						"average"));

		odhDataTypeList.add(
				new DataTypeDto(
						"kcal",
						"kcal",
						"Total amount of kilocalories registered",
						"total"));

		odhDataTypeList.add(
				new DataTypeDto(
						"co2",
						"kg",
						"Total amount of CO2 emissions savings",
						"total"));

		odhDataTypeList.add(
				new DataTypeDto(
						"m2_trees",
						"m2",
						"Total equivalent tree area created with CO2 savings",
						"total"));

		odhDataTypeList.add(
				new DataTypeDto(
						"money_saved",
						"EUR",
						"Total amount of money saved",
						"total"));

		odhDataTypeList.add(
				new DataTypeDto(
						"number_of_people",
						"count",
						"Total number of registered users",
						"total"));

		odhDataTypeList.add(
				new DataTypeDto(
						"workplace_count",
						"count",
						"Total number of people at workplace",
						"total"));

		odhDataTypeList.add(
				new DataTypeDto(
						"university_count",
						"count",
						"Total number of people at University",
						"total"));

		odhDataTypeList.add(
				new DataTypeDto(
						"school_count",
						"count",
						"Total number of people at school",
						"total"));

		odhDataTypeList.add(
				new DataTypeDto(
						"organisation_count",
						"count",
						"Total number of people at organisation",
						"total"));

		odhDataTypeList.add(
				new DataTypeDto(
						"municipality_count",
						"count",
						"Total number of municipalities",
						"total"));

		odhClient.syncDataTypes(odhDataTypeList);
		LOG.info("Sync data type completed successfully");
	}
}
