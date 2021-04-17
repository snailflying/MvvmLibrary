package com.theone.framework.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import com.theone.framework.base.BaseApp
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*


/**
 * @Author zhiqiang
 * @Date 2019-06-20
 * @Email liuzhiqiang@theone.com
 * @Description
 */
object DeviceUtil {

    private const val TAG = "DeviceUtil"
    private const val CHINA_CARRIER_UNKNOWN = "0"
    private const val CHINA_MOBILE = "1"
    private const val CHINA_UNICOM = "2"
    private const val CHINA_TELECOM = "3"
    private const val CHINA_TIETONG = "4"

    const val os = "0"   //此值代表Android，不要动

    private var VERSION_NAME: String = "1.0"
    private var VERSION_CODE: Int = 0

    private val TRACKING_DEVICE_ID: String = "TRACKING_DEVICE_ID"


    /**
     * 获得系统版本
     *
     * @return os version
     */
    fun getOsVersion(): String = Build.VERSION.RELEASE

    /**
     * 获取设备品牌
     *
     * @return branding
     */
    fun getBranding(): String = Build.BRAND

    /**
     * 获得制造商
     *
     * @return manufacturer
     */
    fun getManufacturer(): String = Build.MANUFACTURER

    /**
     * 设备的名字
     *
     * @return device model
     */
    fun getDeviceModel(): String = Build.MODEL


    /**
     * 获得本地语言和国家
     *
     * @return Language +county
     */
    fun getLocal(): String {
        val locale = Locale.getDefault()
        return locale.language + "_" + locale.country
    }


