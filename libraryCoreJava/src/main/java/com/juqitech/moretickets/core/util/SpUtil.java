package com.juqitech.moretickets.core.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * @author zhanfeng
 * @date 2019-06-05
 * @desc
 */
public class SpUtil {

    /**
     * 用户个人信息相关sp
     */
    private static final String USER_INFO = "user_info";
    /**
     * 手机设置信息 相关sp
     */
    private static final String SETTING_INFO = "setting_info";

    public static SharedPreferences getSpUser(Context context) {
        if (null == context) {
            return null;
        }
        return context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
    }

    public static SharedPreferences getSpSetting(Context context) {
        if (null == context) {
            return null;
        }
        return context.getSharedPreferences(SETTING_INFO, Context.MODE_PRIVATE);
    }

    public static void setUserBoolean(Context context, String key, boolean value) {
        SharedPreferences spUser = getSpUser(context);
        SharedPreferences.Editor editor = spUser.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getUserBoolean(Context context, String key, boolean value) {
        SharedPreferences spUser = getSpUser(context);
        return spUser.getBoolean(key, value);
    }

    public static void setUserString(Context context, String key, String value) {
        SharedPreferences spUser = getSpUser(context);
        SharedPreferences.Editor editor = spUser.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getUserString(Context context, String key, String value) {
        SharedPreferences spUser = getSpUser(context);
        return spUser.getString(key, value);
    }

    public static void setUserInt(Context context, String key, int value) {
        SharedPreferences spUser = getSpUser(context);
        SharedPreferences.Editor editor = spUser.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getUserInt(Context context, String key, int value) {
        SharedPreferences spUser = getSpUser(context);
        return spUser.getInt(key, value);
    }

    public static void setUserLong(Context context, String key, long value) {
        SharedPreferences spUser = getSpUser(context);
        SharedPreferences.Editor editor = spUser.edit();
        editor.putLong(key, value).apply();
    }

    public static long getUserLong(Context context, String key, long value) {
        SharedPreferences spUser = getSpUser(context);
        return spUser.getLong(key, value);
    }

    /**
     * ----------------------------------------------------------------------------
     */

    public static void setSettingString(Context context, String key, String value) {
        SharedPreferences spUser = getSpSetting(context);
        SharedPreferences.Editor editor = spUser.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getSettingString(Context context, String key, String value) {
        SharedPreferences spUser = getSpSetting(context);
        return spUser.getString(key, value);
    }

    public static void setSettingLong(Context context, String key, long value) {
        SharedPreferences spUser = getSpSetting(context);
        SharedPreferences.Editor editor = spUser.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static long getSettingLong(Context context, String key, long value) {
        SharedPreferences spUser = getSpSetting(context);
        return spUser.getLong(key, value);
    }

    public static void setSettingBoolean(Context context, String key, boolean value) {
        SharedPreferences spUser = getSpSetting(context);
        SharedPreferences.Editor editor = spUser.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getSettingBoolean(Context context, String key, boolean value) {
        SharedPreferences spUser = getSpSetting(context);
        return spUser.getBoolean(key, value);
    }

}
