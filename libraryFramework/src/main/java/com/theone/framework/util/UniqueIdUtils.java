package com.theone.framework.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * @Author ZhiQiang
 * @Date 2021/9/17
 * @Description 获取设备唯一标识
 */
public class UniqueIdUtils {
    private static final String TAG = "UniqueIdUtils";
    private static String uniqueID;
    private static String uniqueKey = "iunique_id";
    private static String uniqueIDFile = "iunique.txt";

    public static String getUniqueId(Context context) {
        //三步读取：内存中，存储的SP表中，外部存储文件中
        if (!TextUtils.isEmpty(uniqueID)) {
            Log.i(TAG, "getUniqueID: 内存中获取:" + uniqueID);
            return uniqueID;
        }
        uniqueID = getSp(context).getString(uniqueKey, "");
        if (!TextUtils.isEmpty(uniqueID)) {
            Log.i(TAG, "getUniqueID: SP中获取:" + uniqueID);
            return uniqueID;
        }
        readUniqueFile(context);
        if (!TextUtils.isEmpty(uniqueID)) {
            Log.i(TAG, "getUniqueID: 外部存储中获取:" + uniqueID);
            return uniqueID;
        }
        //两步创建：硬件获取；自行生成与存储
        getDeviceID(context);
        getAndroidID(context);
        getSerial();
        createUniqueID(context);
        getSp(context).edit().putString(uniqueKey, uniqueID).apply();
        return uniqueID;
    }

    private static SharedPreferences getSp(Context context) {
        return context.getSharedPreferences(getDefaultSharedPreferencesName(context), Context.MODE_PRIVATE);
    }

    private static String getDefaultSharedPreferencesName(Context context) {
        return context.getPackageName() + "_preferences";
    }

    /**
     * 1.需要android.permission.READ_PHONE_STATE权限，用户可以禁止授权给应用
     * 2.非手机设备没有IMEI/MEID
     * 3.IMEI/MEID理论上应该没有重复，实际却不是这样的，有些厂商生产的设备会有重复
     *
     * @param context
     * @return
     * @Deprecated 用{@see getAndroidID()}
     */
    @SuppressLint({"MissingPermission", "HardwareIds"})
    private static void getDeviceID(Context context) {
        if (!TextUtils.isEmpty(uniqueID)) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return;
        }
        String deviceId = null;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            ///26 27 28用IMEI
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                deviceId = telephonyManager.getImei();
            } else {
                deviceId = telephonyManager.getDeviceId();
            }
            //华为MatePad上，神奇的获得unknown，特此修复
            if (TextUtils.isEmpty(deviceId) || "unknown".equalsIgnoreCase(deviceId)) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        uniqueID = deviceId;
        Log.i(TAG, "getUniqueID: DeviceId获取成功:" + uniqueID);
    }

    /**
     * 1.设备重置后会重新生成ANDROID_ID
     * 2.部分厂商的设备存在ANDROID_ID重复的情况，同样不是绝对可靠
     * 3.Android O 版本中，对于设备上的每个应用和用户，都有不同的ANDROID_ID
     *
     * @param context
     * @return
     */
    @SuppressLint("HardwareIds")
    private static void getAndroidID(Context context) {
        if (!TextUtils.isEmpty(uniqueID)) {
            return;
        }
        String androidID = null;
        try {
            androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            if (TextUtils.isEmpty(androidID) || "9774d56d682e549c".equals(androidID)) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        uniqueID = androidID;
        Log.i(TAG, "getUniqueID: AndroidID获取成功:" + uniqueID);
    }

    private static void getSerial() {
        if (!TextUtils.isEmpty(uniqueID)) {
            return;
        }
        String serialNumber = getSerialNumber();
        if (TextUtils.isEmpty(serialNumber) || "unknown".equalsIgnoreCase(serialNumber)) {
            return;
        }
        uniqueID = serialNumber;
        Log.i(TAG, "getUniqueID: Serial获取成功:" + uniqueID);
    }

    /**
     * 1.经常会返回Unknown
     *
     * @return
     */
    private static String getSerialNumber() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Build.getSerial();
        }
        return Build.SERIAL;
    }

    private static void createUniqueID(Context context) {
        if (!TextUtils.isEmpty(uniqueID)) {
            return;
        }
        uniqueID = UUID.randomUUID().toString();
        Log.i(TAG, "getUniqueID: UUID生成成功:" + uniqueID);
        File filesDir =
                new File(getUniqueIdDirPath(context) + File.separator + context.getApplicationContext().getPackageName());
        if (!filesDir.exists()) {
            filesDir.mkdir();
        }
        File file = new File(filesDir, uniqueIDFile);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(uniqueID.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void readUniqueFile(Context context) {
        File filesDir = new File(getUniqueIdDirPath(context) + File.separator + context.getApplicationContext().getPackageName());
        File file = new File(filesDir, uniqueIDFile);
        if (file.exists()) {
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);
                byte[] bytes = new byte[(int) file.length()];
                inputStream.read(bytes);
                uniqueID = new String(bytes);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void clearUniqueFile(Context context) {
        File filesDir = new File(getUniqueIdDirPath(context) + File.separator + context.getApplicationContext().getPackageName());
        deleteFile(filesDir);
    }

    private static void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] listFiles = file.listFiles();
            if (listFiles == null) {
                return;
            }
            for (File listFile : listFiles) {
                deleteFile(listFile);
            }
        } else {
            file.delete();
        }
    }


    private static String getUniqueIdDirPath(Context context) {
        if (context.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.Q || !isHasSdcard()) {
            return getDiskFileDir(context, "Documents").getAbsolutePath();
        }
        return Environment.getExternalStoragePublicDirectory("Documents").getAbsolutePath();
    }

    private static File getDiskFileDir(Context context, String fileDir) {
        File directory;
        if (isHasSdcard()) {
            directory = context.getExternalFilesDir(null);
        } else {
            directory = context.getFilesDir();
        }
        if (directory == null) {
            context.getCacheDir();
        }
        if (fileDir != null) {
            File file = new File(directory, fileDir);
            if (!file.exists() && !file.mkdir()) {
                return directory;
            } else {
                return file;
            }
        }
        return directory;
    }

    private static boolean isHasSdcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }
}