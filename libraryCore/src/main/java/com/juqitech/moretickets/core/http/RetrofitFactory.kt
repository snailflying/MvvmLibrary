package com.juqitech.moretickets.core.http

import com.juqitech.moretickets.core.EnvironmentManager
import com.juqitech.moretickets.core.IAppEnvironment

import okhttp3.OkHttpClient
import retrofit2.Retrofit

/**
 * @author zhanfeng
 * @date 2019-06-04
 * @desc RetrofitFactory
 * 非 Public 类型，不对外公开，必须通过 HttpClient 获取
 */
internal class RetrofitFactory private constructor() {

    private var mOkHttpClient: OkHttpClient? = null
    private var mRetrofit: Retrofit? = null
    private val appEnvironment: IAppEnvironment = EnvironmentManager.environment

    val retrofit: Retrofit
        get() {
            if (null == mRetrofit) {
                mRetrofit = appEnvironment.retrofit
            }
            return mRetrofit!!
        }

    val okHttpClient: OkHttpClient
        get() = provideOkHttpClient()

    private object RetrofitManagerHelper {
        val INSTANCE = RetrofitFactory()
    }

    private fun provideOkHttpClient(): OkHttpClient {
        if (null == mOkHttpClient) {
            mOkHttpClient = appEnvironment.okHttpClient
        }
        return mOkHttpClient!!
    }

    companion object {

        val instance: RetrofitFactory
            get() = RetrofitManagerHelper.INSTANCE
    }


}
