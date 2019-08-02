package com.themone.core.base.impl

import android.app.Application
import kotlin.properties.Delegates

/**
 * @author zhiqiang
 * @date 2019-06-05
 * @desc
 */
open class CoreApp : Application() {

    override fun onCreate() {
        super.onCreate()
        APPLICATION = this
    }

    companion object {

        @get:Synchronized
        var APPLICATION: CoreApp by Delegates.notNull()
            private set
    }
}
