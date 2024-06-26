// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.bdp.radelt.dto.organisationen;

import java.util.List;

public class RadeltStatisticsDto {
	private List<RadeltChallengeStatisticDto> challengeStatistics;

	public List<RadeltChallengeStatisticDto> getChallengeStatistics() {
		return challengeStatistics;
	}

	public void setChallengeStatistics(List<RadeltChallengeStatisticDto> challengeStatistics) {
		this.challengeStatistics = challengeStatistics;
	}
}
