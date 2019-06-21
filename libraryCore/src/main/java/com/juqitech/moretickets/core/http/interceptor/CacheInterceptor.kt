package com.juqitech.moretickets.core.http.interceptor

import android.text.TextUtils

import java.io.IOException

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class CacheInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        return if (!TextUtils.isEmpty(response.header("Cache-Control"))) {
            response.newBuilder()
                    .removeHeader("Pragma")
                    .build()
        } else {
            response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    //cache for 300 秒
                    .header("Cache-Control", "max-age=" + 300)
                    .build()
        }
    }
}