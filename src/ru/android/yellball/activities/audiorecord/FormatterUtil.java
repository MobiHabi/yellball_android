package ru.android.yellball.activities.audiorecord;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by user on 16.01.2015.
 */
public class FormatterUtil {
    static private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    static public String formatDuration(long milliseconds) {
        long minutes = milliseconds / 60000;
        int seconds = (int) ((milliseconds - 60000 * minutes) / 1000);
        int ms = (int) (milliseconds - 60000 * minutes - 1000 * seconds) / 100;

        String formattedValue = String.format("%01d:%02d.%01d", minutes, seconds, ms);
        return formattedValue;
    }

    static public String formatDate(Date date) {
        if (date == null) {
            return null;
        }

        String formattedValue = DATE_FORMAT.format(date);
        return formattedValue;
    }
}
