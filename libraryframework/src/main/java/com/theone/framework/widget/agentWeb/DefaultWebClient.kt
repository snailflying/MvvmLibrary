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

import android.annotation.TargetApi
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.View
import android.webkit.*
import androidx.annotation.RequiresApi
import com.alipay.sdk.app.PayTask
import com.themone.core.util.LogUtil
import com.theone.framework.base.BaseApp
import java.lang.ref.WeakReference
import java.lang.reflect.Method
import java.net.URISyntaxException
import java.util.*

/**
 * @author cenxiaozhong
 * @since 3.0.0
 */
class DefaultWebClient internal constructor(builder: Builder) : MiddlewareWebClientBase(builder.mClient) {
    /**
     * Activity's WeakReference
     */
    private var mWeakReference: WeakReference<Context?>? = null

    /**
     * 默认为咨询用户
     */
    private var mUrlHandleWays = ASK_USER_OPEN_OTHER_PAGE

    /**
     * 是否拦截找不到相应页面的Url，默认拦截
     */
    private var mIsInterceptUnkownUrl = true

    /**
     * 弹窗回调
     */
    private var mCallback: Handler.Callback? = null

    /**
     * MainFrameErrorMethod
     */
    private val onMainFrameErrorMethod: Method? = null

    /**
     * Alipay PayTask 对象
     */
    private var mPayTask: Any? = null

    /**
     * 缓存当前出现错误的页面
     */
    private val mErrorUrlsSet: MutableSet<String?> = HashSet()

    /**
     * 缓存等待加载完成的页面 onPageStart()执行之后 ，onPageFinished()执行之前
     */
    private val mWaittingFinishSet: MutableSet<String?> = HashSet()

    private var imageUrlList: ArrayList<String?>? = null

    @RequiresApi(api = Build.VERSION_CODES.N)
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        val url = request?.url?.toString() ?: return super.shouldOverrideUrlLoading(view, request)
        if (url.contains("clickimg")) {
            try {
                toBigImageActivity(view, url)
                return true
            } catch (e: Exception) {
                LogUtil.e("Exception", e.message)
            }
        }
        if (url.startsWith(HTTP_SCHEME) || url.startsWith(HTTPS_SCHEME)) {
            return HAS_ALIPAY_LIB && isAlipay(view, url)
        }
        if (handleCommonLink(url)) {
            return true
        }
        // intent
        if (url.startsWith(INTENT_SCHEME)) {
            handleIntentUrl(url)
            LogUtil.i(TAG, "intent url ")
            return true
        }
        // 微信支付
        if (url.startsWith(WEBCHAT_PAY_SCHEME)) {
            LogUtil.i(TAG, "lookup wechat to pay ~~")
            startActivity(url)
            return true
        }
        if (url.startsWith(ALIPAYS_SCHEME) && lookup(url)) {
            LogUtil.i(TAG, "alipays url lookup alipay ~~ ")
            return true
        }
        if (queryActiviesNumber(url) > 0 && deepLink(url)) {
            LogUtil.i(TAG, "intercept url:$url")
            return true
        }

