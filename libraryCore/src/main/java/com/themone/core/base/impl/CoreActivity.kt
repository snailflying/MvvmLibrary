package com.themone.core.base.impl

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.themone.core.util.StatusBarUtil
import io.reactivex.rxjava3.disposables.CompositeDisposable

/**
 * @author zhiqiang
 * @date 2019-06-04
 * @desc 简单 Activity，无业务逻辑可继承此类
 */
open class CoreActivity : AppCompatActivity() {

    protected val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //状态栏透明
        StatusBarUtil.transparentStatusBar(this)
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        //状态栏偏移
        if (isFitsSystemWindows()) {
            StatusBarUtil.setFitsSystemWindows(this)
        }
    }

    override fun setContentView(view: View?) {
        super.setContentView(view)
        //状态栏偏移
        if (isFitsSystemWindows()) {
            StatusBarUtil.setFitsSystemWindows(this)
        }
    }

    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
        super.setContentView(view, params)
        //状态栏偏移
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
        return false
    }

    /**
     * Fragment返回键处理，见[CoreFragment.onBackPressed]
     */
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 0) {
            super.onBackPressed()
        } else {
            try {
                supportFragmentManager.popBackStackImmediate()
            } catch (e: Exception) {
            }
        }
    }
}