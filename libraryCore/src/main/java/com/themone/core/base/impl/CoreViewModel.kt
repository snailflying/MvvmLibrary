package com.themone.core.base.impl

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.themone.core.base.IModel
import com.themone.core.base.IViewModel
import com.themone.core.util.LogUtil

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

    @MainThread
    override fun onResume(owner: LifecycleOwner) {
        LogUtil.i(TAG, "onResume")
    }

    @MainThread
    override fun onPause(owner: LifecycleOwner) {
        LogUtil.i(TAG, "onPause")
    }


    override fun onCleared() {
        model.onDestroy()
        LogUtil.i(TAG, "onCleared")
    }

    companion object {
        private const val TAG = "BaseViewModel"
    }
}
