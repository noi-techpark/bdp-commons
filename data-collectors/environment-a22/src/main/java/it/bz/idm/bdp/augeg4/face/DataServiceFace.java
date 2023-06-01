// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.augeg4.face;



public interface DataServiceFace {

    void loadPreviouslySyncedStations() throws Exception;

    void syncStations() throws Exception;

    void syncDataTypes() throws Exception;

    void pushData() throws Exception;

}
