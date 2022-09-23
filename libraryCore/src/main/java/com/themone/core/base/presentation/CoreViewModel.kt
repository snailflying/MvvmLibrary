package com.themone.core.base.presentation

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.themone.core.base.domain.IRepository

/**
 * @Author ZhiQiang
 * @Date 2022/7/28
 * @Description
 */
abstract class CoreViewModel<M : IRepository> : ViewModel(), IViewModel {

    protected val repository: M by lazy { onCreateRepository() }

    /**
     * 初始化 model
     *
     * @return model
     */
    protected abstract fun onCreateRepository(): M

    @MainThread
    override fun onResume(owner: LifecycleOwner) {
        Log.i(TAG, "onResume")
    }

    @MainThread
    override fun onPause(owner: LifecycleOwner) {
        Log.i(TAG, "onPause")
    }


    override fun onCleared() {
        repository.onDestroy()
        Log.i(TAG, "onCleared")
    }

    companion object {
        private const val TAG = "CoreViewModel"
    }
}
