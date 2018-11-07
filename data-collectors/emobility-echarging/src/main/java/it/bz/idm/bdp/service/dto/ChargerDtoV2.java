package it.bz.idm.bdp.service.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChargerDtoV2 implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = -8562182298961356540L;
	protected String id;
	protected String name;
	protected Double latitude;
	protected Double longitude;
	protected String crs;
	private String origin;
	private String address;
	private String provider;
	private String code;
	private String model;
	private String state;
	private boolean isOnline;
	private String paymentInfo;
	private String accessInfo;
	private String accessType;
	private String[] categories;
	private String flashInfo;
	private String locationServiceInfo;
	private Boolean isReservable;
	private ChargingPositionDto position;
	private List<ChargingPointsDtoV2> chargingPoints;
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
		this.id = code;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public boolean getIsOnline() {
		return isOnline;
	}
	public void setIsOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}
	public String getPaymentInfo() {
		return paymentInfo;
	}
	public void setPaymentInfo(String paymentInfo) {
		this.paymentInfo = paymentInfo;
	}
	public String getAccessInfo() {
		return accessInfo;
	}
	public void setAccessInfo(String accessInfo) {
		this.accessInfo = accessInfo;
	}
	public String getFlashInfo() {
		return flashInfo;
	}
	public void setFlashInfo(String flashInfo) {
		this.flashInfo = flashInfo;
	}
	public String getLocationServiceInfo() {
		return locationServiceInfo;
	}
	public void setLocationServiceInfo(String locationServiceInfo) {
		this.locationServiceInfo = locationServiceInfo;
	}
	public Boolean getIsReservable() {
		return isReservable;
	}
	public void setIsReservable(Boolean isReservable) {
		this.isReservable = isReservable;
	}
	public ChargingPositionDto getPosition() {
		return position;
	}
	public void setPosition(ChargingPositionDto position) {
		this.position = position;
		this.longitude = this.position.getLongitude();
		this.latitude = this.position.getLatitude();
		this.address = this.position.getAddress();
	}
	public List<ChargingPointsDtoV2> getChargingPoints() {
		return chargingPoints;
	}
	public void setChargingPoints(List<ChargingPointsDtoV2> chargingPoints) {
		this.chargingPoints = chargingPoints;
	}
	public String getAddress() {
		return address;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public String getCrs() {
		return crs;
	}
	public void setCrs(String crs) {
		this.crs = crs;
	}
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}
	public String[] getCategories() {
		return categories;
	}
	public void setCategories(String[] categories) {
		this.categories = categories;
	}
	public String getAccessType() {
		return accessType;
	}
	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}
}