        if (mIsInterceptUnkownUrl) {
            LogUtil.i(TAG, "intercept UnkownUrl :" + request.url)
            return true
        }
        return super.shouldOverrideUrlLoading(view, request)
    }

    override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
        return super.shouldInterceptRequest(view, url)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
        return super.shouldInterceptRequest(view, request)
    }

    override fun onReceivedHttpAuthRequest(view: WebView?, handler: HttpAuthHandler?, host: String?, realm: String?) {
        super.onReceivedHttpAuthRequest(view, handler, host, realm)
    }

    private fun deepLink(url: String): Boolean {
        return when (mUrlHandleWays) {
            DERECT_OPEN_OTHER_PAGE -> {
                lookup(url)
                true
            }
            ASK_USER_OPEN_OTHER_PAGE -> {
                var context: Context? = null
                if (mWeakReference!!.get().also { context = it } == null) {
                    return false
                }
                val resolveInfo = lookupResolveInfo(url) ?: return false
                val activityInfo = resolveInfo.activityInfo
                LogUtil.e(TAG, "resolve package:" + resolveInfo.activityInfo.packageName + " app package:" + context!!.packageName)
                if (activityInfo != null && !TextUtils.isEmpty(activityInfo.packageName)
                        && activityInfo.packageName == context!!.packageName) {
                    lookup(url)
                } else true
            }
            else -> false
        }
    }


    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        if (view == null || url == null) return super.shouldOverrideUrlLoading(view, url)
        if (url.contains("clickimg")) {
            try {
                toBigImageActivity(view, url)
                return true
            } catch (e: Exception) {
                LogUtil.e("Exception", e.message)
            }
        }
        if (url.startsWith(HTTP_SCHEME) || url.startsWith(HTTPS_SCHEME)) {
            return HAS_ALIPAY_LIB && isAlipay(view, url)
        }
        //电话 ， 邮箱 ， 短信
        if (handleCommonLink(url)) {
            return true
        }
        //Intent scheme
        if (url.startsWith(INTENT_SCHEME)) {
            handleIntentUrl(url)
            return true
        }
        //微信支付
        if (url.startsWith(WEBCHAT_PAY_SCHEME)) {
            startActivity(url)
            return true
        }
        //支付宝
        if (url.startsWith(ALIPAYS_SCHEME) && lookup(url)) {
            return true
        }
        //打开url 相对应的页面
        if (queryActiviesNumber(url) > 0 && deepLink(url)) {
            LogUtil.i(TAG, "intercept OtherAppScheme")
            return true
        }
        // 手机里面没有页面能匹配到该链接 ，拦截下来。
        if (mIsInterceptUnkownUrl) {
            LogUtil.i(TAG, "intercept InterceptUnkownScheme : $url")
            return true
        }
        return super.shouldOverrideUrlLoading(view, url)
    }

    private fun queryActiviesNumber(url: String): Int {
        return try {
            if (mWeakReference!!.get() == null) {
                return 0
            }
            val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
            val mPackageManager = mWeakReference!!.get()!!.packageManager
            val mResolveInfos = mPackageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            mResolveInfos?.size ?: 0
        } catch (ignore: URISyntaxException) {
            if (LogUtil.isDebug) {
                ignore.printStackTrace()
            }
            0
        }
    }

    private fun handleIntentUrl(intentUrl: String) {
        try {
            val intent: Intent? = null
            if (TextUtils.isEmpty(intentUrl) || !intentUrl.startsWith(INTENT_SCHEME)) {
                return
            }
            if (lookup(intentUrl)) {
                return
            }
        } catch (e: Throwable) {
            if (LogUtil.isDebug) {
                e.printStackTrace()
            }
        }
    }

    private fun lookupResolveInfo(url: String): ResolveInfo? {
        try {
            val intent: Intent
            var context: Context? = null
            if (mWeakReference!!.get().also { context = it } == null) {
                return null
            }
            val packageManager = context!!.packageManager
            intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
            return packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        } catch (ignore: Throwable) {
            if (LogUtil.isDebug) {
                ignore.printStackTrace()
            }
        }
        return null
    }

    private fun lookup(url: String): Boolean {
        try {
            val intent: Intent
            var context: Context? = null
            if (mWeakReference!!.get().also { context = it } == null) {
                return true
            }
            val packageManager = context!!.packageManager
            intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
            val info = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
            // 跳到该应用
            if (info != null) {
                context!!.startActivity(intent)
                return true
            }
        } catch (ignore: Throwable) {
            if (LogUtil.isDebug) {
                ignore.printStackTrace()
            }
        }
        return false
    }

    private fun isAlipay(view: WebView?, url: String?): Boolean {
        try {
            var context: Context? = null
            if (mWeakReference!!.get().also { context = it } == null) {
                return false
            }
            /**
             * 推荐采用的新的二合一接口(payInterceptorWithUrl),只需调用一次
             */
            if (mPayTask == null) {
                val clazz = Class.forName("com.alipay.sdk.app.PayTask")
                val mConstructor = clazz.getConstructor(Activity::class.java)
                mPayTask = mConstructor.newInstance(context)
            }
            val task = mPayTask as PayTask?
            val isIntercepted = task!!.payInterceptorWithUrl(url, true) { result ->
                val url = result.returnUrl
                if (!TextUtils.isEmpty(url)) {
                    view?.post { view.loadUrl(url) }
                }
            }
            if (isIntercepted) {
                LogUtil.i(TAG, "alipay-isIntercepted:$isIntercepted  url:$url")
            }
            return isIntercepted
        } catch (ignore: Throwable) {
            if (BaseApp.isDebug()) {
                ignore.printStackTrace()
            }
        }
        return false
    }

    private fun handleCommonLink(url: String): Boolean {
        if (url.startsWith(WebView.SCHEME_TEL)
                || url.startsWith(SCHEME_SMS)
                || url.startsWith(WebView.SCHEME_MAILTO)
                || url.startsWith(WebView.SCHEME_GEO)) {
            try {
                var context: Context? = null
                if (mWeakReference!!.get().also { context = it } == null) {
                    return false
                }
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                context!!.startActivity(intent)
            } catch (ignored: ActivityNotFoundException) {
                if (BaseApp.isDebug()) {
                    ignored.printStackTrace()
                }
            }
            return true
        }
        return false
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        if (!mWaittingFinishSet.contains(url)) {
            mWaittingFinishSet.add(url)
        }
        super.onPageStarted(view, url, favicon)
    }

    /**
     * MainFrame Error
     *
     * @param view
     * @param errorCode
     * @param description
     * @param failingUrl
     */
    override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
        LogUtil.i(TAG, "onReceivedError：$description  CODE:$errorCode")
        onMainFrameError(view, errorCode, description, failingUrl)
        super.onReceivedError(view, errorCode, description, failingUrl)
    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
        if (request?.isForMainFrame == true && error?.errorCode != -1) {
            onMainFrameError(view,
                    error?.errorCode ?: 0, error?.description?.toString(),
                    request.url.toString())
        }
        super.onReceivedError(view, request, error)
        LogUtil.i(TAG, "onReceivedError:" + error?.description + " code:" + error?.errorCode)
    }

    private fun onMainFrameError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
        mErrorUrlsSet.add(failingUrl)
        // 下面逻辑判断开发者是否重写了 onMainFrameError 方法 ， 优先交给开发者处理
