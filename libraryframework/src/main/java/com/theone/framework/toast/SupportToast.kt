package com.theone.framework.toast

import android.app.Application
import com.theone.framework.toast.BaseToast

/**
 * @Author zhiqiang
 * @Email liuzhiqiang@theone.com
 * @Date 2019-06-22
 * @Description Toast 无通知栏权限兼容
 */
internal class SupportToast(application: Application) : BaseToast(application) {

    // 吐司弹窗显示辅助类
    private val mToastHelper: ToastHelper

    init {
        mToastHelper = ToastHelper(this, application)
    }

    override fun show() {
        // 显示吐司
        mToastHelper.show()
    }

    override fun cancel() {
        // 取消显示
        mToastHelper.cancel()
    }
}