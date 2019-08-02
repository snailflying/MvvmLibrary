package com.themone.core.base.impl

import android.os.Bundle
import androidx.annotation.MainThread
import androidx.lifecycle.Lifecycle
import com.themone.core.base.IViewModel
import com.themone.core.util.LogUtil

/**
 * @author zhiqiang
 * @date 2019-06-03
 * @desc MVP 模式 Activity
 */
abstract class CoreMvvmActivity<VM : IViewModel> : CoreActivity() {

    private val TAG = "CoreMvvmActivity"

    protected var viewModel: VM? = null
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = onCreateViewModel()
        initLifecycleObserver(lifecycle)
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
    protected fun initLifecycleObserver(lifecycle: Lifecycle) {
        if (null != viewModel) {
            lifecycle.addObserver(viewModel!!)
        }
    }

    override fun onPause() {
        super.onPause()
        LogUtil.i(TAG, "onPause")

    }

    override fun onResume() {
        super.onResume()
        LogUtil.i(TAG, "onResume")

    }

    override fun onStop() {
        super.onStop()
        LogUtil.i(TAG, "onStop")

    }

    override fun onStart() {
        super.onStart()
        LogUtil.i(TAG, "onStart")
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtil.i(TAG, "onDestroy")

    }

}
