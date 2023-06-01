// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.dcbikesharingmoqo.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BikesharingMoqoPageDto implements Serializable {

    private static final long serialVersionUID = 8642860252556395832L;

    private List<BikeDto> bikeList;
    private PaginationDto pagination;
    private Map<String, LocationDto> locationMap;

    public BikesharingMoqoPageDto() {
        this.pagination = new PaginationDto();
        this.bikeList = new ArrayList<BikeDto>();
        this.locationMap = new HashMap<String, LocationDto>();
    }

    public List<BikeDto> getBikeList() {
        return bikeList;
    }

    public void setBikeList(List<BikeDto> bikeList) {
        this.bikeList = bikeList;
    }

    public PaginationDto getPagination() {
        return pagination;
    }

    public void setPagination(PaginationDto pagination) {
        this.pagination = pagination;
    }

    public Map<String, LocationDto> getLocationMap() {
        return locationMap;
    }

    public void setLocationMap(Map<String, LocationDto> locationMap) {
        this.locationMap = locationMap;
    }

    @Override
    public String toString() {
        return "BikesharingMoqoPageDto [pagination=" + pagination + ", bikeList=" + bikeList + ", locationMap=" + locationMap + "]";
    }

}
