package com.juqitech.moretickets.core.util;

import android.text.TextUtils;
import android.util.Log;

import com.juqitech.moretickets.library.BuildConfig;


/**
 * @author zhiqiang
 * @date 2019-06-04
 * @desc
 */
public class LogUtil {

    private static boolean isDebug = BuildConfig.DEBUG;

    public static void e(String tag, String message) {
        if (isDebug && checkParams(tag, message)) {
            Log.e(tag, message);
        }
    }

    public static void w(String tag, String message) {
        if (isDebug && checkParams(tag, message)) {
            Log.w(tag, message);
        }
    }

    public static void d(String tag, String message) {
        if (isDebug && checkParams(tag, message)) {
            Log.d(tag, message);
        }
    }

    public static void i(String tag, String message) {
        if (isDebug && checkParams(tag, message)) {
            Log.i(tag, message);
        }
    }

    public static void v(String tag, String message) {
        if (isDebug && checkParams(tag, message)) {
            Log.v(tag, message);
        }
    }

    private static boolean checkParams(String tag, String message) {
        return !TextUtils.isEmpty(tag);
    }
}
