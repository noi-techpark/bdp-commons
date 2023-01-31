package com.opendatahub.bdp.commons.dc.bikeboxes.services;

import java.util.List;

import com.opendatahub.bdp.commons.dc.bikeboxes.dto.BikeService;
import com.opendatahub.bdp.commons.dc.bikeboxes.dto.BikeStation;

public interface IBikeBoxesService {
    public List<BikeService> getBikeServices();
    public List<BikeStation> getBikeStations(String cityId);
    public BikeStation getBikeStation(String stationId);
}
