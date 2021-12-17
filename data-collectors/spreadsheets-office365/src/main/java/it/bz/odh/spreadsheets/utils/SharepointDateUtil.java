package it.bz.odh.spreadsheets.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.stereotype.Component;

@Component
public class SharepointDateUtil {

    private SimpleDateFormat microsoftDateConverter;

    public SharepointDateUtil(){
        microsoftDateConverter  = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        microsoftDateConverter.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public Date parseDate(String dateString) throws ParseException{
        return microsoftDateConverter.parse(dateString);
    }
}
