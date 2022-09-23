package com.theone.framework.base

import com.themone.core.base.presentation.IViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable

/**
 * @Author zhiqiang
 * @Date 2019-08-02
 * @Description 指示Framework层的ViewModel需要实现的接口
 */
interface IBaseViewModel: IViewModel {
    val compositeDisposable: CompositeDisposable
}