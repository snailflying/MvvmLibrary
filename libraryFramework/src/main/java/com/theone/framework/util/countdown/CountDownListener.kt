package com.theone.framework.util.countdown

interface CountDownListener {
    /**
     * 倒计时开始
     */
    fun onCountDownStart(millisLeft: Long)

    /**
     * 倒计时被取消（不正常结束）
     */
    fun onCountDownCancel()

    /**
     * 倒计时嘀嗒触发,onStart时不触发
     */
    fun onCountDownTick(millisLeft: Long)

    /**
     * 倒计时正常结束
     */
    fun onCountDownFinish()
}