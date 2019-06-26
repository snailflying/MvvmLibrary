package com.themone.core.base.impl

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.themone.core.base.IModel
import com.themone.core.base.IViewModel
import com.themone.core.util.LogUtil
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * @author zhiqiang
 * @date 2019-06-04
 * @desc
 */
abstract class BaseViewModel<M : IModel> : ViewModel(), IViewModel {
    /**
     * 初始化 model
     *
     * @return model
     */
    protected abstract var mModel: M?

    /**
     * 为了方便kotlin引用
     */
    protected var mCompositeDisposable: CompositeDisposable? = CompositeDisposable()
        private set

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

    companion object {
        private const val TAG = "BaseViewModel"
    }
}
