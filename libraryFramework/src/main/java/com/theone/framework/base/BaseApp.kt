package com.theone.framework.base

import android.app.Application
import com.chenenyu.router.Router
import com.theone.framework.BuildConfig
import com.theone.framework.router.AppMatcher
import com.theone.framework.util.SpUtil
import com.theone.framework.widget.toast.ToastUtils
import kotlin.properties.Delegates

/**
 * @author zhiqiang
 * @date 2019-06-05
 * @desc
 */
interface BaseApp {


    /**
     * 初始化,必须在自定义Application类的onCreate内第一个调用
     */
    fun initApplication(application: Application) {
        Companion.application = application
        SpUtil.init(application)
        Router.registerMatcher(AppMatcher())
        ToastUtils.init(application)
    }

    companion object {

        var application: Application by Delegates.notNull()
            private set

        fun isDebug(): Boolean {
            return BuildConfig.DEBUG
        }
    }
}
