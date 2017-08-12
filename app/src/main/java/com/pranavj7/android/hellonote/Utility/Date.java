package com.pranavj7.android.hellonote.Utility;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class Date {
    private static void clearTime(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getActualMinimum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getActualMinimum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getActualMinimum(Calendar.MILLISECOND));
    }
    public static java.util.Date clearTime(java.util.Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        clearTime(cal);

        return cal.getTime();
    }
}
