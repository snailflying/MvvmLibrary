package com.themone.core.base.impl

import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.themone.core.util.StatusBarUtil

/**
 * @author zhiqiang
 * @date 2019-06-04
 * @desc 简单 Activity，无业务逻辑可继承此类
 */
open class CoreActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.setTransparentForWindow(this)
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        if (isFitsSystemWindows()) {
            StatusBarUtil.setFitsSystemWindows(this)
        }
    }

    open fun isFitsSystemWindows(): Boolean {
        return true
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean = if (KeyEvent.KEYCODE_BACK == keyCode) {
        if (supportFragmentManager.backStackEntryCount == 0) {
            finish()
            true
        } else {
            try {
                supportFragmentManager.popBackStackImmediate()
            } catch (e: Exception) {
            }
            true
        }
    } else {
        super.onKeyDown(keyCode, event)
    }
}