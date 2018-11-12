package it.bz.idm.bdp.dcparkingtn.dto;

import java.io.Serializable;

import it.bz.idm.bdp.dto.parking.ParkingStationDto;

public class ParkingTnDto implements Serializable {

    private static final long serialVersionUID = 8642860252556395832L;

    private ParkingAreaServiceDto parkingArea;
    private ParkingStationDto station;
    private String municipality;

    public ParkingTnDto() {
    }

    public ParkingTnDto(ParkingAreaServiceDto parkingArea, ParkingStationDto station, String municipality) {
        this.parkingArea = parkingArea;
        this.station = station;
        this.municipality = municipality;
    }

    public ParkingAreaServiceDto getParkingArea() {
        return parkingArea;
    }

    public void setParkingArea(ParkingAreaServiceDto parkingArea) {
        this.parkingArea = parkingArea;
    }

    public ParkingStationDto getStation() {
        return station;
    }

    public void setStation(ParkingStationDto station) {
        this.station = station;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    @Override
    public String toString() {
        String retval =
                this.getClass().getSimpleName() + ": municipality=" + municipality + ", station=" + station + ", parkingArea=" + parkingArea;
        return retval;
    }
}
