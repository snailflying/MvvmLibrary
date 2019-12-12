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
class CountDown internal constructor(val name: String, val listener: CountDownListener, private val millisInFuture: Long = DEFAULT_MILLS_IN_FUTURE, val countdownInterval: Long = DEFAULT_INTERVAL) {

    /**
     * 回调接口
     */
    var countDownListener: CountDownListener? = listener
    /**
     * 剩余时间
     */
    private var mStopTimeInFuture: Long = 0
        get() = if (millisInFuture == NEVER_STOP) {
            NEVER_STOP
        } else {
            field
        }

    /**
     * 退出当前页面时的时间点
     */
    private var mExitPageTime: Long = -1
    /**
     * 退出当前页面时，倒计时剩余时间
     */
    private var mExitPageRemindTime: Long = -1

    private val timeHandler = TimeHandler(this)
        get() {
            if (!field.isAvailable) {
                field.removeMessages(COUNT_DOWN)
                return TimeHandler(this)
            }
            return field
        }

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
            CountDownFactory.INSTANCE.keeperDestroy(this)
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

        mStopTimeInFuture = SystemClock.elapsedRealtime() + millisInFuture
        timeHandler.sendEmptyMessageDelayed(COUNT_DOWN, countdownInterval)
        return this
    }

    fun cancel() {
        countDownListener?.onCountDownCancel(name)

        mCancelled = true
        mStopTimeInFuture = DEFAULT_MILLS_IN_FUTURE
        timeHandler.removeMessages(COUNT_DOWN)
        CountDownFactory.INSTANCE.removeLimiter(this)
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

        internal val wrCountDown: WeakReference<CountDown> = WeakReference(countDown)

        internal val isAvailable: Boolean = wrCountDown.get() != null

        override fun handleMessage(msg: Message) {
            val countDown = wrCountDown.get() ?: return
            synchronized(CountDown::class.java) {
                if (mCancelled) {
                    return
                }
                if (countDown.mStopTimeInFuture == NEVER_STOP) {
                    countDown.countDownListener?.onCountDownTick(countDown.name, NEVER_STOP)
                    return
                }

                val millisLeft = countDown.mStopTimeInFuture - SystemClock.elapsedRealtime()

                if (millisLeft <= 0) {
                    countDown.countDownListener?.onCountDownFinish(countDown.name)
                } else {
                    val lastTickStart = SystemClock.elapsedRealtime()
                    countDown.countDownListener?.onCountDownTick(countDown.name, millisLeft)

                    // take into account user's onCountDownTick taking time to execute
                    val lastTickDuration = SystemClock.elapsedRealtime() - lastTickStart
                    var delay: Long

                    if (millisLeft < countDown.countdownInterval) {
                        // just delay until done
                        delay = millisLeft - lastTickDuration

                        // special case: user's onCountDownTick took more than interval to
                        // complete, trigger onCountDownFinish without delay
                        if (delay < 0) {
                            delay = 0
                        }
                    } else {
                        delay = countDown.countdownInterval - lastTickDuration

                        // special case: user's onCountDownTick took more than interval to
                        // complete, skip to next interval
                        while (delay < 0) {
                            delay += countDown.countdownInterval
                        }
                    }

                    sendMessageDelayed(obtainMessage(COUNT_DOWN), delay)
                }
            }
        }
    }

    companion object {

        private const val COUNT_DOWN = 1

        /**
         * 剩余时间如果是1123，则表示永不停止。除非被destroy或cancel
         */
        internal const val NEVER_STOP: Long = -1123
        /**
         * 默认剩余时间，-1表示已结束
         */
        private const val DEFAULT_MILLS_IN_FUTURE: Long = -1
        /**
         * 倒计时间隔，默认1秒
         */
        internal const val DEFAULT_INTERVAL: Long = 1000

        private var mCancelled = false
    }

}