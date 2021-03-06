package ohtu.beddit.io;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import ohtu.beddit.R;

/**
 * Methods for writing and reading data from preferences. Used keys are under values folder in the settingkeys.xml file.
 * These keys are used as the id for the given preference.
 * If there is no value for some preference saved, getter return empty string / false.
 */
public class PreferenceService {
    private static final int DEFAULT_SNOOZE_LENGTH = 5;

    private static boolean getSettingBoolean(Context context, int settingId) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(context.getString(settingId), false);
    }

    private static String getSettingString(Context context, int settingId) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(context.getString(settingId), "");
    }

    private static void setSetting(Context context, int settingId, String newValue) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(context.getString(settingId), newValue);
        editor.commit();
    }

    /**
     * @see <a href="http://en.wikipedia.org/wiki/OAuth#OAuth_2.0">OAuth2</a>
     * @param context
     * @return The access token (OAuth2 protocol) as a String
     */
    public static String getToken(Context context) {
        return getSettingString(context, R.string.pref_key_userToken);
    }

    public static String getUsername(Context context) {
        return getSettingString(context, R.string.pref_key_username);
    }

    private static String getFirstName(Context context) {
        return getSettingString(context, R.string.pref_key_first_name);
    }

    private static String getLastName(Context context) {
        return getSettingString(context, R.string.pref_key_last_name);
    }

    /**
     *
     * @param context
     * @return Alarm length in minutes.
     */
    public static int getAlarmLength(Context context) {
        return Integer.parseInt(getSettingString(context, R.string.pref_key_alarm_length));
    }

    public static String getFullName(Context context) {
        return getFirstName(context) + " " + getLastName(context);
    }

    public static void setToken(Context context, String token) {
        setSetting(context, R.string.pref_key_userToken, token);
    }

    public static void setUsername(Context context, String username) {
        setSetting(context, R.string.pref_key_username, username);
    }

    public static void setFirstName(Context context, String firstName) {
        setSetting(context, R.string.pref_key_first_name, firstName);
    }

    public static void setLastName(Context context, String lastName) {
        setSetting(context, R.string.pref_key_last_name, lastName);
    }

    public static int getSnoozeLength(Context context) {
        try {
            return Integer.parseInt(getSettingString(context, R.string.pref_key_snooze));
        } catch (Exception e) {
            return DEFAULT_SNOOZE_LENGTH;
        }
    }

    public static String getColourTheme(Context context) {
        return getSettingString(context, R.string.pref_key_colour_theme);
    }

    public static char getWakeUpSleepStage(Context context) {
        return getSettingString(context, R.string.pref_key_wake_up_sleep_stage).charAt(0);
    }

    public static boolean getShowSleepData(Context context) {
        return getSettingBoolean(context, R.string.pref_key_show_sleep_data_checkbox);
    }
}
