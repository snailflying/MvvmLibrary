package com.theone.framework.util.countdown

import android.annotation.SuppressLint
import android.os.*
import java.lang.ref.WeakReference

/**
 * @Author zhiqiang
 * @Date 2019-12-12
 * @Description 在[android.os.CountDownTimer]基础上:
 * 1.解耦onTick和onFinish回调
 * 2.增加[onCountDownStart]和[onCountDownCancel]回调,且[onCountDownTick]在[start]调用时不会立即触发
 * 3.增加[onPause]暂停倒计时回调（不暂停倒计时）
 * 4.支持线程内调用（未详细测试）
 *
 * @param [mMillisInFuture]倒计时. 单位ms
 * @param [mCountdownInterval]倒计时间隔. 单位ms
 */
class CountDown(private val mMillisInFuture: Long, private val mCountdownInterval: Long) {
    private lateinit var mHandler: Handler
    private var mStopTimeInFuture: Long = 0

    /**
     * 回调
     */
    private var countDownListener: CountDownListener? = null

    /**
     * 用来实现线程内调用
     */
    private lateinit var mHandlerThread: HandlerThread


    /**
     * boolean representing if the timer was cancelled
     */
    private var mCancelled = false

    /**
     * 是否已经倒计时结束，为[onPause]功能服务
     */
    private var mFinished = false


    /**
     * 设置回调
     *
     * @param countDownListener
     * @return
     */
    fun setCountDownListener(countDownListener: CountDownListener?): CountDown {
        this.countDownListener = countDownListener
        return this
    }

    /**
     * Start the countdown.
     */
    @Synchronized
    fun start(): CountDown {
        if (countDownListener == null) {
            throw NullPointerException("setCountDownListener() must be called")
        }
        if (mHandler.hasMessages(MSG)) {
            return this
        }
        mCancelled = false
        if (isNeverStop()) {
            mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG), mCountdownInterval)
            countDownListener!!.onCountDownStart(mMillisInFuture)
            return this
        } else if (mMillisInFuture <= 0) {
            mFinished = true
            countDownListener!!.onCountDownFinish()
            return this
        }
        mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG), mCountdownInterval)
        countDownListener!!.onCountDownStart(mMillisInFuture)
        return this
    }

    /**
     * Cancel the countdown.
     */
    @Synchronized
    fun cancel() {
        mCancelled = true
        countDownListener?.onCountDownCancel()
        mHandler.removeMessages(MSG)
    }

    /**
     * 如果退出页面需要停止倒计时回调（倒计时时间依旧消耗），则调用
     */
    fun onResume() {
        if (!isPaused() || mHandler.hasMessages(MSG)) return

        if (isNeverStop()) {
            resendMessage()
            return
        }
        if (mFinished) return

        val millisLeft = mStopTimeInFuture - SystemClock.elapsedRealtime()
        if (millisLeft <= 0) {
            countDownListener?.onCountDownFinish()
            mCancelled = true
            mHandler.removeMessages(MSG)
        } else {
            resendMessage()
        }
    }

    /**
     * 如果退出页面需要停止倒计时回调（倒计时时间依旧消耗），则调用
     */
    fun onPause() {
        CURRENT_TIME = SystemClock.elapsedRealtime()
        mCancelled = true
        mHandler.removeMessages(MSG)
    }

    private fun isPaused(): Boolean {
        return CURRENT_TIME != 0L
    }

    private fun isNeverStop(): Boolean {
        return mMillisInFuture == NEVER_STOP
    }


    private fun resendMessage() {
        var delay = mCountdownInterval + CURRENT_TIME - SystemClock.elapsedRealtime()
        if (delay < 0) delay = 0
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG), delay)
    }

    // handles counting down
    @SuppressLint("HandlerLeak")
    private val mCallback: Handler.Callback = object : Handler.Callback {
        override fun handleMessage(msg: Message): Boolean {
            synchronized(this@CountDown) {
                if (mCancelled) {
                    return true
                }
                if (isNeverStop()) {
                    countDownListener?.onCountDownTick(mMillisInFuture)
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG), mCountdownInterval)
                    return false;
                }
                val millisLeft = mStopTimeInFuture - SystemClock.elapsedRealtime()
                if (millisLeft <= 0) {
                    mFinished = true
                    countDownListener?.onCountDownFinish()
                } else {
                    val lastTickStart = SystemClock.elapsedRealtime()
                    countDownListener?.onCountDownTick(millisLeft)
                    // take into account user's onTick taking time to execute
                    val lastTickDuration = SystemClock.elapsedRealtime() - lastTickStart
                    var delay: Long
                    if (millisLeft < mCountdownInterval) { // just delay until done
                        delay = millisLeft - lastTickDuration
                        // special case: user's onTick took more than interval to
// complete, trigger onFinish without delay
                        if (delay < 0) delay = 0
                    } else {
                        delay = mCountdownInterval - lastTickDuration
                        // special case: user's onTick took more than interval to
// complete, skip to next interval
                        while (delay < 0) delay += mCountdownInterval
                    }
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG), delay)
                }
            }
            return false;
        }

    }

    private fun isMainThread(): Boolean {
        return Looper.getMainLooper().thread == Thread.currentThread()
    }

    init {
        if (!isMainThread()) {
            mHandlerThread = HandlerThread("CountDownThread")
            mHandlerThread.start()
            mHandler = Handler(mHandlerThread.looper, mCallback)
        } else {
            mHandler = Handler(mCallback)
        }
    }

    open class SimpleCountDownListener : CountDownListener {
        override fun onCountDownStart(millisLeft: Long) {
        }

        override fun onCountDownCancel() {
        }

        override fun onCountDownTick(millisLeft: Long) {
        }

        override fun onCountDownFinish() {
        }
    }

    companion object {
        private const val MSG = 1
        const val NEVER_STOP: Long = -1
        private var CURRENT_TIME: Long = 0L
    }

}