//        this.mWebView.setVisibility(View.GONE);
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        if (mErrorUrlsSet.contains(url) || !mWaittingFinishSet.contains(url)) {
            view?.visibility = View.VISIBLE
        }
        if (mWaittingFinishSet.contains(url)) {
            mWaittingFinishSet.remove(url)
        }
        if (!mErrorUrlsSet.isEmpty()) {
            mErrorUrlsSet.clear()
        }
        getAllImages(view)
        super.onPageFinished(view, url)
    }

    private fun getAllImages(webView: WebView?) {
        if (null == webView || Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return
        }
        webView.evaluateJavascript("javascript:getAllImageUrl()", ValueCallback { value ->
            if (TextUtils.isEmpty(value)) {
                return@ValueCallback
            }
            val imageUrls = value.substring(1, value.length - 1)
            val split = imageUrls.split(",").toTypedArray()
            if (null == imageUrlList) {
                imageUrlList = ArrayList()
            }
            imageUrlList!!.clear()
            imageUrlList!!.addAll(Arrays.asList(*split))
        })
    }

    private fun toBigImageActivity(webView: WebView?, url: String?) {
        if (null == webView || url.isNullOrBlank()) {
            return
        }
        /*val split = url.split(":").toTypedArray()
        val index = split[1]
        if (!imageUrlList.isNullOrEmpty()) {
            val bundle = Bundle()
            bundle.putInt(AppRouteUrl.ROUTE_BIG_IMAGE_SELECT, 2)
            bundle.putInt(AppRouteUrl.ROUTE_BIG_IMAGE_POSITION, Integer.valueOf(index))
            bundle.putBoolean(AppRouteUrl.ROUTE_BIG_IMAGE_IS_LOCAL, true)
            bundle.putStringArrayList(AppRouteUrl.ROUTE_BIG_IMAGE_IMG_LIST, imageUrlList)
            Router.build(AppRouteUrl.ROUTE_BIG_IMAGE_URL)
                    .with(bundle)
                    .go(webView.context)
        }*/
    }

    private fun startActivity(url: String) {
        try {
            if (mWeakReference!!.get() == null) {
                return
            }
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.data = Uri.parse(url)
            mWeakReference!!.get()!!.startActivity(intent)
        } catch (e: Exception) {
            if (LogUtil.isDebug) {
                e.printStackTrace()
            }
        }
    }

    /**
     * https错误忽略
     * @param view WebView
     * @param handler SslErrorHandler
     * @param error SslError
     */
    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
        if (BaseApp.isDebug()) {
            handler?.proceed()
        } else {
            super.onReceivedSslError(view, handler, error)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
        super.onReceivedHttpError(view, request, errorResponse)
    }

    override fun onScaleChanged(view: WebView?, oldScale: Float, newScale: Float) {
        LogUtil.i(TAG, "onScaleChanged:$oldScale   n:$newScale")
        if (newScale - oldScale > CONSTANTS_ABNORMAL_BIG) {
            view?.setInitialScale((oldScale / newScale * 100).toInt())
        }
    }

    private fun getCallback(url: String): Handler.Callback? {
        return if (mCallback != null) {
            mCallback
        } else Handler.Callback { msg ->
            when (msg.what) {
                1 -> lookup(url)
                else -> return@Callback true
            }
            true
        }.also { mCallback = it }
    }

    class Builder {
        internal var mContext: Context? = null
        internal var mClient: WebViewClient? = null
        internal var mIsInterceptUnkownScheme = false
        internal var mUrlHandleWays = 0
        fun setActivity(context: Context?): Builder {
            mContext = context
            return this
        }

        fun setClient(client: WebViewClient?): Builder {
            mClient = client
            return this
        }

        fun setInterceptUnkownUrl(interceptUnkownScheme: Boolean): Builder {
            mIsInterceptUnkownScheme = interceptUnkownScheme
            return this
        }

        fun setUrlHandleWays(urlHandleWays: Int): Builder {
            mUrlHandleWays = urlHandleWays
            return this
        }

        fun build(): DefaultWebClient {
            return DefaultWebClient(this)
        }
    }

    companion object {
        /**
         * 缩放
         */
        private const val CONSTANTS_ABNORMAL_BIG = 7

        /**
         * intent ' s scheme
         */
        const val INTENT_SCHEME = "intent://"

        /**
         * Wechat pay scheme ，用于唤醒微信支付
         */
        const val WEBCHAT_PAY_SCHEME = "weixin://wap/pay?"

        /**
         * 支付宝
         */
        const val ALIPAYS_SCHEME = "alipays://"

        /**
         * http scheme
         */
        const val HTTP_SCHEME = "http://"

        /**
         * https scheme
         */
        const val HTTPS_SCHEME = "https://"

        /**
         * true 表示当前应用内依赖了 alipay library , false  反之
         */
        private var HAS_ALIPAY_LIB = false

        /**
         * WebViewClient's tag 用于打印
         */
        private val TAG = DefaultWebClient::class.java.simpleName

        /**
         * 直接打开其他页面
         */
        const val DERECT_OPEN_OTHER_PAGE = 1001

        /**
         * 弹窗咨询用户是否前往其他页面
         */
        const val ASK_USER_OPEN_OTHER_PAGE = DERECT_OPEN_OTHER_PAGE shr 2

        /**
         * 不允许打开其他页面
         */
        const val DISALLOW_OPEN_OTHER_APP = DERECT_OPEN_OTHER_PAGE shr 4

        /**
         * SMS scheme
         */
        const val SCHEME_SMS = "sms:"
        fun createBuilder(): Builder {
            return Builder()
        }

        init {
            var tag = true
            try {
                Class.forName("com.alipay.sdk.app.PayTask")
            } catch (ignore: Throwable) {
                tag = false
            }
            HAS_ALIPAY_LIB = tag
            LogUtil.i(TAG, "HAS_ALIPAY_LIB:$HAS_ALIPAY_LIB")
        }
    }

    init {
        mWeakReference = WeakReference(builder.mContext)
        mIsInterceptUnkownUrl = builder.mIsInterceptUnkownScheme
        mUrlHandleWays = if (builder.mUrlHandleWays <= 0) {
            ASK_USER_OPEN_OTHER_PAGE
        } else {
            builder.mUrlHandleWays
        }
    }
}