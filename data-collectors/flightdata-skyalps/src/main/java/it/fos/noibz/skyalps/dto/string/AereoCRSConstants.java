package it.fos.noibz.skyalps.dto.string;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Use this class to save static variables for memory optimizations
 */
public class AereoCRSConstants {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");
    protected static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    protected static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
    protected static final SimpleDateFormat DATE_FORMAT_EN = new SimpleDateFormat("ddMMMyy", Locale.ENGLISH);
    protected static final DateTimeFormatter MONTH_FORMAT_EN = DateTimeFormatter.ofPattern("MMM")
            .withLocale(Locale.ENGLISH);

    // Reference example
    // 3 BN 19510101J29JAN2329JAN23 7 BER09400940+0100 BZO11251125+0100
    protected static final int SSIMSTRINGLENGHT = 200;
    protected static final int MONTHFROMFIRST = 16;
    protected static final int MONTHFROM = 17;
    protected static final int MONTHFROMFINAL = 19;
    protected static final int MONTHTOFIRST = 23;
    protected static final int MONTHTO = 24;
    protected static final int MONTHTOFINAL = 26;
    protected static final int FLTNUMBERSTART = 2;
    protected static final int FLTNUMBEREND = 9;
    protected static final String AIRLINENAME = "Sky Alps";
    protected static final int FROMDESTINATIONSTART = 36;
    protected static final int FROMDESTINATIONEND = 39;
    protected static final int TODESTINATIONSTART = 54;
    protected static final int TODESTINATIONEND = 57;
    protected static final int AIRLINEIDSTART = 10;
    protected static final int AIRLINEIDEND = 13;
    protected static final int ACCODESTART = 72;
    protected static final int ACCODEND = 75;
    protected static final int YEAR = 2000;
    protected static final int YEAFROMSTART = 19;
    protected static final int YEARFROMEND = 21;
    protected static final int YEARTOSTART = 26;
    protected static final int YEARTOEND = 28;
    protected static final int DAYFROMSTART = 14;
    protected static final int DAYFROMEND = 16;
    protected static final int DAYTOSTART = 21;
    protected static final int DAYTOEND = 23;
    protected static final int STDHOURSTART = 43;
    protected static final int STDHOUREND = 45;
    protected static final int STDMINUTESTART = 45;
    protected static final int STDMINUTEEND = 47;
    protected static final int STAHOURSTART = 61;
    protected static final int STAHOUREND = 63;
    protected static final int STAMINUTESTART = 63;
    protected static final int STAMINUTEND = 65;
    protected static final int MONDAYCHAR = 28;
    protected static final int SUNCHAR = 34;
    protected static final int MONDAY = 1;
    protected static final int TUESDAY = 2;
    protected static final int WEDNESDAY = 3;
    protected static final int THURSDAY = 4;
    protected static final int FRIDAY = 5;
    protected static final int SATURDAY = 6;
    protected static final int SUNDAY = 7;
}
