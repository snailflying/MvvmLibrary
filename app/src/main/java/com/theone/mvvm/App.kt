package com.theone.mvvm

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.theone.framework.base.BaseApp

/**
 * @Author zhiqiang
 * @Date 2019-06-27
 * @Description
 */
class App : Application(), BaseApp {
    override fun onCreate() {
        super.onCreate()
        BaseApp.initApplication(this)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}
