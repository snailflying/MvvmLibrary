package com.theone.framework.util

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log


/**
 * @Author zhiqiang
 * @Date 2019-06-20
 * @Email liuzhiqiang@theone.com
 * @Description
 */
object DeviceUtil {

    private const val TAG = "DeviceUtil"
    /**
     * Return the application's version name.
     *
     * @param packageName The name of the package.
     * @return the application's version name
     */
    fun getAppVersionName(context: Context): String? {
        return try {
            val pm = context.packageManager
            pm.getPackageInfo(context.packageName, 0)?.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, e.message)
            ""
        }

    }

    /**
     * Return the application's version code.
     *
     * @param packageName The name of the package.
     * @return the application's version code
     */
    fun getAppVersionCode(context: Context): Int {
        return try {
            val pm = context.packageManager
            val pi = pm.getPackageInfo(context.packageName, 0)
            pi?.versionCode ?: -1
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, e.message)
            -1
        }

    }
}
