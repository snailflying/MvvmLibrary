package com.themone.core.base.impl;

import androidx.annotation.NonNull;

import com.themone.core.base.IBaseModel;
import com.themone.core.http.HttpClient;
import com.themone.core.util.LogUtil;

/**
 * @author zhiqiang
 * @date 2019-06-04
 * @desc
 */
public class BaseModel<T> implements IBaseModel {

    /**
     * apiService
     * 用于 retrofit 请求网络
     */
    protected T mApiService;

    public BaseModel(@NonNull Class<T> clazz) {
        this.mApiService = HttpClient.create(clazz);
    }

    @Override
    public void onDestroy() {
        LogUtil.i("BaseModel", "onDestroy");
        mApiService = null;
    }

}
