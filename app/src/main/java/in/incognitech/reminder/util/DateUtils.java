package in.incognitech.reminder.util;

import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by udit on 16/02/16.
 */
public class DateUtils {

    public static String toString(Date date) {
        return DateFormat.format(Constants.DATE_FORMAT, date).toString();
    }

    public static String toGMT(Date date) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(Constants.DATE_FORMAT);
            Date curDate = format.parse(toString(date));
            format.setTimeZone(TimeZone.getTimeZone("GMT"));
            return format.format(curDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
