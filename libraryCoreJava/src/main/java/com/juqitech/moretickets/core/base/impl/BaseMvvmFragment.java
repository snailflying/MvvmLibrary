package com.juqitech.moretickets.core.base.impl;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import com.juqitech.moretickets.core.base.IBaseViewModel;

/**
 * @author zhiqiang
 * @date 2019-06-05
 * @desc
 */
public abstract class BaseMvvmFragment<VM extends IBaseViewModel> extends BaseFragment {

    protected VM mViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mViewModel = onCreateViewModel();
        initLifecycleObserver(getLifecycle());
    }

    /**
     * 初始化 Presenter
     *
     * @return
     */
    protected abstract VM onCreateViewModel();

    /**
     * 初使化 lifeCycle 跟 mViewModel 绑定生命周期
     *
     * @param lifecycle
     */
    @MainThread
    protected void initLifecycleObserver(@NonNull Lifecycle lifecycle) {
        if (null != mViewModel) {
            lifecycle.addObserver(mViewModel);
        }
    }

}
