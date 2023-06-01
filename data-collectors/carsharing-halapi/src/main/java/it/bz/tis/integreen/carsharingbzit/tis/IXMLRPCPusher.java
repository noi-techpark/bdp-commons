// Copyright (C) 2015 TIS Innovation Park - Bolzano/Bozen - Italy
// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.tis.integreen.carsharingbzit.tis;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationList;

/**
 * 
 * @author Davide Montesin <d@vide.bz>
 */
public interface IXMLRPCPusher
{
   public Object syncStations(String datasourceName, StationList data);

   public Object pushData(String datasourceName, DataMapDto<RecordDtoImpl> dto);
}
