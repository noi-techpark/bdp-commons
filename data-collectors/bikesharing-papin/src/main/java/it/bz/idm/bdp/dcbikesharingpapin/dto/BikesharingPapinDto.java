// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.dcbikesharingpapin.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BikesharingPapinDto implements Serializable {

    private static final long serialVersionUID = 8642860252556395832L;

    private List<BikesharingPapinStationDto> stationList;

    public BikesharingPapinDto() {
        this.stationList = new ArrayList<BikesharingPapinStationDto>();
    }

    public List<BikesharingPapinStationDto> getStationList() {
        return stationList;
    }

    public void setStationList(List<BikesharingPapinStationDto> stationList) {
        this.stationList = stationList;
    }

    @Override
    public String toString() {
        return "BikesharingPapinDto [stationList=" + stationList + "]";
    }

}
