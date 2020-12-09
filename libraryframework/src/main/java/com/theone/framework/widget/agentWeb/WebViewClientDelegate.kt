/*
 * Copyright (C)  Justson(https://github.com/Justson/AgentWeb)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.theone.framework.widget.agentWeb

import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.os.Message
import android.view.KeyEvent
import android.webkit.*
import androidx.annotation.RequiresApi

/**
 * @Author ZhiQiang
 * @Date 2020/11/15
 * @Description
 */
open class WebViewClientDelegate internal constructor(private var mDelegate: WebViewClient? = null) : WebViewClient() {

    open fun setDelegate(delegate: WebViewClient) {
        this.mDelegate = delegate
    }

    @Deprecated("")
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        return if (mDelegate != null) {
            mDelegate!!.shouldOverrideUrlLoading(view, url)
        } else super.shouldOverrideUrlLoading(view, url)
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        return if (mDelegate != null) {
            mDelegate!!.shouldOverrideUrlLoading(view, request)
        } else super.shouldOverrideUrlLoading(view, request)
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        if (mDelegate != null) {
            mDelegate!!.onPageStarted(view, url, favicon)
            return
        }
        super.onPageStarted(view, url, favicon)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        if (mDelegate != null) {
            mDelegate!!.onPageFinished(view, url)
            return
        }
        super.onPageFinished(view, url)
    }

    override fun onLoadResource(view: WebView?, url: String?) {
        if (mDelegate != null) {
            mDelegate!!.onLoadResource(view, url)
            return
        }
        super.onLoadResource(view, url)
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onPageCommitVisible(view: WebView?, url: String?) {
        if (mDelegate != null) {
            mDelegate!!.onPageCommitVisible(view, url)
            return
        }
        super.onPageCommitVisible(view, url)
    }

    @Deprecated("")
    override fun shouldInterceptRequest(view: WebView?,
                                        url: String?): WebResourceResponse? {
        return if (mDelegate != null) {
            mDelegate!!.shouldInterceptRequest(view, url)
        } else {
            super.shouldInterceptRequest(view, url)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun shouldInterceptRequest(view: WebView?,
                                        request: WebResourceRequest?): WebResourceResponse? {
        return if (mDelegate != null) {
            mDelegate!!.shouldInterceptRequest(view, request)
        } else {
            super.shouldInterceptRequest(view, request)
        }
    }

    @Deprecated("")
    override fun onTooManyRedirects(view: WebView?, cancelMsg: Message?,
                                    continueMsg: Message?) {
        if (mDelegate != null) {
            mDelegate!!.onTooManyRedirects(view, cancelMsg, continueMsg)
            return
        }
        super.onTooManyRedirects(view, cancelMsg, continueMsg)
    }

    @Deprecated("")
    override fun onReceivedError(view: WebView?, errorCode: Int,
                                 description: String?, failingUrl: String?) {
        if (mDelegate != null) {
            mDelegate!!.onReceivedError(view, errorCode, description, failingUrl)
            return
        }
        super.onReceivedError(view, errorCode, description, failingUrl)
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
        if (mDelegate != null) {
            mDelegate!!.onReceivedError(view, request, error)
            return
        }
        super.onReceivedError(view, request, error)
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onReceivedHttpError(
            view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
        if (mDelegate != null) {
            mDelegate!!.onReceivedHttpError(view, request, errorResponse)
            return
        }
        super.onReceivedHttpError(view, request, errorResponse)
    }

    override fun onFormResubmission(view: WebView?, dontResend: Message?,
                                    resend: Message?) {
        if (mDelegate != null) {
            mDelegate!!.onFormResubmission(view, dontResend, resend)
            return
        }
        super.onFormResubmission(view, dontResend, resend)
    }

    override fun doUpdateVisitedHistory(view: WebView?, url: String?,
                                        isReload: Boolean) {
        if (mDelegate != null) {
            mDelegate!!.doUpdateVisitedHistory(view, url, isReload)
            return
        }
        super.doUpdateVisitedHistory(view, url, isReload)
    }

    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?,
                                    error: SslError?) {
        if (mDelegate != null) {
            mDelegate!!.onReceivedSslError(view, handler, error)
            return
        }
        super.onReceivedSslError(view, handler, error)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onReceivedClientCertRequest(view: WebView?, request: ClientCertRequest?) {
        if (mDelegate != null) {
            mDelegate!!.onReceivedClientCertRequest(view, request)
            return
        }
        super.onReceivedClientCertRequest(view, request)
    }

    override fun onReceivedHttpAuthRequest(view: WebView?,
                                           handler: HttpAuthHandler?, host: String?, realm: String?) {
        if (mDelegate != null) {
            mDelegate!!.onReceivedHttpAuthRequest(view, handler, host, realm)
            return
        }
        super.onReceivedHttpAuthRequest(view, handler, host, realm)
    }

    override fun shouldOverrideKeyEvent(view: WebView?, event: KeyEvent?): Boolean {
        return if (mDelegate != null) {
            mDelegate!!.shouldOverrideKeyEvent(view, event)
        } else super.shouldOverrideKeyEvent(view, event)
    }

    override fun onUnhandledKeyEvent(view: WebView?, event: KeyEvent?) {
        if (mDelegate != null) {
            mDelegate!!.onUnhandledKeyEvent(view, event)
            return
        }
        super.onUnhandledKeyEvent(view, event)
    }

    override fun onScaleChanged(view: WebView?, oldScale: Float, newScale: Float) {
        if (mDelegate != null) {
            mDelegate!!.onScaleChanged(view, oldScale, newScale)
            return
        }
        super.onScaleChanged(view, oldScale, newScale)
    }

    override fun onReceivedLoginRequest(view: WebView?, realm: String?,
                                        account: String?, args: String?) {
        if (mDelegate != null) {
            mDelegate!!.onReceivedLoginRequest(view, realm, account, args)
            return
        }
        super.onReceivedLoginRequest(view, realm, account, args)
    }

    companion object {
        private val TAG = WebViewClientDelegate::class.java.simpleName
    }

}