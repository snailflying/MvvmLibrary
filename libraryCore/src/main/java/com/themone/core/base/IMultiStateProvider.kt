package com.themone.core.base

import android.view.View

/**
 * @author zhiqiang
 * @date 2019-06-06
 * @desc loading、error 等通过弹窗
 */
interface IMultiStateProvider {


    /**
     * loading 页面
     */
    fun showStateLoading(view: View? = null)

    /**
     * 网络异常，请求失败
     */
    fun showErrorState(view: View? = null)

    /**
     * 空数据
     */
    fun showStateEmpty(view: View? = null)

    /**
     * 正常数据页面
     */
    fun showStateMain()

    /**
     * 请求成功，后台服务异常
     */
    fun showStateFailed(view: View? = null)
}
