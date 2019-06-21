package com.themone.core

import java.io.IOException
import java.net.InetAddress
import java.net.Socket
import java.net.UnknownHostException
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * @author sincerity
 * @desc 屏蔽 hppts 双向验证，仅在 debug 模式开启
 */
class SSLSocketFactoryImp @Throws(NoSuchAlgorithmException::class, KeyManagementException::class)
constructor(keyStore: KeyStore) : SSLSocketFactory() {

    val sslContext = SSLContext.getInstance("SSL")!!

    private val trustManager: TrustManager

    fun getTrustManager(): X509TrustManager {
        return trustManager as X509TrustManager
    }

    init {
        trustManager = object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(x509Certificates: Array<X509Certificate>, s: String) {

            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(x509Certificates: Array<X509Certificate>, s: String) {

            }

            override fun getAcceptedIssuers(): Array<X509Certificate?> {
                //注意这里不能返回null，否则会报错,如下面错误[1]
                return arrayOfNulls(0)
            }
        }

        sslContext.init(null, arrayOf<TrustManager>(trustManager), null)
    }

    override fun getDefaultCipherSuites(): Array<String?> {
        return arrayOfNulls(0)
    }

    override fun getSupportedCipherSuites(): Array<String?> {
        return arrayOfNulls(0)
    }

    @Throws(IOException::class)
    override fun createSocket(): Socket {
        return sslContext.socketFactory.createSocket()
    }

    @Throws(IOException::class)
    override fun createSocket(socket: Socket, host: String, post: Int, autoClose: Boolean): Socket {
        return sslContext.socketFactory.createSocket(socket, host, post, autoClose)
    }

    @Throws(IOException::class, UnknownHostException::class)
    override fun createSocket(s: String, i: Int): Socket? {
        return null
    }

    @Throws(IOException::class, UnknownHostException::class)
    override fun createSocket(s: String, i: Int, inetAddress: InetAddress, i1: Int): Socket? {
        return null
    }

    @Throws(IOException::class)
    override fun createSocket(inetAddress: InetAddress, i: Int): Socket? {
        return null
    }

    @Throws(IOException::class)
    override fun createSocket(inetAddress: InetAddress, i: Int, inetAddress1: InetAddress, i1: Int): Socket? {
        return null
    }
}