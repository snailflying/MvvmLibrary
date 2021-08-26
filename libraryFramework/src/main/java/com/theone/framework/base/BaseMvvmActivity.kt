package com.theone.framework.base

import android.content.Context

import com.themone.core.base.IViewModel
import com.themone.core.base.impl.CoreMvvmActivity
import com.themone.core.util.LogUtil
import com.theone.framework.util.I18NUtil
import io.reactivex.rxjava3.disposables.CompositeDisposable

/**
 * @Author zhiqiang
 * @Date 2019-06-19
 * @Email liuzhiqiang@theone.com
 * @Description
 */
abstract class BaseMvvmActivity<VM : IViewModel> : CoreMvvmActivity<VM>(), IBaseActivity {
    override val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(I18NUtil.updateResource(newBase))
        LogUtil.i("attachBaseContext")
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}