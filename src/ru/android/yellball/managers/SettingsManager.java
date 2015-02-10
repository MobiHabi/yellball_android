package ru.android.yellball.managers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import ru.android.yellball.bo.Settings;
import ru.android.yellball.utils.ContextParamsUtil;

/**
 * Created by user on 27.01.2015.
 */
public class SettingsManager {
    static private final String SHOW_COUNTDOWN = "showCountdown";

    static public Settings readSettings(Activity activity) {
        Settings settings = new Settings();

        SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
        settings.setShowCountdown(preferences.getBoolean(SHOW_COUNTDOWN, ContextParamsUtil.isCountdownOnDefaultValue(activity)));

        return settings;
    }

    static public void writeSettings(Activity activity, Settings settings) {
        if (settings == null) {
            throw new IllegalArgumentException("Settings are null");
        }

        activity.getPreferences(Context.MODE_PRIVATE).edit()
                .putBoolean(SHOW_COUNTDOWN, settings.isShowCountdown())
                .commit();
    }
}
