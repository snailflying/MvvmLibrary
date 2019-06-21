package com.juqitech.moretickets.core.base.impl;

import android.os.Bundle;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import com.juqitech.moretickets.core.base.IBaseViewModel;
import com.juqitech.moretickets.core.util.LogUtil;

/**
 * @author zhanfeng
 * @date 2019-06-03
 * @desc MVP 模式 Activity
 */
public abstract class BaseMvvmActivity<VM extends IBaseViewModel> extends BaseActivity {

    private final String TAG = "BaseMvpActivity";

    protected VM mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = onCreateViewModel();
        initLifecycleObserver(getLifecycle());
    }

    /**
     * 初始化 ViewModel
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

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.i(TAG, "onPause");

    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.i(TAG, "onResume");

    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.i(TAG, "onStop");

    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.i(TAG, "onStart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG, "onDestroy");

    }
}
