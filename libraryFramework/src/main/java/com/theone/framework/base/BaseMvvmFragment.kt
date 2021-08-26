package com.theone.framework.base

import com.themone.core.base.IViewModel
import com.themone.core.base.impl.CoreMvvmFragment
import io.reactivex.rxjava3.disposables.CompositeDisposable


/**
 * @Author zhiqiang
 * @Date 2019-06-19
 * @Email liuzhiqiang@theone.com
 * @Description
 */
abstract class BaseMvvmFragment<VM : IViewModel> : CoreMvvmFragment<VM>(),IBaseFragment{
    override val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}