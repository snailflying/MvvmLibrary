package com.theone.framework.util

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.media.*
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

/**
 * @Author zhiqiang
 * @Email liuzhiqiang@theone.com
 * @Date 2019-06-15
 * @Description 管理类(振动:[Vibrator]+提示音:[SoundPool],[MediaPlayer],[Ringtone])
 */
class VibrateUtils private constructor(val context: Context) {

    /**
     * 一次振动
     * @param context      Context实例
     * @param milliseconds 震动时长 , 单位毫秒
     */
    @SuppressLint("MissingPermission")
    fun vibrate(durationMill: Long = VIBRATE_DURATION) {
        val vibrator = context?.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(
                VibrationEffect.createOneShot(
                    durationMill,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            vibrator?.vibrate(durationMill)
        }
    }

    /**
     * 间歇式振动
     * @param context  Context实例
     * @param pattern  自定义震动模式 。数组中数字的含义依次是[静止时长，震动时长，静止时长，震动时长。。。]单位是毫秒
     * @param isRepeat true-> 反复震动，false-> 只震动一次
     */
    fun vibrateWave(
        pattern: LongArray = longArrayOf(
            VIBRATE_WAVE_DURATION,
            VIBRATE_WAVE_DURATION,
            VIBRATE_WAVE_DURATION,
            VIBRATE_WAVE_DURATION
        ),
        isRepeat: Boolean = false
    ) {
        val vibrator = context?.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createWaveform(pattern, if (isRepeat) 1 else -1))
        } else {
            vibrator?.vibrate(pattern, if (isRepeat) 1 else -1)
        }
    }


    companion object {
        private const val TAG = "VibrateUtils"

        private const val VIBRATE_DURATION = 100L;
        private const val VIBRATE_WAVE_DURATION = 130L;


        @Volatile
        private var instance: VibrateUtils? = null

        fun getInstance(context: Context): VibrateUtils {
            if (instance == null) {
                synchronized(VibrateUtils::class) {
                    if (instance == null) {
                        instance = VibrateUtils(context)
                    }
                }
            }
            return instance!!
        }

    }


}