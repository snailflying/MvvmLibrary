package com.theone.framework.widget.agentWeb

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.theone.framework.util.DeviceUtil

/**
 * @Author ZhiQiang
 * @Date 2020/11/15
 * @Description
 */
object WebViewSetting {

    @SuppressLint("JavascriptInterface", "SetJavaScriptEnabled")
    fun initWebSettings(
        webView: WebView?,
        cachePath: String? = null,
        middleWebClient: MiddlewareWebClientBase? = null
    ) {
        webView ?: return
        webView.webViewClient = generaWebViewClient(webView.context, middleWebClient)
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls = false
        webSettings.savePassword = false
        if (DeviceUtil.isNetworkAvailable(webView.context)) { //根据cache-control获取数据。
            webSettings.cacheMode = WebSettings.LOAD_DEFAULT
        } else { //没网，则从本地获取，即离线加载
            webSettings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        }
        //针对不同机型设置是否硬件加速还是软件加速，防止选座模块加载不出来
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //适配5.0不允许http和https混合使用情况
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }
        webSettings.textZoom = 100
        webSettings.databaseEnabled = true
        webSettings.setAppCacheEnabled(true)
        webSettings.loadsImagesAutomatically = true
        webSettings.setSupportMultipleWindows(false)
        // 是否阻塞加载网络图片  协议http or https
        webSettings.blockNetworkImage = false
        // 允许加载本地文件html  file协议
        webSettings.allowFileAccess = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) { // 通过 file url 加载的 Javascript 读取其他的本地文件 .建议关闭
            webSettings.allowFileAccessFromFileURLs = false
            // 允许通过 file url 加载的 Javascript 可以访问其他的源，包括其他的文件和 http，https 等其他的源
            webSettings.allowUniversalAccessFromFileURLs = false
        }
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        } else {
            webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
        }
        webSettings.loadWithOverviewMode = false
        webSettings.useWideViewPort = false
        webSettings.domStorageEnabled = true
        webSettings.setNeedInitialFocus(true)
        webSettings.defaultTextEncodingName = "UTF-8" //设置编码格式
        webSettings.defaultFontSize = 16
        webSettings.minimumFontSize = 8 //设置 WebView 支持的最小字体大小，默认为 8
        webSettings.setGeolocationEnabled(true)
        if (TextUtils.isEmpty(cachePath)) { //设置数据库路径  api19 已经废弃,这里只针对 webkit 起作用
            webSettings.setGeolocationDatabasePath(cachePath)
            webSettings.databasePath = cachePath
            webSettings.setAppCachePath(cachePath)
        }
        //缓存文件最大值
        webSettings.setAppCacheMaxSize(1024 * 1024 * 8.toLong())
    }
    private fun generaWebViewClient(context: Context?, header: MiddlewareWebClientBase?): WebViewClient {
        val mDefaultWebClient: DefaultWebClient = DefaultWebClient
            .createBuilder()
            .setActivity(context)
            .setInterceptUnkownUrl(true)
            .setUrlHandleWays(-1)
            .build()
        return if (header != null) {
            var tail = header
            var count = 1
            var tmp = header
            while (tmp!!.next() != null) {
                tmp = tmp.next()
                tail = tmp
                count++
            }
            tail?.setDelegate(mDefaultWebClient)
            header
        } else {
            mDefaultWebClient
        }
    }
}