package com.juqitech.moretickets.core.http

import okhttp3.OkHttpClient
import retrofit2.Retrofit

/**
 * @author zhanfeng
 * @date 2019-06-04
 * @desc
 */
object HttpClient {

    val retrofit: Retrofit
        get() = RetrofitFactory.instance.retrofit

    val okHttpClient: OkHttpClient
        get() = RetrofitFactory.instance.okHttpClient

    fun <T> create(clazz: Class<T>): T {
        return retrofit.create(clazz)
    }

}
