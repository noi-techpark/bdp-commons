package info.datatellers.appatn.helpers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Nicolò Molinari, Datatellers.
 *
 * This helper class handles everything related to dates and Date objects.
 */
public class DateHelper
{
    private static final Logger LOG = LogManager.getLogger(DateHelper.class.getName());
    public DateHelper()
    {}

    /**
     * This method is used inside test classes.
     * @return A String, containing the date of the day before yesterday
     * in the form (yyyy-MM-dd). The day before yesterday is chosen because
     * everyday's yesterday is the most recent available measurement, but
     * since everyday's update time of measurement is not known, this has to
     * be considered the most recent data, at least regarding testing.
     */
    public String getTestDate()
    {
        Date dayBeforeYesterday = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.clear(Calendar.HOUR);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.setTime(dayBeforeYesterday);
        calendar.add(Calendar.DATE, -2);
        dayBeforeYesterday = calendar.getTime();

        DateHelper dateHelper = new DateHelper();
        return dateHelper.formatDate(dayBeforeYesterday.toString());
    }

    /**
     * This method, given the input parameters, produces a timeStamp used
     * for the measurements mapping.
     * @param date A Date referring to the measurement.
     * @param hour A String referring to the measurement hour.
     * @return A Long containing the so formed timeStamp.
     */
    public Long getTimeStamp(String date, String hour)
    {
        LOG.debug("Producing timestamp...");
        String acquisitionTime = date + "T" + hour + ":00:00+01:00";
        try {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").parse(acquisitionTime).getTime();
        } catch (ParseException e)
        {
            LOG.error("Impossible to parse time and set timestamp.");
            return null;
        }
    }

    /**
     * This method, given a String, splits it in tokens and returns
     * a String the way described below.
     * @param inputDate A String containing a date of the form
     *                  "Thu Jan 01 00:59:59 CET 1970".
     * @return A String of the form "1970-01-01"
     */
    public String formatDate(String inputDate)
    {
        LOG.debug("Formatting date...");
        String year;
        String month;
        String day;

        String[] tokens = inputDate.split(" ");

        year = tokens[5];
        day = tokens[2];

        switch (tokens[1])
        {
            case "Jan": month = "01";
                break;
            case "Feb": month = "02";
                break;
            case "Mar": month = "03";
                break;
            case "Apr": month = "04";
                break;
            case "May": month = "05";
                break;
            case "Jun": month = "06";
                break;
            case "Jul": month = "07";
                break;
            case "Aug": month = "08";
                break;
            case "Sep": month = "09";
                break;
            case "Oct": month = "10";
                break;
            case "Nov": month = "11";
                break;
            case "Dec": month = "12";
                break;
            default: month = "Invalid month.";
                break;
        }
        return year + "-" + month + "-" + day;
    }

    /**
     * This method, given a Date, returns the date of the day after.
     * It is used to specify the json portion which contains the
     * measurements collected in the specified date, that is to move
     * inside the retrieved json.
     * @param currentDay A Date corresponding to the date which
     *                   measurements has already been retrieved
     *                   when this method is called.
     * @return A Date corresponding to the day after the input parameter.
     */
    public Date getNextDay(Date currentDay)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDay);
        calendar.add(Calendar.DATE, 1);
        return calendar.getTime();
    }

    /**
     * Since the maximum date span possible inside an endpoint request
     * is 90 days, should a larger one be wanted this method handles its
     * logic in respect to the just said constraint. If an interval
     * larger than 90 days is inputted, it is reduced to the maximum
     * possible. The original span is kept inside fillSensorsBranches
     * (DataMapDto rootMap, String date, String[] polluters, int[] stationIds, int looper)
     * method, where this method is called every time its  slitting is
     * necessary.
     * @param from A Date, representing the first span day.
     * @param to A Date, representing the last span day.
     * @return A Date, equal to to if the maximum span is not
     * exceeded, 90 days from from if not.
     */
    public Date getSecurityInterval(Date from, Date to)
    {
        LOG.debug("Calculating security interval as allowed by the web-service...");
        Calendar c = Calendar.getInstance();
        c.setTime(from);
        c.add(Calendar.DATE, 90);

        if (c.getTime().compareTo(to) > 0)
        {
            return to;
        }else
            {
            return c.getTime();
        }
    }
}
