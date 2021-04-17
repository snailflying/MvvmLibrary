package com.theone.framework.base

import com.themone.core.base.impl.CoreModel
import com.theone.framework.http.HttpClient

/**
 * @Author zhiqiang
 * @Date 2019-08-02

 * @Description
 */
open class BaseModel<T>(clazz: Class<T>) : CoreModel<T>() {
    /**
     * apiService
     * 用于 retrofit 请求网络
     */
    protected val apiService: T = HttpClient.create(clazz)
}