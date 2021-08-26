package com.theone.framework.base

import com.themone.core.base.impl.CoreFragment
import io.reactivex.rxjava3.disposables.CompositeDisposable


/**
 * @Author zhiqiang
 * @Date 2019-06-19
 * @Email liuzhiqiang@theone.com
 * @Description
 */
open class BaseFragment : CoreFragment(), IBaseFragment {
    override val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}