package com.theone.framework.base

import com.themone.core.base.presentation.CoreMvvmFragment
import com.themone.core.base.presentation.IViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable


/**
 * @Author zhiqiang
 * @Date 2019-06-19
 * @Description
 */
abstract class BaseMvvmFragment<VM : IViewModel> : CoreMvvmFragment<VM>(), IBaseFragment {
    override val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}
