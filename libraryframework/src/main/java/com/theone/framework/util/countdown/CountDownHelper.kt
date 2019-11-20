package com.theone.framework.util.countdown

import android.text.TextUtils
import java.util.concurrent.ConcurrentHashMap

/**
 * @Author zhiqiang
 * @Date 2019-11-19
 * @Description 倒计时工具类，工厂类。对外只提供[createTimeLimiter]
 */
class CountDownHelper private constructor() {

    private var countDownList: ConcurrentHashMap<String, CountDown> = ConcurrentHashMap()

    /**
     *
     * @param countDownName String 倒计时名称
     * @param countDownListener TimeKeeper 倒计时回调
     * @param millisInFuture Long 倒计时持续时间（默认永不停止）
     * @param countDownInterval Long 倒计时间隔
     * @return TimeLimiter
     */
    fun createTimeLimiter(countDownName: String, countDownListener: CountDownListener, millisInFuture: Long = CountDown.NEVER_STOP, countDownInterval: Long = CountDown.DEFAULT_INTERVAL): CountDown {
        if (TextUtils.isEmpty(countDownName)) {
            throw RuntimeException("countDownName can not be empty")
        }
        return if (countDownList.contains(countDownName)) {
            countDownList[countDownName]!!
        } else{
            val limiter = CountDown(countDownName, countDownListener, millisInFuture, countDownInterval)
            addLimiter(limiter)
            limiter
        }
    }

    private fun addLimiter(countDown: CountDown?) {
        if (countDown == null) {
            return
        }
        if (countDownList.contains(countDown)) {
            return
        }
        countDownList.put(countDown.name,countDown)
    }

    internal fun keeperDestroy(countDown: CountDown?) {
        if (countDown == null) {
            return
        }
        if (countDown.checkCountDownOver()) {
            countDownList.remove(countDown.name)
        } else {
            countDown.countDownListener = null
        }
    }

    internal fun removeLimiter(countDown: CountDown?) {
        if (countDown == null) {
            return
        }
        countDownList.remove(countDown.name)
    }

    companion object {
        val instance: CountDownHelper by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            CountDownHelper()
        }
    }
}
