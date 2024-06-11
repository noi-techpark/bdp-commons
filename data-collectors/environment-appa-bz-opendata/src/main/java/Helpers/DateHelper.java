// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package Helpers;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;


public class DateHelper {

    private static final Logger LOG = LoggerFactory.getLogger(DateHelper.class.getName());
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
    /**
     * This method, given the input parameters, produces a timeStamp used
     * for the measurements mapping.
     * @return A Long containing the so formed timeStamp.
     */
    public Long getTimeStamp(String input)
    {
        LOG.debug("Producing timestamp...");
        try {
			input = input.replace("\"", "");
			LOG.debug("date: {}", input);
			LOG.debug("instant: {}", dateFormat.parse(input).getTime());
			return dateFormat.parse(input).getTime();
			//return instant.toEpochMilli();
        } catch (ParseException e)
        {
            LOG.error("Impossible to parse time and set timestamp.");
            return null;
        }
    }
}
