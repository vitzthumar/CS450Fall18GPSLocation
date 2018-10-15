package edu.stlawu.locationgps;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Purpse of this class is to take in date and time of an event and convert it into either seconds, minutes or hours.
 */
public class Time {
    public static long[] calculateTimeDifference (Date start, Date stop) {
        // Custom date format
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
        String dateStart = format.format(start);
        String dateStop = format.format(stop);

        Date d1 = null;
        Date d2 = null;
        try {
            d1 = format.parse(dateStart);
            d2 = format.parse(dateStop);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Get msec from each, and subtract.
        long diff = d2.getTime() - d1.getTime();
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000);
        return new long[]{diffSeconds, diffMinutes, diffHours};
    }

    public static double convertToHours (Date dateOb1, Date dateOb2) {
        return 0.0;
    }

    public static double convertToMinutes (Date dateOb1, Date dateOb2) {
        return 0.0;
    }
}
