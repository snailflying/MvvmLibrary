package com.theone.framework.http.interceptor

import android.annotation.SuppressLint
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession

/**
 * @Author ZhiQiang
 * @Date 2020/10/1
 * @Description IP直连忽略host检测
 */
class TrueHostVerifier() : HostnameVerifier {

    @SuppressLint("BadHostnameVerifier")
    override fun verify(hostname: String, session: SSLSession): Boolean {
        return true
    }
}