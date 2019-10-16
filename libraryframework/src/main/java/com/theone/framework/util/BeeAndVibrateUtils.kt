package com.theone.framework.util

/**
 * @Author zhiqiang
 * @Date 2019-06-22
 * @Email liuzhiqiang@theone.com
 * @Description
 */

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.media.*
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.annotation.RawRes
import com.theone.framework.R
import java.io.IOException


/**
 * @Author zhiqiang
 * @Email liuzhiqiang@theone.com
 * @Date 2019-06-15
 * @Description 管理类(振动:[Vibrator]+提示音:[SoundPool],[MediaPlayer],[Ringtone])
 */
class BeeAndVibrateUtils private constructor(context: Context) {

    /**
     * 声音播放，轻量，适合播放较短提示音。
     * !!!必须在合适时机调用soundPool.release()释放资源!!!
     */
    var soundPool: SoundPool? = null

    /**
     * 播放声音，较重，不限播放时长
     */
    var mediaPlayer: MediaPlayer? = null

    fun getInstance(context: Context): BeeAndVibrateUtils {
        if (instance == null) {
            synchronized(BeeAndVibrateUtils::class) {
                if (instance == null) {
                    instance = BeeAndVibrateUtils(context)
                }
            }
        }
        return instance!!
    }


    init {
        initSoundPool(context)
    }

    /**
     * 一次振动
     * @param context      Context实例
     * @param milliseconds 震动时长 , 单位毫秒
     */
    @SuppressLint("MissingPermission")
    fun vibrate(context: Context?, durationMill: Long = VIBRATE_DURATION) {
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
        context: Context?,
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

    /**
     * 播放成功声音
     * @param context Context
     */
    fun playBeeSuccess(): SoundPool? {
        return playSound(SOUND_SUCCESS)
    }

    /**
     * 播放错误声音
     * @param context Context
     */
    fun playBeeError(): SoundPool? {
        return playSound(SOUND_ERROR)
    }

    private fun initSoundPool(context: Context?): SoundPool? {
        if (context == null) return null
        if (soundPool == null) {
            soundPool = if (Build.VERSION.SDK_INT >= 21) {
                val builder = SoundPool.Builder()
                builder.setMaxStreams(1)
                val attrBuilder = AudioAttributes.Builder()
                attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC)
                builder.setAudioAttributes(attrBuilder.build())
                builder.build()
            } else {
                SoundPool(1, AudioManager.STREAM_SYSTEM, 5)
            }
        }
        //加载“成功”音效
        SOUND_SUCCESS = soundPool?.load(context, R.raw.beep, 1) ?: 1
        //加载“失败”音效
        SOUND_ERROR = soundPool?.load(context, R.raw.beep, 1) ?: 2
        return soundPool
    }

    /**
     * 声音播放，轻量，适合播放较短提示音。
     *  左\右声道的音量控制, 0.0 到 1.0
     * @param number Int 音频的序号(load到SoundPool的顺序，从1开始)
     */
    private fun playSound(number: Int): SoundPool? {
        soundPool?.play(
            number,
            BEEP_VOLUME,
            BEEP_VOLUME, 0, 0, 1F
        )
        return soundPool
    }

    /**
     * 播放系统自带声音,默认通知音
     * @param context Context
     * @param type Int
     * @return Ringtone
     */
    fun playRingtone(context: Context?, type: Int = RingtoneManager.TYPE_ALL): Ringtone? {
        if (context == null) return null
        val notification = RingtoneManager.getDefaultUri(type)
        return RingtoneManager.getRingtone(context, notification).also {
            it.play()
        }
    }

    /**
     * 播放声音，较重，不限播放时长
     *
     * @param context Context
     * @param listener PlayerCompleteListener 允许为空
     */
    fun playMediaSound(
        context: Context?, @RawRes id: Int,
        listener: PlayerCompleteListener? = null
    ): MediaPlayer? {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
        } else {
            mediaPlayer?.release()
            mediaPlayer?.reset()
        }
        if (Build.VERSION.SDK_INT >= 21) {
            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            mediaPlayer?.setAudioAttributes(attributes)
        } else {
            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        }
        try {
            context?.resources?.openRawResourceFd(id)?.use {
                mediaPlayer?.setDataSource(
                    it.fileDescriptor,
                    it.startOffset, it.length
                )
                mediaPlayer?.setVolume(
                    BEEP_VOLUME,
                    BEEP_VOLUME
                )
                mediaPlayer?.setOnCompletionListener { mp ->
                    mp.stop()
                    mp.seekTo(0)
                    listener?.onComplete(mp)
                }
                mediaPlayer?.setOnPreparedListener {
                    mediaPlayer?.start()
                }
                mediaPlayer?.prepareAsync()
            }
        } catch (ioe: IOException) {
            Log.e(TAG, "$ioe")
            mediaPlayer?.release()
            mediaPlayer = null
        }
        return mediaPlayer
    }

    /**
     * 释放播放资源
     */
    fun release() {
        mediaPlayer?.release()
        soundPool?.release()
    }


    //MediaPlayer播放完毕监听
    interface PlayerCompleteListener {
        fun onComplete(mp: MediaPlayer)
    }

    companion object {
        private const val TAG = "BeeAndVibrateUtils"

        private const val BEEP_VOLUME = 0.1F;
        private const val VIBRATE_DURATION = 100L;
        private const val VIBRATE_WAVE_DURATION = 130L;

        private var SOUND_SUCCESS = 1;
        private var SOUND_ERROR = 2;

        private var instance: BeeAndVibrateUtils? = null
    }

}