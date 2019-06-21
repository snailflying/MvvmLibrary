package com.themone.core.base.impl

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.themone.core.base.IModel
import com.themone.core.base.IViewModel
import com.themone.core.entity.ApiResponse
import com.themone.core.exception.NetworkException
import com.themone.core.util.LogUtil
import io.reactivex.MaybeObserver
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * @author zhiqiang
 * @date 2019-06-04
 * @desc
 */
abstract class BaseViewModel<M : IModel> : ViewModel(), IViewModel {

    protected var mModel: M? = null

    /**
     * 为了方便kotlin引用
     */
    protected var mCompositeDisposable: CompositeDisposable? = CompositeDisposable()

    init {
        this.mModel = this.onCreateModel()
    }

    /**
     * 初始化 model
     *
     * @return model
     */
    protected abstract fun onCreateModel(): M


    @MainThread
    override fun onResume(owner: LifecycleOwner) {
        LogUtil.i(Companion.TAG, "onResume")
    }

    @MainThread
    override fun onPause(owner: LifecycleOwner) {
        LogUtil.i(Companion.TAG, "onPause")
    }


    override fun onCleared() {
        mModel?.onDestroy()
        mModel = null
        unSubscribe()
        LogUtil.i(Companion.TAG, "onCleared")
    }

    private fun unSubscribe() {
        if (null != mCompositeDisposable) {
            mCompositeDisposable!!.clear()
        }
    }

    protected fun addSubscribe(subscription: Disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = CompositeDisposable()
        }
        mCompositeDisposable!!.add(subscription)
    }

    /**
     * Rxjava的Observe绑定基类
     */
    protected abstract inner class BaseObserver<T> : Observer<ApiResponse<T>> {

        override fun onSubscribe(d: Disposable) {
            mCompositeDisposable!!.add(d)
        }

        override fun onNext(t: ApiResponse<T>) {
            if (t.isSuccess) {
                onResult(t.data)
            } else {
                onError(NetworkException(t.errorCode, t.message))
            }
        }

        /**
         * 返回去除ApiResponse后的data值
         * @param t 去除ApiResponse后的data值
         */
        abstract fun onResult(t: T?)

        override fun onError(e: Throwable) {
            LogUtil.e("BaseObserver", "e:$e")
        }

        override fun onComplete() {

        }
    }

    /**
     * Rxjava的Maybe绑定基类
     */
    protected abstract inner class BaseMaybeObserver<T> : MaybeObserver<ApiResponse<T>> {


        override fun onSubscribe(d: Disposable) {

        }

        /**
         * 返回去除ApiResponse后的data值
         * @param t 去除ApiResponse后的data值
         */
        abstract fun onResult(t: T?)

        override fun onSuccess(t: ApiResponse<T>) {
            if (t.isSuccess) {
                onResult(t.data)
            } else {
                onError(NetworkException(t.errorCode, t.message))
            }
        }

        override fun onError(e: Throwable) {
            LogUtil.e("BaseMaybeObserver", "e:$e")
        }

        override fun onComplete() {

        }
    }

    companion object {
        private const val TAG = "BaseViewModel"
    }
}
