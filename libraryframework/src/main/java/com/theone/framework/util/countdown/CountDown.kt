package com.theone.framework.util.countdown

import android.os.Handler
import android.os.Message
import android.os.SystemClock
import java.lang.ref.WeakReference

/**
 * @Author zhiqiang
 * @Date 2019-11-19
 * @Description 倒计时工具类，具体实现类
 */
class CountDown {
    private constructor()
    internal constructor(name: String, listener: CountDownListener, millisInFuture: Long, countdownInterval: Long) {
        this.name = name
        this.countDownListener = listener
        this.mMillisInFuture = millisInFuture
        mCountdownInterval = countdownInterval
    }

    lateinit var name: String
    private var mMillisInFuture: Long = 0
    var countDownListener: CountDownListener? = null

    private val timeHandler = TimeHandler(this)
    /**
     * 退出当前页面时的时间点
     */
    private var mExitPageTime: Long = -1
    /**
     * 退出当前页面时，倒计时剩余时间
     */
    private var mExitPageRemindTime: Long = -1

    /**
     * 绑定宿主的生命周期
     */
    fun onStart() {
        //定时器时间检测,判断之前是否有存在
        val startTime = System.currentTimeMillis()
        if (reenterPage()) {
            val outOfTime = startTime - mExitPageTime
            mStopTimeInFuture = mExitPageRemindTime - outOfTime
            mExitPageTime = -1
        }
    }

    /**
     * 绑定宿主的生命周期
     */
    fun onStop() {
        if (!reenterPage()) {
            mExitPageTime = System.currentTimeMillis()
            mExitPageRemindTime = mStopTimeInFuture
        }
    }

    /**
     * 绑定宿主的生命周期
     */
    fun onDestroy(isKeepDestroy: Boolean) {
        //重置exit page状态位
        mExitPageTime = -1
        mExitPageRemindTime = -1

        if (isKeepDestroy) {
            CountDownHelper.instance.keeperDestroy(this)
        } else {
            cancel()
        }

    }

    fun start(): CountDown {
        if (timeHandler.hasMessages(COUNT_DOWN)) {
            return this
        }
        return restart()
    }

    fun restart(): CountDown {
        countDownListener?.onCountDownStart(name)

        mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture
        timeHandler.sendEmptyMessageDelayed(COUNT_DOWN, mCountdownInterval)
        return this
    }

    fun cancel() {
        countDownListener?.onCountDownCancel(name)

        mCancelled = true
        mStopTimeInFuture = DEFAULT_MILLS_IN_FUTURE
        timeHandler.removeMessages(COUNT_DOWN)
        timeHandler.removeMessages(COUNT_DOWN_OVER)
        CountDownHelper.instance.removeLimiter(this)
    }


    internal fun checkCountDownOver(): Boolean {
        return mStopTimeInFuture != NEVER_STOP && mStopTimeInFuture <= 0
    }

    private fun reenterPage(): Boolean {
        return mExitPageTime != -1L
    }

    /**
     * 参考[android.os.CountDownTimer]
     */
    private class TimeHandler internal constructor(countDown: CountDown) : Handler() {

        internal var wrCountDown: WeakReference<CountDown> = WeakReference(countDown)

        override fun handleMessage(msg: Message) {
            val countDown = wrCountDown.get() ?: return
            synchronized(CountDown::class.java) {
                if (mCancelled) {
                    return
                }
                if (mStopTimeInFuture == NEVER_STOP) {
                    countDown.countDownListener?.onCountDownTick(countDown.name, NEVER_STOP)
                    return
                }

                val millisLeft = mStopTimeInFuture - SystemClock.elapsedRealtime()

                if (millisLeft <= 0) {
                    countDown.countDownListener?.onCountDownFinish(countDown.name)
                } else {
                    val lastTickStart = SystemClock.elapsedRealtime()
                    countDown.countDownListener?.onCountDownTick(countDown.name, millisLeft)

                    // take into account user's onCountDownTick taking time to execute
                    val lastTickDuration = SystemClock.elapsedRealtime() - lastTickStart
                    var delay: Long

                    if (millisLeft < mCountdownInterval) {
                        // just delay until done
                        delay = millisLeft - lastTickDuration

                        // special case: user's onCountDownTick took more than interval to
                        // complete, trigger onCountDownFinish without delay
                        if (delay < 0) {
                            delay = 0
                        }
                    } else {
                        delay = mCountdownInterval - lastTickDuration

                        // special case: user's onCountDownTick took more than interval to
                        // complete, skip to next interval
                        while (delay < 0) {
                            delay += mCountdownInterval
                        }
                    }

                    sendMessageDelayed(obtainMessage(COUNT_DOWN), delay)
                }
            }
        }
    }

    companion object {

        private const val COUNT_DOWN = 1
        private const val COUNT_DOWN_OVER = 2

        /**
         * 剩余时间如果是1123，则表示永不停止。除非被destroy
         */
        internal const val NEVER_STOP: Long = -1123
        private const val DEFAULT_MILLS_IN_FUTURE: Long = -1
        /**
         * 倒计时间隔，默认1秒
         */
        internal const val DEFAULT_INTERVAL: Long = 1000
        /**
         * 剩余时间
         */
        private var mStopTimeInFuture: Long = DEFAULT_MILLS_IN_FUTURE
        private var mCountdownInterval: Long = DEFAULT_INTERVAL
        private var mCancelled = false
    }

}