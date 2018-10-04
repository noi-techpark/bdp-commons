package it.bz.idm.bdp.forecast.domain;

public class ParkingPlace implements ObservationMetaInfo{

	private String name = "";
	private int maxSlots = -1;
	private String parkingId;
	private String phone = "";
	private String adress = "";
	private String description = "";
	private double latitude = -1;
	private double longitude = -1;


	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getMaxSlots() {
		return maxSlots;
	}

	public void setMaxSlots(int maxSlots) {
		this.maxSlots = maxSlots;
	}

	public String getParkingId() {
		return parkingId;
	}

	public void setParkingId(String parkingId) {
		this.parkingId = parkingId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAdress() {
		return adress;
	}

	public void setAdress(String adress) {
		this.adress = adress;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	
	

	

}
