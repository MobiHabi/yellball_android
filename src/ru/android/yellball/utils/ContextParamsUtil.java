package ru.android.yellball.utils;

import android.content.Context;
import ru.android.yellball.R;

/**
 * Created by user on 29.12.2014.
 */
public class ContextParamsUtil {
    static public boolean isCountdownOn(Context context) {
        return context.getResources().getBoolean(R.bool.show_countdown);
    }

    static public int getRecordButtonAlpha(Context context) {
        return context.getResources().getInteger(R.integer.record_image_alpha);
    }

    static public int getInfoButtonAlpha(Context context) {
        return context.getResources().getInteger(R.integer.info_image_alpha);
    }

    static public int getCountdownTextSize(Context context) {
        return context.getResources().getInteger(R.integer.countdown_text_size_in_sp);
    }

    static public int getCountdownTextColor(Context context) {
        return context.getResources().getInteger(R.integer.countdown_text_color);
    }

    static public int getCountdownStartValue(Context context) {
        return context.getResources().getInteger(R.integer.countdown_start_value);
    }

    static public float getRecordButtonSize(Context context) {
        return context.getResources().getDimension(R.dimen.record_image_size);
    }

    static public float getSendButtonSize(Context context) {
        return context.getResources().getDimension(R.dimen.send_image_size);
    }

    static public int getSoundHistogramColor(Context context) {
        return context.getResources().getInteger(R.integer.sound_histogram_color);
    }

    static public int getMaxRecordDurationInMs(Context context) {
        return context.getResources().getInteger(R.integer.max_record_duration_ms);
    }

    static public int getMenuButtonAlpha(Context context) {
        return context.getResources().getInteger(R.integer.menu_image_alpha);
    }
}
