// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later
package it.bz.tis.integreen.carsharingbzit;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/* Part of Netex MaaS4Italy implementation. Add new models when they are added to the service
    https://github.com/noi-techpark/sta-nap-export/issues/1
 */
public class CarModels {
    private Map<String, Object> model(String childseat, int seats, int doors, String transmission,
            boolean cruisecontrol,
            boolean satnav, boolean usbpowersockets, boolean wintertyres, boolean chains, boolean trailserhitch,
            boolean roofrack, boolean cyclerack, boolean skirack) {
        Map<String, Object> ret = new HashMap<>();
        ret.put("childseat", childseat);
        ret.put("seats", seats);
        ret.put("doors", doors);
        ret.put("transmission", transmission);
        ret.put("cruisecontrol", cruisecontrol);
        ret.put("satnav", satnav);
        ret.put("usbpowersockets", usbpowersockets);
        ret.put("wintertyres", wintertyres);
        ret.put("chains", chains);
        ret.put("trailserhitch", trailserhitch);
        ret.put("roofrack", roofrack);
        ret.put("cyclerack", cyclerack);
        ret.put("skirack", skirack);
        return ret;
    }

    private Map<String, Map<String, Object>> models;

    public CarModels() {
        models = new HashMap<>();
        models.put("VW up!", model("none", 4, 4, "manual", false, true, true, true, false, false, false, false, false));
        models.put("VW Golf", model("none", 5, 4, "manual", true, true, true, true, false, false, false, false, false));
        models.put("VW Caddy Caddy 2.0", model("baby", 7, 4, "manual", true, true, true, true, false, true, true, false, false));
        models.put("VW ID.3", model("none", 4, 4, "automatic", true, true, true, true, false, false, false, false, false)); 
    }

    public Map<String, Object> getFeatures(String model) {
      var features = models.get(model);
      return features != null? features : Collections.emptyMap();
    }
}
