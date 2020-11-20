package it.bz.odh.util;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.bz.idm.bdp.dto.OddsRecordDto;

public class TimeConvertingUtility {
    public static Logger loggstuff = LogManager.getLogger(TimeConvertingUtility.class);
    public static long getLocalToUtcDelta() {
        Calendar local = Calendar.getInstance();
        local.clear();
        local.set(1970, Calendar.JANUARY, 1, 0, 0, 0);
        return local.getTimeInMillis();
    }

    public static long converLocalTimeToUtcTime(long timeSinceLocalEpoch) {
    	Calendar instance = Calendar.getInstance(Locale.ITALY);
    	instance.setTimeInMillis(timeSinceLocalEpoch);
    	long delta = instance.getTimeInMillis() - getLocalToUtcDelta();
        return delta;
    }

	/**
	 * converts record timestamp from italian locale to utc
	 * @param recs records send by a single BluetoothBox
	 */
	public static void convert(List<OddsRecordDto> recs) {
		for (OddsRecordDto dto : recs) {
			dto.setUtcInMs(converLocalTimeToUtcTime(dto.getGathered_on().getTime()));
		}
		
	}
}
