package com.theone.framework.util

import android.content.Context
import android.media.*
import android.os.Build
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
class BeeHelper constructor(val context: Context) {

    /**
     * 声音播放，轻量，适合播放较短提示音。
     * !!!必须在合适时机调用soundPool.release()释放资源!!!
     */
    var soundPool: SoundPool? = null

    /**
     * 播放声音，较重，不限播放时长
     */
    var mediaPlayer: MediaPlayer? = null

    init {
        initSoundPool(context)
    }

    /**
     * 播放成功声音
     * @param context Context
     */
    fun playBeeSuccess(@RawRes resId: Int = R.raw.beep) {
        //加载“成功”音效
        if (SOUND_SUCCESS == SOUND_DEFAULT_ERROR) {
            SOUND_SUCCESS = soundPool?.load(context, resId, 1) ?: SOUND_DEFAULT_ERROR
            soundPool?.setOnLoadCompleteListener { soundPool, sampleId, status -> playSound(SOUND_SUCCESS) }
        } else {
            playSound(SOUND_SUCCESS)
        }
    }

    /**
     * 播放错误声音
     * @param context Context
     */
    fun playBeeError(@RawRes resId: Int = R.raw.beep) {
        //加载“失败”音效
        if (SOUND_ERROR == SOUND_DEFAULT_ERROR) {
            SOUND_ERROR = soundPool?.load(context, resId, 1) ?: SOUND_DEFAULT_ERROR
            soundPool?.setOnLoadCompleteListener { soundPool, sampleId, status -> playSound(SOUND_ERROR) }
        } else {
            playSound(SOUND_ERROR)

        }
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
        return soundPool
    }

    /**
     * 声音播放，轻量，适合播放较短提示音。
     *
     * @param number Int 音频的序号(load到SoundPool的顺序，从1开始)
     */
    private fun playSound(number: Int): SoundPool? {
        mediaPlayer?.reset()
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
        @RawRes id: Int = R.raw.beep,
        listener: PlayerCompleteListener? = null
    ): MediaPlayer? {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
            if (Build.VERSION.SDK_INT >= 21) {
                val attributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
                mediaPlayer?.setAudioAttributes(attributes)
            } else {
                mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            }
        } else {
            mediaPlayer?.stop()
            mediaPlayer?.reset()
        }

        try {
            context.resources?.openRawResourceFd(id)?.use {
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
        SOUND_SUCCESS = SOUND_DEFAULT_ERROR
        SOUND_ERROR = SOUND_DEFAULT_ERROR
    }


    //MediaPlayer播放完毕监听
    interface PlayerCompleteListener {
        fun onComplete(mp: MediaPlayer)
    }

    companion object {
        private const val TAG = "BeeAndVibrateUtils"

        private const val BEEP_VOLUME = 0.1F;

        private val SOUND_DEFAULT_ERROR = -1
        private var SOUND_SUCCESS = SOUND_DEFAULT_ERROR;
        private var SOUND_ERROR = SOUND_DEFAULT_ERROR;


    }

}