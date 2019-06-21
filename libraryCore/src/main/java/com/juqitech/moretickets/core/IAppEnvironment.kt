package com.juqitech.moretickets.core

import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit

/**
 * @author zhanfeng
 * @date 2019-06-05
 * @desc 可选择不重载 [.getRetrofit]和[.getOkHttpClient]
 */
interface IAppEnvironment {

    /**
     * 配置 Retrofit，配合 [.getOkHttpClient]
     *
     * @return
     */
    val retrofit: Retrofit

    /**
     * 配置OkHttp
     *
     * @return
     */
    val okHttpClient: OkHttpClient

    /**
     * app 默认域名,重载 [.getRetrofit]后不起作用
     *
     * @return
     */
    val apiBaseServiceUrl: String

    /**
     * 提供Gson Convert,重载 [.getRetrofit]后不起作用
     *
     * @return
     */
    val converterFactory: Converter.Factory

    /**
     * 提供CallAdapter,重载 [.getRetrofit]后不起作用
     *
     * @return
     */
    val callAdapterFactory: CallAdapter.Factory


    /**
     * 普通 拦截器,重载 [.getOkHttpClient]后不起作用
     *
     * @return
     */
    val interceptors: List<Interceptor>

    /**
     * 网络 拦截器,重载 [.getOkHttpClient]后不起作用
     *
     * @return
     */
    val networkInterceptors: List<Interceptor>

    /**
     * cache,重载 [.getOkHttpClient]后不起作用
     *
     * @return
     */
    val cache: Cache
}
