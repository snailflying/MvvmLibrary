package com.theone.framework.base

import com.themone.core.base.IModel
import com.themone.core.base.impl.CoreViewModel
import com.theone.framework.http.ApiResponse
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable

/**
 * @Author zhiqiang
 * @Date 2019-08-02
 * @Description
 */
abstract class BaseViewModel<M : IModel>: CoreViewModel<M>(),IFrameworkViewModel {

    protected abstract inner class BaseObserver<T> : Observer<ApiResponse<T>> {
        override fun onSubscribe(d: Disposable) {
            compositeDisposable.add(d)
        }

        /**
         * 返回去除ApiResponse后的data值
         *
         * @param data 去除ApiResponse后的data值,可能为空
         */
        abstract fun onResultSuccess(data: T?)

        /**
         * 返回data为空或者数据错误时的数据
         *
         * @param statusCode
         * @param comments
         */
        abstract fun onResultFailed(statusCode: Int, comments: String?)

        override fun onComplete() { //空实现
        }

        override fun onNext(apiResponse: ApiResponse<T>) {
            if (apiResponse.isSuccess) {
                onResultSuccess(apiResponse.data)
            } else {
                onResultFailed(apiResponse.statusCode, apiResponse.message)
            }
        }
    }
}