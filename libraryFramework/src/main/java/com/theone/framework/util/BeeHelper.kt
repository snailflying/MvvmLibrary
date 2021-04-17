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
class BeeHelper constructor(
    private val context: Context,
    private val useSoundPool: Boolean = true
) {

    /**
     * 声音播放，轻量，适合播放较短提示音。
     * !!!必须在合适时机调用soundPool.release()释放资源!!!
     */
    private var soundPool: SoundPool? = null

    /**
     * 播放声音，较重，不限播放时长
     */
    private var mediaPlayer: MediaPlayer? = null

    private var SOUND_SUCCESS = SOUND_DEFAULT_ERROR;
    private var SOUND_ERROR = SOUND_DEFAULT_ERROR;

    init {
        if (useSoundPool) {
            initSoundPool(context)
        }
    }

    /**
     * 播放成功声音
     * @param context Context
     */
    fun playBeeSuccess(@RawRes resId: Int = R.raw.beep) {
        if (!useSoundPool) {
            playMediaSound(resId)
            return
        }
        if (SOUND_SUCCESS == SOUND_DEFAULT_ERROR) {
            SOUND_SUCCESS = soundPool?.load(context, resId, 1) ?: SOUND_DEFAULT_ERROR
            soundPool?.setOnLoadCompleteListener { soundPool, sampleId, status ->
                playSound(
                    SOUND_SUCCESS
                )
            }
        } else {
            playSound(SOUND_SUCCESS)
        }
    }

    /**
     * 播放错误声音
     * @param context Context
     */
    fun playBeeError(@RawRes resId: Int = R.raw.beep) {
        if (!useSoundPool) {
            playMediaSound(resId)
            return
        }
        if (SOUND_ERROR == SOUND_DEFAULT_ERROR) {
            SOUND_ERROR = soundPool?.load(context, resId, 1) ?: SOUND_DEFAULT_ERROR
            soundPool?.setOnLoadCompleteListener { soundPool, sampleId, status ->
                playSound(
                    SOUND_ERROR
                )
            }
        } else {
            playSound(SOUND_ERROR)

        }
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
     * 播放声音，较重，不限播放时长
     *
     * @param context Context
     * @param listener PlayerCompleteListener 允许为空
     */
    fun playMediaSound(
        @RawRes id: Int = R.raw.beep,
        listener: PlayerCompleteListener? = null
    ) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val builder = AudioAttributes.Builder()
                val attrBuilder = AudioAttributes.Builder()
                attrBuilder.setLegacyStreamType(AudioManager.STREAM_ALARM)
                mediaPlayer?.setAudioAttributes(builder.build())
            } else {
                mediaPlayer?.setAudioStreamType(AudioManager.STREAM_ALARM)
            }
            try {
                context?.resources?.openRawResourceFd(id)?.use {
                    mediaPlayer?.setDataSource(
                        it.fileDescriptor,
                        it.startOffset, it.length
                    )
                    mediaPlayer?.setVolume(BEEP_VOLUME, BEEP_VOLUME)
                    mediaPlayer?.prepareAsync()

                    mediaPlayer?.setOnPreparedListener {
                        if (shouldPlayBeep()) {
                            mediaPlayer?.start()
                        }
                    }
                    mediaPlayer?.setOnCompletionListener { mp ->
                        listener?.onComplete(mp)
                    }
                    mediaPlayer?.setOnErrorListener { mp, what, extra ->
                        mediaPlayer?.release()
                        mediaPlayer = null
                        true
                    }
                }


            } catch (ioe: IOException) {
                Log.e(TAG, "$ioe")
                mediaPlayer?.release()
                mediaPlayer = null
            }
        } else {
            mediaPlayer?.stop()
            mediaPlayer?.reset()
            context?.resources?.openRawResourceFd(id)?.use {
                mediaPlayer?.setDataSource(
                    it.fileDescriptor,
                    it.startOffset, it.length
                )
                mediaPlayer?.prepareAsync()

                mediaPlayer?.setOnPreparedListener {
                    if (shouldPlayBeep()) {
                        mediaPlayer?.start()
                    }
                }
            }
        }
    }


    private fun initSoundPool(context: Context?): SoundPool? {
        if (context == null) return null
        if (soundPool == null) {
            soundPool = if (Build.VERSION.SDK_INT >= 21) {
                val builder = SoundPool.Builder()
                builder.setMaxStreams(MAX_STREAMS)
                val attrBuilder = AudioAttributes.Builder()
                attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC)
                builder.setAudioAttributes(attrBuilder.build())
                builder.build()
            } else {
                SoundPool(MAX_STREAMS, AudioManager.STREAM_SYSTEM, 0)
            }
        }
        return soundPool
    }


    private fun shouldPlayBeep(): Boolean {
        val audioService =
            context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        //如果手机不是普通模式就不让其有声音
        return audioService?.ringerMode == AudioManager.RINGER_MODE_NORMAL
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

        private const val BEEP_VOLUME = 1F;

        private val SOUND_DEFAULT_ERROR = -1

        /**
         * 声音池中允许同时存在的声音数量
         */
        private val MAX_STREAMS = 5

    }

}