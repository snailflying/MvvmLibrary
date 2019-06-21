package com.themone.core;

import androidx.annotation.NonNull;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

import java.util.List;

/**
 * @author zhiqiang
 * @date 2019-06-05
 * @desc 可选择不重载 {@link #getRetrofit()}和{@link #getOkHttpClient()}
 *
 */
public interface IAppEnvironment {

    /**
     * 配置 Retrofit，配合 {@link #getOkHttpClient()}
     *
     * @return
     */
    @NonNull
    Retrofit getRetrofit();

    /**
     * 配置OkHttp
     *
     * @return
     */
    @NonNull
    OkHttpClient getOkHttpClient();

    /**
     * app 默认域名,重载 {@link #getRetrofit()}后不起作用
     *
     * @return
     */
    @NonNull
    String getApiBaseServiceUrl();

    /**
     * 普通 拦截器,重载 {@link #getOkHttpClient()}后不起作用
     *
     * @return
     */
    @NonNull
    List<Interceptor> getInterceptors();

    /**
     * 网络 拦截器,重载 {@link #getOkHttpClient()}后不起作用
     *
     * @return
     */
    @NonNull
    List<Interceptor> getNetworkInterceptors();

    /**
     * cache,重载 {@link #getOkHttpClient()}后不起作用
     *
     * @return
     */
    @NonNull
    Cache getCache();
}
