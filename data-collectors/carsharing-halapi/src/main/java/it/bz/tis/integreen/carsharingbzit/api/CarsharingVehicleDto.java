/*
carsharing-ds: car sharing datasource for the integreen cloud

Copyright (C) 2015 TIS Innovation Park - Bolzano/Bozen - Italy

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.bz.tis.integreen.carsharingbzit.api;


/**
 * 
 * @author Davide Montesin <d@vide.bz>
 */
public class CarsharingVehicleDto
{
	protected String id;
	protected String name;
	protected Double latitude;
	protected Double longitude;
	protected String crs;
	private String origin;
	String licensePlate;
	String model;
	String brand;
	String showType;
	String stationId;

	public void setVehicleUID(String vehicleUID)
	{
		this.setId(vehicleUID);
	}

	public String getLicensePlate()
	{
		return this.licensePlate;
	}

	public void setLicensePlate(String licensePlate)
	{
		this.licensePlate = licensePlate;
	}

	public String getModel()
	{
		return this.model;
	}

	public void setModel(String model)
	{
		this.model = model;
	}

	public String getBrand()
	{
		return this.brand;
	}

	public void setBrand(String brand)
	{
		this.brand = brand;
	}

	public String getShowType()
	{
		return this.showType;
	}

	public void setShowType(String showType)
	{
		this.showType = showType;
	}

	public void setStation(CarsharingStationDto carsharingStationDto)
	{
		this.stationId = carsharingStationDto.getId();
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

	public String getStationId() {
		return stationId;
	}

	public void setStationId(String stationId) {
		this.stationId = stationId;
	}
	
}
