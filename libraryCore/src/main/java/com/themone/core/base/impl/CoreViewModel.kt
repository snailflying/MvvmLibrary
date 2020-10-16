package com.themone.core.base.impl

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.themone.core.base.IModel
import com.themone.core.base.IViewModel
import com.themone.core.util.LogUtil
import io.reactivex.rxjava3.disposables.CompositeDisposable

/**
 * @author zhiqiang
 * @date 2019-06-04
 * @desc
 */
abstract class CoreViewModel<M : IModel> : ViewModel(), IViewModel {
    /**
     * 初始化 model
     *
     * @return model
     */
    protected abstract var model: M

    /**
     * 为了方便kotlin引用
     */
    protected val compositeDisposable: CompositeDisposable = CompositeDisposable()

    @MainThread
    override fun onResume(owner: LifecycleOwner) {
        LogUtil.i(Companion.TAG, "onResume")
    }

    @MainThread
    override fun onPause(owner: LifecycleOwner) {
        LogUtil.i(Companion.TAG, "onPause")
    }


    override fun onCleared() {
        model.onDestroy()
        unSubscribe()
        LogUtil.i(Companion.TAG, "onCleared")
    }

    private fun unSubscribe() {
        compositeDisposable.clear()
    }


    companion object {
        private const val TAG = "BaseViewModel"
    }
}
