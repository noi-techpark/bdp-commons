package it.bz.noi.ondemandmerano.model.iternitystep;

import java.util.HashMap;

public abstract class OnDemandMeranoIternityStep {
    protected String type;

    public String getType() {
        return type;
    }

    public abstract HashMap<String, Object> toJson();
}
