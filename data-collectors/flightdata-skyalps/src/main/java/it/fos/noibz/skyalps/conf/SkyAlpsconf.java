// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.fos.noibz.skyalps.conf;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import it.fos.noibz.skyalps.SpringBootApp;
import it.fos.noibz.skyalps.dto.json.schedule.AeroCRSFlight;
import it.fos.noibz.skyalps.dto.json.schedule.AeroCRSGetScheduleSuccess;
import it.fos.noibz.skyalps.dto.json.schedule.AeroCRSGetScheduleSuccessResponse;
import it.fos.noibz.skyalps.rest.AeroCRSRest;
import it.fos.noibz.skyalps.service.SyncScheduler;

@Configuration
public class SkyAlpsconf {
	private static final String IATA = "IATA";
	private static final String BQ = "BQ";
	private static final Logger LOG = LoggerFactory.getLogger(SpringBootApp.class);

	@Autowired
	SyncScheduler sync;

	@Value("${commandlinerunner_days_before}")
	private int DAYS_BEFORE;
	@Value("${commandlinerunner_days_after}")
	private int DAYS_AFTER;

	@Value("${ssim_enabled}")
	private boolean ssimEnabled;

	//Boolean to retrieve ssim format
	@Bean
	public boolean ssim(){
		return ssimEnabled;
	}
}
