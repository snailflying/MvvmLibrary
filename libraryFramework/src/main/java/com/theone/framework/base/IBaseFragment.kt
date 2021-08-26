package com.theone.framework.base

import io.reactivex.rxjava3.disposables.CompositeDisposable

/**
 * @Author zhiqiang
 * @Date 2019-08-02
 * @Description 指示Framework层的Fragment需要实现的接口
 */
interface IBaseFragment {
    val compositeDisposable: CompositeDisposable
}