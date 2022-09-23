package com.theone.framework.base

import com.theone.framework.http.HttpClient

/**
 * @Author zhiqiang
 * @Date 2019-08-02
 * @Description
 */
open class BaseRepository<T>(clazz: Class<T>) : IBaseRepository {
    /**
     * apiService
     * 用于 retrofit 请求网络
     */
    protected val apiService: T = HttpClient.create(clazz)

    override fun onDestroy() {

    }

}