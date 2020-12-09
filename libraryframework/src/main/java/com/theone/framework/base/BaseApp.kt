package com.theone.framework.base

import android.app.Application
import com.chenenyu.router.Router
import com.theone.framework.BuildConfig
import com.theone.framework.router.AppMatcher
import kotlin.properties.Delegates

/**
 * @author zhiqiang
 * @date 2019-06-05
 * @desc
 */
open class BaseApp : Application() {

    override fun onCreate() {
        super.onCreate()
        application = this
        Router.registerMatcher(AppMatcher())
    }


    companion object {

        var application: BaseApp by Delegates.notNull()
            private set

        fun isDebug(): Boolean {
            return BuildConfig.DEBUG
        }
    }
}
