package com.theone.framework.base.multi

import android.view.View

/**
 * @author zhiqiang
 * @date 2019-06-06
 * @desc loading、error 等通过弹窗
 */
interface IMultiStateProvider {


    /**
     * 根据状态显示页面
     */
    fun showStateView(state: MultiViewState)

    /**
     * loading 页面
     */
    fun showStateLoading(view: View? = null)

    /**
     * 网络异常，请求失败
     */
    fun showStateError(view: View? = null)

    /**
     * 空数据
     */
    fun showStateEmpty(view: View? = null)

    /**
     * 正常数据页面
     */
    fun showStateMain()
}
