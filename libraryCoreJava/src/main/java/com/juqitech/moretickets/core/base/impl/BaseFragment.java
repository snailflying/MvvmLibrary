package com.juqitech.moretickets.core.base.impl;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.juqitech.moretickets.core.util.LogUtil;

import java.util.Iterator;
import java.util.List;

/**
 * @author zhiqiang
 * @date 2019-06-05
 * @desc
 */
public class BaseFragment extends Fragment {

    protected Activity mContext;

    private static final String TAG = "BaseLazyLoadFragment";
    private boolean mIsFirstVisible = true;
    private boolean isViewCreated = false;
    private boolean currentVisibleState = false;

    public BaseFragment() {
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (this.isViewCreated) {
            if (isVisibleToUser && !this.currentVisibleState) {
                this.dispatchUserVisibleHint(true);
            } else if (!isVisibleToUser && this.currentVisibleState) {
                this.dispatchUserVisibleHint(false);
            }
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.isViewCreated = true;
        if (!this.isHidden() && this.getUserVisibleHint()) {
            this.dispatchUserVisibleHint(true);
        }

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogUtil.i(TAG, this.getClass().getSimpleName() + "  onHiddenChanged dispatchChildVisibleState  hidden " + hidden);
        if (hidden) {
            this.dispatchUserVisibleHint(false);
        } else {
            this.dispatchUserVisibleHint(true);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!this.mIsFirstVisible && !this.isHidden() && !this.currentVisibleState && this.getUserVisibleHint()) {
            this.dispatchUserVisibleHint(true);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (this.currentVisibleState && this.getUserVisibleHint()) {
            this.dispatchUserVisibleHint(false);
        }

    }

    private boolean isFragmentVisible(Fragment fragment) {
        return !fragment.isHidden() && fragment.getUserVisibleHint();
    }

    private void dispatchUserVisibleHint(boolean visible) {
        if (!visible || !this.isParentInvisible()) {
            if (this.currentVisibleState != visible) {
                this.currentVisibleState = visible;
                if (visible) {
                    if (this.mIsFirstVisible) {
                        this.mIsFirstVisible = false;
                        this.onFragmentFirstVisible();
                    }

                    this.onFragmentResume();
                    this.dispatchChildVisibleState(true);
                } else {
                    this.dispatchChildVisibleState(false);
                    this.onFragmentPause();
                }

            }
        }
    }

    private boolean isParentInvisible() {
        BaseFragment fragment = (BaseFragment) this.getParentFragment();
        return fragment != null && !fragment.isSupportVisible();
    }

    private boolean isSupportVisible() {
        return this.currentVisibleState;
    }

    private void dispatchChildVisibleState(boolean visible) {
        FragmentManager childFragmentManager = this.getChildFragmentManager();
        List<Fragment> fragments = childFragmentManager.getFragments();
        if (!fragments.isEmpty()) {
            Iterator var4 = fragments.iterator();

            while (var4.hasNext()) {
                Fragment child = (Fragment) var4.next();
                if (child instanceof BaseFragment && !child.isHidden() && child.getUserVisibleHint()) {
                    ((BaseFragment) child).dispatchUserVisibleHint(visible);
                }
            }
        }

    }

    public void onFragmentFirstVisible() {
        LogUtil.i(TAG, this.getClass().getSimpleName() + "  对用户第一次可见");
    }

    public void onFragmentResume() {
        LogUtil.i(TAG, this.getClass().getSimpleName() + "  对用户可见");
    }

    public void onFragmentPause() {
        LogUtil.i(TAG, this.getClass().getSimpleName() + "  对用户不可见");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.isViewCreated = false;
        this.mIsFirstVisible = true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }
}
