package com.juqitech.moretickets.core.base.impl

import com.juqitech.moretickets.core.base.IModel
import com.juqitech.moretickets.core.http.HttpClient
import com.juqitech.moretickets.core.util.LogUtil

/**
 * @author zhanfeng
 * @date 2019-06-04
 * @desc
 */
open class BaseModel<T>(clazz: Class<T>) : IModel {

    /**
     * apiService
     * 用于 retrofit 请求网络
     */
    protected var mApiService: T? = null

    init {
        this.mApiService = HttpClient.create(clazz)
    }

    override fun onDestroy() {
        LogUtil.i("BaseModel", "onDestroy")
        mApiService = null
    }

}
