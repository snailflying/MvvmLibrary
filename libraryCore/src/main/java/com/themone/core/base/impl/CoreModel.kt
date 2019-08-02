package com.themone.core.base.impl

import com.themone.core.base.IModel
import com.themone.core.http.HttpClient
import com.themone.core.util.LogUtil

/**
 * @author zhiqiang
 * @date 2019-06-04
 * @desc
 */
open class CoreModel<T>(clazz: Class<T>) : IModel {

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
