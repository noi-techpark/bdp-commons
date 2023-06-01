// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.dcbikesharingmoqo.dto;

import java.io.Serializable;

public class LocationDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String city       ; 
    private String postcode   ; 
    private String street     ; 
    private String kind       ; 
    private String name       ; 
    private String parkingName; 
    private Double longitude  ; 
    private Double latitude   ; 

    public LocationDto() {
        
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParkingName() {
        return parkingName;
    }

    public void setParkingName(String parkingName) {
        this.parkingName = parkingName;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "LocationDto [city=" + city + ", postcode=" + postcode + ", street=" + street + ", kind=" + kind + ", name=" + name + ", parkingName=" + parkingName + ", longitude=" + longitude
                + ", latitude=" + latitude + "]";
    }

}
