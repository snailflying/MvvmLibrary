package com.juqitech.moretickets.core.base.impl

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.juqitech.moretickets.core.util.StatusBarUtil

/**
 * @author zhanfeng
 * @date 2019-06-04
 * @desc 简单 Activity，无业务逻辑可继承此类
 */
open class CoreActivity : AppCompatActivity() {

    protected lateinit var mContext: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        StatusBarUtil.setTransparentForWindow(this)
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        if (isFitsSystemWindows()) {
            StatusBarUtil.setFitsSystemWindows(this)
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }

    open fun isFitsSystemWindows(): Boolean {
        return true
    }

}