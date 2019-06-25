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

    protected var mViewModel: VM? = null
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mViewModel = onCreateViewModel()
        initLifecycleObserver(lifecycle)
    }

    /**
     * 初始化 Presenter
     *
     * @return
     */
    protected abstract fun onCreateViewModel(): VM

    /**
     * 初使化 lifeCycle 跟 mViewModel 绑定生命周期
     *
     * @param lifecycle
     */
    @MainThread
    protected fun initLifecycleObserver(lifecycle: Lifecycle) {
        if (null != mViewModel) {
            lifecycle.addObserver(mViewModel!!)
        }
    }

}
