package com.themone.core.base.impl

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.themone.core.base.IModel
import com.themone.core.base.IViewModel

/**
 * @author zhiqiang
 * @date 2019-06-04
 * @desc
 */
abstract class CoreViewModel<M : IModel> : ViewModel(), IViewModel {
    protected val model: M by lazy { onCreateModel() }

    /**
     * 初始化 model
     *
     * @return model
     */
    protected abstract fun onCreateModel(): M

    @MainThread
    override fun onResume(owner: LifecycleOwner) {
        Log.i(TAG, "onResume")
    }

    @MainThread
    override fun onPause(owner: LifecycleOwner) {
        Log.i(TAG, "onPause")
    }


    override fun onCleared() {
        model.onDestroy()
        Log.i(TAG, "onCleared")
    }

    companion object {
        private const val TAG = "BaseViewModel"
    }
}
