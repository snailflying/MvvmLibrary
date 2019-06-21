package com.themone.core.base.impl;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.themone.core.base.IBaseViewModel;
import com.themone.core.base.IUiStatusProvider;
import com.themone.core.base.MultiViewStatus;
import com.themone.theone.library.R;

import static com.themone.core.base.MultiViewStatus.STATUS_HTTP_ERROR;
import static com.themone.core.base.MultiViewStatus.STATUS_LOADING;
import static com.themone.core.base.MultiViewStatus.STATUS_MAIN;

/**
 * @author zhiqiang
 * @date 2019-06-06
 * @desc 提供默认的 loading、error、empty 等状态 UI
 * 需要更改状态的 view Id 强制规定命名 为 id=@+id/multiStatusView
 */
public abstract class BaseMultiViewFragment<VM extends IBaseViewModel> extends BaseMvvmFragment<VM> implements IUiStatusProvider {

    protected View viewError;
    protected View viewLoading;
    protected View mMultiStatusView;
    private ViewGroup mParent;

    private int mViewErrorRes;

    private MultiViewStatus mViewState = STATUS_MAIN;
    private boolean isErrorViewAdded = false;
    private ViewGroup.LayoutParams mMultiStatusLP;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUiStatus(view, savedInstanceState);
    }

    private void initUiStatus(View view, Bundle savedInstanceState) {
        mMultiStatusView = view.findViewById(R.id.multiStatusView);
        mMultiStatusLP = mMultiStatusView.getLayoutParams();
        if (mMultiStatusView == null) {
            throw new IllegalStateException(
                    "The subclass of RootActivity must contain a View named 'view_main'.");
        }
        if (!(mMultiStatusView.getParent() instanceof ViewGroup)) {
            throw new IllegalStateException(
                    "view_main's ParentView should be a ViewGroup.");
        }
        mParent = (ViewGroup) mMultiStatusView.getParent();
        mViewErrorRes = getHttpErrorRes();
    }

    @Override
    public void onStatusLoading() {
        if (mViewState == STATUS_LOADING) {
            return;
        }
        if (null == viewLoading) {
            viewLoading = View.inflate(mContext, R.layout.view_progresss, null);
            if (null == viewLoading) {
                throw new IllegalStateException(
                        "A View should be named 'view_progresss' in viewLoadingResource.");
            }
            //设置同一个layoutParams
            mParent.addView(viewLoading, mMultiStatusLP);
            viewLoading = mParent.findViewById(R.id.loadingView);
        }
        hideCurrentView();
        mViewState = STATUS_LOADING;
        viewLoading.setVisibility(View.VISIBLE);
//        ivLoading.start();
    }

    @Override
    public void onStatusHttpError() {
        if (mViewState == STATUS_HTTP_ERROR) {
            return;
        }
        if (!isErrorViewAdded) {
            isErrorViewAdded = true;
            viewError = View.inflate(mContext, mViewErrorRes, null);
            if (viewError == null) {
                throw new IllegalStateException(
                        "A View should be named 'view_error' in ErrorLayoutResource.");
            }
            //设置同一个layoutParams
            mParent.addView(viewError, mMultiStatusLP);
            viewError = mParent.findViewById(R.id.errorView);
            viewError.setOnClickListener(event -> retryNetWork());
        }
        hideCurrentView();
        mViewState = STATUS_HTTP_ERROR;
        viewError.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStatusEmpty() {
        onStatusMain();
    }

    @Override
    public void onStatusMain() {
        if (mViewState == STATUS_MAIN) {
            return;
        }
        hideCurrentView();
        mViewState = STATUS_MAIN;
        mMultiStatusView.setVisibility(View.VISIBLE);

    }

    @Override
    public void onStatusServiceEx() {
        onStatusHttpError();
    }

    /**
     * 点击重试
     */
    protected void retryNetWork() {
    }

    private void hideCurrentView() {
        switch (mViewState) {
            case STATUS_EMPTY:
            case STATUS_MAIN:
                mMultiStatusView.setVisibility(View.GONE);
                break;
            case STATUS_LOADING:
//                ivLoading.stop();
                viewLoading.setVisibility(View.GONE);
                break;
            case STATUS_HTTP_ERROR:
            case STATUS_NETWORK_FAILED:
                if (null != viewError) {
                    viewError.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
    }

    @LayoutRes
    int getHttpErrorRes() {
        return R.layout.view_error;
    }

}
