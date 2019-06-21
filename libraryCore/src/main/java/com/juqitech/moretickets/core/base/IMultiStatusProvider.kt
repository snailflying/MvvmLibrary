package com.juqitech.moretickets.core.base

/**
 * @author zhiqiang
 * @date 2019-06-06
 * @desc loading、error 等通过弹窗
 */
interface IMultiStatusProvider {

    /**
     * loading 页面
     */
    fun onStatusLoading()

    /**
     * 网络异常，请求失败
     */
    fun onStatusHttpError()

    /**
     * 空数据
     */
    fun onStatusEmpty()

    /**
     * 正常数据页面
     */
    fun onStatusMain()

    /**
     * 请求成功，后台服务异常
     */
    fun onStatusServiceEx()
}
