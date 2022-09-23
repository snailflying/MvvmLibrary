package com.theone.framework.base

import android.app.Application
import android.content.pm.ApplicationInfo
import com.theone.framework.util.SpUtil
import kotlin.properties.Delegates

/**
 * @author zhiqiang
 * @date 2019-06-05
 * @desc
 */
interface BaseApp {

    companion object {
        /**
         * 初始化,必须在自定义Application类的onCreate内第一个调用
         * 为了方便Java调用，挪到companion object内
         */
        fun initApplication(app: Application) {
            application = app
            SpUtil.init(app);
        }

        var application: Application by Delegates.notNull()
            private set

        fun isDebug(): Boolean {
            return application.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        }
    }
}
