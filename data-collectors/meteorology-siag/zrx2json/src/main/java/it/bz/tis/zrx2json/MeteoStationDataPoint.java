package it.bz.tis.zrx2json;

public class MeteoStationDataPoint {
	private String date;
	private Double dataPoint;
	private Integer statusFlag;
	private String comment;
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public Double getDataPoint() {
		return dataPoint;
	}
	public void setDataPoint(Double dataPoint) {
		this.dataPoint = dataPoint;
	}
	public Integer getStatusFlag() {
		return statusFlag;
	}
	public void setStatusFlag(Integer statusFlag) {
		this.statusFlag = statusFlag;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
}
