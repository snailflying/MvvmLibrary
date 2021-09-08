package com.theone.framework.base

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.themone.core.base.impl.CoreActivity

import com.theone.framework.util.I18NUtil
import com.theone.framework.util.StatusBarUtil
import io.reactivex.rxjava3.disposables.CompositeDisposable

/**
 * @Author zhiqiang
 * @Date 2019-06-19
 * @Email liuzhiqiang@theone.com
 * @Description
 */
open class BaseActivity : CoreActivity(), IBaseActivity {
    override val compositeDisposable: CompositeDisposable = CompositeDisposable()
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

    /**
     * 1.statusBar的底色来自Imageview等元素，而非根部局
     * 2.statusBar的底色Fragment
     * 以上情况，isFitsSystemWindows()设置为false，且手动调用[StatusBarUtil.addStatusBarOffsetForView]
     * @return Boolean
     */
    open fun isFitsSystemWindows(): Boolean {
        return false
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(I18NUtil.updateResource(newBase))
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}