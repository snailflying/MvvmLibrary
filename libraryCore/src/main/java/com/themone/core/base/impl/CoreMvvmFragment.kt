package com.themone.core.base.impl

import android.content.Context
import android.os.Bundle
import androidx.annotation.MainThread
import androidx.lifecycle.Lifecycle
import com.themone.core.base.IViewModel

/**
 * @author zhiqiang
 * @date 2019-06-05
 * @desc
 */
abstract class CoreMvvmFragment<VM : IViewModel> : CoreFragment() {

    protected lateinit var viewModel: VM
        private set


    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = onCreateViewModel()
        initLifecycleObserver(lifecycle)
    }

    /**
     * 初始化 Presenter
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
        lifecycle.addObserver(viewModel)
    }

}
