package com.juqitech.moretickets.core.base.impl;

import androidx.annotation.MainThread;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;
import com.juqitech.moretickets.core.base.IBaseModel;
import com.juqitech.moretickets.core.base.IBaseViewModel;
import com.juqitech.moretickets.core.entity.ApiResponse;
import com.juqitech.moretickets.core.util.LogUtil;
import io.reactivex.MaybeObserver;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * @author zhiqiang
 * @date 2019-06-04
 * @desc
 */
public abstract class BaseViewModel<M extends IBaseModel> extends ViewModel implements IBaseViewModel {

    private final String TAG = "BaseViewModel";

    protected M mModel;

    public BaseViewModel() {
        this.mModel = onCreateModel();
    }

    /**
     * 初始化 model
     *
     * @return model
     */
    protected abstract M onCreateModel();


    @MainThread
    @Override
    public void onResume(LifecycleOwner owner) {
        LogUtil.i(TAG, "onResume");
    }

    @MainThread
    @Override
    public void onPause(LifecycleOwner owner) {
        LogUtil.i(TAG, "onPause");
    }


    @Override
    protected void onCleared() {
        if (null != mModel) {
            mModel.onDestroy();
            this.mModel = null;
        }
        unSubscribe();
        LogUtil.i(TAG, "onCleared");
    }

    /**
     * 为了方便kotlin引用
     */
    protected CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    private void unSubscribe() {
        if (null != mCompositeDisposable) {
            mCompositeDisposable.clear();
        }
    }

    protected void addSubscribe(Disposable subscription) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(subscription);
    }

    /**
     * Rxjava的Observe绑定基类
     */
    protected abstract class BaseObserver<T> implements Observer<ApiResponse<T>> {

        @Override
        public void onSubscribe(Disposable d) {
            mCompositeDisposable.add(d);
        }

        @Override
        public void onNext(ApiResponse<T> t) {
            if (t.isSuccess()) {
                onResult(t.getData());
            } else {
                onError(new Exception(t.getMessage()));
            }
        }

        /**
         * 返回去除ApiResponse后的data值
         * @param t 去除ApiResponse后的data值
         */
        public abstract void onResult(T t);

        @Override
        public void onError(Throwable e) {
            LogUtil.e("BaseObserver", "e:" + e);
        }

        @Override
        public void onComplete() {

        }
    }

    /**
     * Rxjava的Maybe绑定基类
     */
    protected abstract class BaseMaybeObserver<T> implements MaybeObserver<ApiResponse<T>> {


        @Override
        public void onSubscribe(Disposable d) {

        }

        /**
         * 返回去除ApiResponse后的data值
         * @param t 去除ApiResponse后的data值
         */
        public abstract void onResult(T t);

        @Override
        public void onSuccess(ApiResponse<T> t) {
            if (t.isSuccess()) {
                onResult(t.getData());
            } else {
                onError(new Exception(t.getMessage()));
            }
        }

        @Override
        public void onError(Throwable e) {
            LogUtil.e("BaseMaybeObserver", "e:" + e);
        }

        @Override
        public void onComplete() {

        }
    }
}
