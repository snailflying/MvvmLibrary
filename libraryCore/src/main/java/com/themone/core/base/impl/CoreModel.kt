package com.themone.core.base.impl

import com.themone.core.base.IModel
import com.themone.core.http.HttpClient

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
    protected val apiService: T

    init {
        this.apiService = HttpClient.create(clazz)
    }

    override fun onDestroy() {
    }

}

