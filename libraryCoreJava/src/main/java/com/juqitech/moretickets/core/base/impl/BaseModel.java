package com.juqitech.moretickets.core.base.impl;

import androidx.annotation.NonNull;

import com.juqitech.moretickets.core.base.IBaseModel;
import com.juqitech.moretickets.core.http.HttpClient;
import com.juqitech.moretickets.core.util.LogUtil;

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
