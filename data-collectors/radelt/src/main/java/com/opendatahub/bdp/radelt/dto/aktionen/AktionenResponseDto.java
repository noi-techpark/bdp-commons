package it.bz.idm.bdp.radelt.dto.aktionen;

import java.util.List;

public class AktionenResponseDto {
	private boolean success;
	private Data data;

	// Getters and Setters
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public static class Data {
		private List<RadeltChallengeDto> challenges;

		// Getters and Setters
		public List<RadeltChallengeDto> getChallenges() {
			return challenges;
		}

		public void setChallenges(List<RadeltChallengeDto> challenges) {
			this.challenges = challenges;
		}
	}
}
