package com.theone.framework.util.countdown

interface CountDownListener {


    /**
     * 倒计时开始
     * @param countDownName 倒计时名称
     */
    fun onCountDownStart(countDownName: String)

    /**
     * 倒计时被取消（不正常结束）
     * @param countDownName 倒计时名称
     */
    fun onCountDownCancel(countDownName: String)

    /**
     * 倒计时嘀嗒触发
     * @param countDownName 倒计时名称
     */
    fun onCountDownTick(countDownName: String, millisLeft: Long)

    /**
     * 倒计时正常结束
     * @param countDownName 倒计时名称
     */
    fun onCountDownFinish(countDownName: String)
}