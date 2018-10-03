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

import it.bz.idm.bdp.dto.carsharing.BookMode;
import it.bz.idm.bdp.dto.carsharing.Company;
import it.bz.idm.bdp.dto.carsharing.StationAccess;

/**
 * 
 * @author Davide Montesin <d@vide.bz>
 */
public class CarsharingStationDto
{
	protected String id;
	protected String name;
	protected Double latitude;
	protected Double longitude;
	protected String crs;
	private String origin;
	boolean  hasFixedParking;
	BookMode bookMode;
	StationAccess access;
	Company company;

	public void setUid(String uid)
	{
		this.setId(uid);
	}

	public void setHasFixedParking(boolean hasFixedParking)
	{
		this.hasFixedParking = hasFixedParking;
	}

	public boolean isHasFixedParking()
	{
		return this.hasFixedParking;
	}

	public void setGeoPos(GeoPos geoPos)
	{
		this.setLatitude(Double.parseDouble(geoPos.getLat()));
		this.setLongitude(Double.parseDouble(geoPos.getLon()));
	}

	public void setBookMode(BookMode bookMode)
	{
		this.bookMode = bookMode;
	}

	public BookMode getBookMode()
	{
		return this.bookMode;
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

	public StationAccess getAccess() {
		return access;
	}

	public void setAccess(StationAccess access) {
		this.access = access;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}
	

}
