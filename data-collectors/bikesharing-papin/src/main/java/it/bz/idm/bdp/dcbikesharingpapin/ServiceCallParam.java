// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.dcbikesharingpapin;

public class ServiceCallParam {

    public static final String TYPE_FIXED_VALUE   = "fixed";
    public static final String TYPE_FUNCTION      = "funct";

    public static final String FUNCTION_NAME_STATION_ID = "STATION_ID";

    public String name;
    public String type;
    public String value;

    public ServiceCallParam(String name) {
        super();
        this.name = name;
    }

}