    /**
     * 获得Android设备唯一标识：Device_id
     *
     * @param context context
     * @return device id
     */
    @SuppressLint("HardwareIds", "MissingPermission", "PrivateApi")
    fun getDeviceId(context: Context = BaseApp.application): String {

        //优化device id的策略，尽量减少漂移
        if (!TextUtils.isEmpty(SpUtil.getSp(context).getString(TRACKING_DEVICE_ID, null))) {
            return SpUtil.getSp(context).getString(TRACKING_DEVICE_ID, null) ?: ""

        }

        var result: String? = ""
        try {
            if (checkPermissions(context, "android.permission.READ_PHONE_STATE")) {
                var deviceId: String? = null
                if (context.getSystemService(
                        Context
                            .TELEPHONY_SERVICE
                    ) != null
                ) {
                    val telephonyManager = context.getSystemService(
                        Context.TELEPHONY_SERVICE
                    ) as? TelephonyManager
                    deviceId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        telephonyManager?.imei
                    } else {
                        telephonyManager?.deviceId
                    }
                }
                var backId = ""
                if (!TextUtils.isEmpty(deviceId) && deviceId?.contains("00000000000") != true) {
                    backId = deviceId!!
                    backId = backId.replace("0", "")
                }

                if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(backId)) {
                    deviceId = try {
                        val c = Class.forName("android.os.SystemProperties")
                        val get = c.getMethod("get", String::class.java, String::class.java)
                        get.invoke(c, "ro.serialno", "unknown") as String
                    } catch (t: Exception) {
                        ""
                    }

                }

                result = if (!deviceId.isNullOrEmpty()) {
                    deviceId
                } else {
                    Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
                }
            } else {
                result =
                    Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            }
        } catch (ignored: Exception) {

        }

        SpUtil.getSp(context).edit().putString(TRACKING_DEVICE_ID, result!!).apply()
        return result
    }

    @SuppressLint("HardwareIds", "MissingPermission", "PrivateApi")
    fun getIMEI(context: Context): String? {
        return if (checkPermissions(context, "android.permission.READ_PHONE_STATE")) {
            if (context.getSystemService(Context.TELEPHONY_SERVICE) != null) {
                val telephonyManager =
                    context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    telephonyManager.imei
                } else {
                    telephonyManager.deviceId
                }
            } else {
                null
            }
        } else {
            null
        }


    }


    /**
     * 获得应用的包名
     *
     * @param context context
     * @return package name
     */
    fun getPackageName(context: Context): String {
        return context.packageName
    }


    /**
     * 获得应用名
     *
     * @param context context
     * @return package name
     */
    fun getAppName(context: Context): String {
        var appName = ""
        try {
            appName = context.packageManager.getApplicationLabel(context.applicationInfo) as String
        } catch (ignored: Exception) {
        }

        return appName
    }


    /**
     * 获得当前应用的版本号
     *
     * @param context context
     * @return App Version
     */

    @Synchronized
    fun getAppVersionName(context: Context = BaseApp.application): String {
        if (!TextUtils.isEmpty(VERSION_NAME)) {
            return VERSION_NAME
        }

        var info: PackageInfo? = null
        try {
            info = context.packageManager.getPackageInfo(context.packageName, 0)
        } catch (ignored: PackageManager.NameNotFoundException) {
        }

        if (info != null) {
            VERSION_NAME = info.versionName
            VERSION_CODE = info.versionCode
        }
        return VERSION_NAME
    }

    @Synchronized
    fun getAppVersionCode(context: Context = BaseApp.application): Int {
        if (VERSION_CODE != 0) {
            return VERSION_CODE
        }
        var info: PackageInfo? = null
        try {
            info = context.packageManager.getPackageInfo(context.packageName, 0)
        } catch (ignored: PackageManager.NameNotFoundException) {
        }

        if (info != null) {
            VERSION_CODE = info.versionCode
            VERSION_NAME = info.versionName
        }
        return VERSION_CODE
    }


    private fun carryByOperator(manager: TelephonyManager?): String {
        val operatorString = manager!!.simOperator

        return if ("46000" == operatorString || "46002" == operatorString || "46007" == operatorString) {
            //中国移动
            CHINA_MOBILE
        } else if ("46001" == operatorString || "46006" == operatorString) {
            //中国联通
            CHINA_UNICOM
        } else if ("46003" == operatorString || "46005" == operatorString) {
            //中国电信
            CHINA_TELECOM
        } else if ("46020" == operatorString) {
            CHINA_TIETONG
        } else {
            CHINA_CARRIER_UNKNOWN
        }
    }

    /**
     * 获得网管硬件地址
     *
     * @param context context
     * @return Mac Address
     */
    @SuppressLint("HardwareIds", "WifiManagerPotentialLeak")
    fun getMacAddress(context: Context): String {
        if (!checkPermissions(context, "android.permission.ACCESS_WIFI_STATE")) {
            return ""
        }
        var result = ""

        try {
            if (context.getSystemService(Context.WIFI_SERVICE) != null) {
                val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val wifiInfo = wifiManager.connectionInfo
                if (wifiInfo != null) {
                    result = wifiInfo.macAddress
                }
            }
        } catch (ignored: Exception) {

        }

        // Log.i("MAC Address", "macAdd:" + result);
        return result
    }


    /**
     * 判断当前设备是手机还是平板
     *
     * @param context
     * @return 平板返回 True，手机返回 False
     */
    fun isTablet(context: Context): Boolean {
        return context.resources.configuration.screenLayout and Configuration
            .SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }


    /**
     * 读取application 节点  meta-data 信息
     */
    fun getMetaDataFromApplication(tag: String, context: Context = BaseApp.application): String {
        var metaData: String = ""

        try {
            val appInfo = context.packageManager
                .getApplicationInfo(
                    context.packageName,
                    PackageManager.GET_META_DATA
                )
            if (appInfo.metaData != null && appInfo.metaData.getString(tag) != null) {
                metaData = appInfo.metaData!!.getString(tag)!!
                if (!metaData.isEmpty()) {
                    metaData = metaData.trim { it <= ' ' }
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return metaData
    }

    /**
     * 检查网络是否可用
     *
     * @param context
     * @return
     */
    fun isNetworkAvailable(context: Context = BaseApp.application): Boolean {
        val manager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val networkinfo = manager.activeNetworkInfo
        return !(networkinfo == null || !networkinfo.isAvailable)
    }

    /**
     * 检查权限是否开启
     *
     * @param permission
     * @return true or false
     */
    @JvmStatic
    fun checkPermissions(context: Context?, permission: String): Boolean {

        val localPackageManager = context!!.applicationContext.packageManager
        return localPackageManager.checkPermission(
            permission,
            context.applicationContext.packageName
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 获取本机Ip地址
     * @param context Context
     * @return String?
     */
    fun getIpAddress(context: Context = BaseApp.application): String? {
        val info = (context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
        if (info != null && info.isConnected) {
            // 3/4g网络
            if (info.type == ConnectivityManager.TYPE_MOBILE) {
                try {
                    val en: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
                    while (en.hasMoreElements()) {
                        val intf: NetworkInterface = en.nextElement()
                        val enumIpAddr: Enumeration<InetAddress> = intf.inetAddresses
                        while (enumIpAddr.hasMoreElements()) {
                            val inetAddress: InetAddress = enumIpAddr.nextElement()
                            if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                                return inetAddress.getHostAddress()
                            }
                        }
                    }
                } catch (e: SocketException) {
                    e.printStackTrace()
                }
            } else if (info.type == ConnectivityManager.TYPE_WIFI) {
                //  wifi网络
                val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as? WifiManager
                val wifiInfo = wifiManager?.connectionInfo
                return intIP2StringIP(wifiInfo?.ipAddress ?: 0)
            } else if (info.type == ConnectivityManager.TYPE_ETHERNET) {
                // 有限网络
                return getLocalIp()
            }
        }
        return null
    }

    private fun intIP2StringIP(ip: Int): String {
        return (ip and 0xFF).toString() + "." +
                (ip shr 8 and 0xFF) + "." +
                (ip shr 16 and 0xFF) + "." +
                (ip shr 24 and 0xFF)
    }


    // 获取有限网IP
    private fun getLocalIp(): String? {
        try {
            val en: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf: NetworkInterface = en.nextElement()
                val enumIpAddr: Enumeration<InetAddress> = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress: InetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        return inetAddress.getHostAddress()
                    }
                }
            }
        } catch (ex: SocketException) {
        }
        return "0.0.0.0"
    }
}
