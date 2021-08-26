package com.theone.framework.base

import android.content.Context
import com.themone.core.base.impl.CoreActivity

import com.theone.framework.util.I18NUtil
import io.reactivex.rxjava3.disposables.CompositeDisposable

/**
 * @Author zhiqiang
 * @Date 2019-06-19
 * @Email liuzhiqiang@theone.com
 * @Description
 */
open class BaseActivity : CoreActivity(), IBaseActivity {
    override val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(I18NUtil.updateResource(newBase))
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}