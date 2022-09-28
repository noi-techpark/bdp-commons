package it.bz.odh.trafficprovbz.dto;

public class LaneDto {

    private String id;
    private String direction;

    public LaneDto(String id, String direction) {
        this.id = id;
        this.direction = direction;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}