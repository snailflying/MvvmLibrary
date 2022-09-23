package com.themone.core.base.presentation

import android.content.Context
import androidx.annotation.MainThread
import androidx.lifecycle.Lifecycle

/**
 * @Author ZhiQiang
 * @Date 2022/7/28
 * @Description
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
