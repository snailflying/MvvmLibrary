package com.themone.core.base.impl

import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.themone.core.util.StatusBarUtil
import io.reactivex.disposables.CompositeDisposable

/**
 * @author zhiqiang
 * @date 2019-06-04
 * @desc 简单 Activity，无业务逻辑可继承此类
 */
open class CoreActivity : AppCompatActivity() {

    protected val compositeDisposable: CompositeDisposable = CompositeDisposable()

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

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    /**
     * 1.statusBar的底色来自Imageview等元素，而非根部局
     * 2.statusBar的底色Fragment
     * 以上情况，isFitsSystemWindows()设置为false，且手动调用[StatusBarUtil.addStatusBarOffsetForView]
     * @return Boolean
     */
    open fun isFitsSystemWindows(): Boolean {
        return true
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean =
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            if (supportFragmentManager.backStackEntryCount == 0) {
                super.onKeyDown(keyCode, event)
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