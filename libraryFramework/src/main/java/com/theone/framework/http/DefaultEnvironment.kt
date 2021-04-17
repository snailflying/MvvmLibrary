package com.theone.framework.http

import com.themone.core.util.LogUtil
import com.theone.framework.base.BaseApp
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyStore
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author zhiqiang
 * @date 2019-06-05
 * @desc
 */
open class DefaultEnvironment : IAppEnvironment {

    override val retrofit: Retrofit
        get() = Retrofit.Builder()
            .baseUrl(apiBaseServiceUrl)
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .addCallAdapterFactory(callAdapterFactory)
            .build()

    override val okHttpClient: OkHttpClient
        get() {
            val builder = OkHttpClient.Builder()
            builder.connectTimeout(DEFAULT_TIME_OUT.toLong(), TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIME_OUT.toLong(), TimeUnit.SECONDS)
                .readTimeout(DEFAULT_READ_TIME_OUT.toLong(), TimeUnit.SECONDS)
                .cache(cache)

            for (interceptor in interceptors) {
                builder.addInterceptor(interceptor)
            }
            for (interceptor in networkInterceptors) {
                builder.addNetworkInterceptor(interceptor)
            }

            if (LogUtil.isDebug) {
                var ssl: SSLSocketFactoryImp? = null
                try {
                    ssl = SSLSocketFactoryImp(KeyStore.getInstance(KeyStore.getDefaultType()))
                } catch (e: Exception) {
                    LogUtil.d("SSLSocketFactory", "ssl:" + e.message)
                }

                if (null != ssl) {
                    builder.sslSocketFactory(ssl.sslContext.socketFactory, ssl.getTrustManager())
                }
            }

            return builder.build()
        }


    override val apiBaseServiceUrl: String
        get() = "http://www.theone.com"

    override val converterFactory: Converter.Factory
        get() = GsonConverterFactory.create()

    override val callAdapterFactory: CallAdapter.Factory
        get() = RxJava3CallAdapterFactory.create()

    override val interceptors: MutableList<Interceptor>
        get() {
            val interceptors = ArrayList<Interceptor>()
            return interceptors
        }

    override val networkInterceptors: MutableList<Interceptor>
        get() {
            val interceptors = ArrayList<Interceptor>()
           /* if (LogUtil.isDebug) {
                val logging = HttpLoggingInterceptor()
                logging.level = HttpLoggingInterceptor.Level.BODY
                interceptors.add(logging)
            }*/
            return interceptors
        }

    override val cache: Cache
        get() = Cache(BaseApp.application.cacheDir, (10240 * 1024).toLong())

    companion object {
        /**
         * 连接超时时间
         */
        private const val DEFAULT_TIME_OUT = 10

        /**
         * 读操作超时时间
         */
        private const val DEFAULT_READ_TIME_OUT = 5
    }


}
