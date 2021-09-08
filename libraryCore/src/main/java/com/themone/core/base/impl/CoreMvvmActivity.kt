package com.themone.core.base.impl

import android.os.Bundle
import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.Lifecycle
import com.themone.core.base.IViewModel

/**
 * @author zhiqiang
 * @date 2019-06-03
 * @desc MVP 模式 Activity
 */
abstract class CoreMvvmActivity<VM : IViewModel> : CoreActivity() {

    private val TAG = "CoreMvvmActivity"

    protected lateinit var viewModel: VM
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = onCreateViewModel()
        initLifecycleObserver(lifecycle, viewModel)
    }

    /**
     * 初始化 ViewModel
     *
     * @return
     */
    protected abstract fun onCreateViewModel(): VM

    /**
     * 初使化 lifeCycle 跟 viewModel 绑定生命周期
     *
     * @param lifecycle
     */
    @MainThread
    protected fun initLifecycleObserver(lifecycle: Lifecycle, viewModel: VM) {
        lifecycle.addObserver(viewModel)
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "onPause")

    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume")
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "onStop")
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "onStart")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
    }

}
