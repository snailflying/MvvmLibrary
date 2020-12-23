package com.theone.framework.util

import android.content.Context
import android.media.AudioManager
import androidx.annotation.IntDef
import com.theone.framework.base.BaseApp
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

/**
 * @Author ZhiQiang
 * @Date 2020/9/2
 * @Description 音量控制
 */
class AudioVolumeHelper private constructor(context: Context) {
    private val TAG = "AudioMngHelper"
    private val OpenLog = true
    private val audioManager: AudioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var NOW_AUDIO_TYPE = TYPE_MUSIC
    private var NOW_FLAG = FLAG_NOTHING
    private var VOICE_STEP_100 = 2 //0-100的步进。

    @IntDef(TYPE_MUSIC, TYPE_ALARM, TYPE_RING)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class TYPE

    @IntDef(FLAG_SHOW_UI, FLAG_PLAY_SOUND, FLAG_NOTHING)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class FLAG

    fun getSystemMaxVolume(): Int = audioManager.getStreamMaxVolume(NOW_AUDIO_TYPE)

    fun getSystemCurrentVolume(): Int = audioManager.getStreamVolume(NOW_AUDIO_TYPE)

    /**
     * 以0-100为范围，获取当前的音量值
     * @return  获取当前的音量值
     */
    fun get100CurrentVolume(): Int {
        return 100 * getSystemCurrentVolume() / getSystemMaxVolume()
    }

    /**
     * 修改步进值
     * @param step  step
     * @return  this
     */
    fun setVolumeStep100(step: Int): AudioVolumeHelper {
        VOICE_STEP_100 = step
        return this
    }

    /**
     * 改变当前的模式，对全局API生效
     * @param type
     * @return
     */
    fun setAudioType(@TYPE type: Int): AudioVolumeHelper {
        NOW_AUDIO_TYPE = type
        return this
    }

    /**
     * 改变当前FLAG，对全局API生效
     * @param flag
     * @return
     */
    fun setFlag(@FLAG flag: Int): AudioVolumeHelper {
        NOW_FLAG = flag
        return this
    }

    fun addVolumeSystem(): AudioVolumeHelper {
        audioManager.adjustStreamVolume(NOW_AUDIO_TYPE, AudioManager.ADJUST_RAISE, NOW_FLAG)
        return this
    }

    fun subVolumeSystem(): AudioVolumeHelper {
        audioManager.adjustStreamVolume(NOW_AUDIO_TYPE, AudioManager.ADJUST_LOWER, NOW_FLAG)
        return this
    }

    /**
     * 调整音量，自定义
     * @param num   0-100
     * @return  改完后的音量值
     */
    fun setVolume100(num: Int): Int {
        var a = ceil(num * getSystemMaxVolume() * 0.01).toInt()
        a = max(a, 0)
        a = min(a, 100)
        audioManager.setStreamVolume(NOW_AUDIO_TYPE, a, NOW_FLAG)
        return get100CurrentVolume()
    }

    /**
     * 步进加，步进值可修改
     * 0——100
     * @return  改完后的音量值
     */
    fun addVolume100(): Int {
        var a = ceil((VOICE_STEP_100 + get100CurrentVolume()) * getSystemMaxVolume() * 0.01).toInt()
        a = max(a, 0)
        a = min(a, 100)
        audioManager.setStreamVolume(NOW_AUDIO_TYPE, a, NOW_FLAG)
        return get100CurrentVolume()
    }

    /**
     * 步进减，步进值可修改
     * 0——100
     * @return  改完后的音量值
     */
    fun subVolume100(): Int {
        var a =
            floor((get100CurrentVolume() - VOICE_STEP_100) * getSystemMaxVolume() * 0.01).toInt()
        a = max(a, 0)
        a = min(a, 100)
        audioManager.setStreamVolume(NOW_AUDIO_TYPE, a, NOW_FLAG)
        return get100CurrentVolume()
    }

    companion object {
        /**
         * 封装：STREAM_类型
         */
        private const val TYPE_MUSIC = AudioManager.STREAM_MUSIC
        private const val TYPE_ALARM = AudioManager.STREAM_ALARM
        private const val TYPE_RING = AudioManager.STREAM_RING

        /**
         * 封装：FLAG
         */
        private const val FLAG_SHOW_UI = AudioManager.FLAG_SHOW_UI
        private const val FLAG_PLAY_SOUND = AudioManager.FLAG_PLAY_SOUND
        private const val FLAG_NOTHING = 0
        private var INSTANCE: AudioVolumeHelper? = null
        fun getInstance(context: Context = BaseApp.application): AudioVolumeHelper {
            if (INSTANCE == null) {
                synchronized(AudioVolumeHelper) {
                    if (INSTANCE == null) {
                        INSTANCE = AudioVolumeHelper(context)
                    }
                }
            }
            return INSTANCE!!
        }

    }

}