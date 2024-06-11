// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package Helpers;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
public class DateHelper {

    private static final Logger LOG = LoggerFactory.getLogger(DateHelper.class.getName());
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-ddTHH:mm:ssXXX");
    /**
     * This method, given the input parameters, produces a timeStamp used
     * for the measurements mapping.
     * @return A Long containing the so formed timeStamp.
     */
    public Long getTimeStamp(String input)
    {
        LOG.debug("Producing timestamp...");
        try {
            return dateFormat.parse(input).getTime();
        } catch (ParseException e)
        {
            LOG.error("Impossible to parse time and set timestamp.");
            return null;
        }
    }
}
