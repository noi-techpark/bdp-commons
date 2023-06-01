// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.augeg4.face;

import it.bz.idm.bdp.augeg4.dto.AugeG4ProcessedData;
import it.bz.idm.bdp.augeg4.dto.tohub.AugeG4ProcessedDataToHubDto;

import java.util.List;

/**
 * Converts data from linearized to Data Hub format.
 */
public interface DataConverterHubFace {

    List<AugeG4ProcessedDataToHubDto> convert(List<AugeG4ProcessedData> data);
}
