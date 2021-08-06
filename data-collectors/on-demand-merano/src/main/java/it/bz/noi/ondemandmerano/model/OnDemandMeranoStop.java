package it.bz.noi.ondemandmerano.model;

import java.util.ArrayList;
import java.util.HashMap;

public class OnDemandMeranoStop {

    private Long id;
    private String title;
    private String reference;
    private String type;
    private OnDemandServicePositionPoint position;
    private HashMap<String, Object> address;
    private ArrayList<HashMap<String, Object>> groups;
    private HashMap<String, Object> region;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public OnDemandServicePositionPoint getPosition() {
        return position;
    }

    public void setPosition(OnDemandServicePositionPoint position) {
        this.position = position;
    }

    public HashMap<String, Object> getAddress() {
        return address;
    }

    public void setAddress(HashMap<String, Object> address) {
        this.address = address;
    }

    public ArrayList<HashMap<String, Object>> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<HashMap<String, Object>> groups) {
        this.groups = groups;
    }

    public HashMap<String, Object> getRegion() {
        return region;
    }

    public void setRegion(HashMap<String, Object> region) {
        this.region = region;
    }

    @Override
    public String toString() {
        return "OnDemandMeranoStop{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", reference='" + reference + '\'' +
                ", type='" + type + '\'' +
                ", position=" + position +
                ", address=" + address +
                ", groups=" + groups +
                ", region=" + region +
                '}';
    }
}
