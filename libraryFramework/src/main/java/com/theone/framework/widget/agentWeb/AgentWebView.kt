package com.theone.framework.widget.agentWeb

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.util.Pair
import android.view.ViewGroup
import android.view.accessibility.AccessibilityManager
import android.webkit.WebView
import android.widget.Toast
import java.net.URI
import java.net.URLEncoder
import java.util.*

/**
 * @Author ZhiQiang
 * @Date 2020/11/15
 * @Description
 */
open class AgentWebView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null) :
    WebView(context, attrs) {
    private var mOnScrollChangeListener: OnScrollListener? = null

    private var mJsCallJavas: MutableMap<String, JsCallJava>? = null
    private var mInjectJavaScripts: MutableMap<String, String>? = null
    private val mIsInited: Boolean
    private var mIsAccessibilityEnabledOriginal: Boolean? = null


    /**
     * 经过大量的测试，按照以下方式才能保证JS脚本100%注入成功：
     * 1、在第一次loadUrl之前注入JS（在addJavascriptInterface里面注入即可，setWebViewClient和setWebChromeClient要在addJavascriptInterface之前执行）；
     * 2、在webViewClient.onPageStarted中都注入JS；
     * 3、在webChromeClient.onProgressChanged中都注入JS，并且不能通过自检查（onJsPrompt里面判断）JS是否注入成功来减少注入JS的次数，因为网页中的JS可以同时打开多个url导致无法控制检查的准确性；
     *
     *
     * Android 4.2.2及以上版本的 addJavascriptInterface 方法已经解决了安全问题，如果不使用“网页能将JS函数传到Java层”功能，不建议使用该类，毕竟系统的JS注入效率才是最高的；
     */
    @SuppressLint("JavascriptInterface", "AddJavascriptInterface")
    override fun addJavascriptInterface(interfaceObj: Any?, interfaceName: String) {
        interfaceObj ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            super.addJavascriptInterface(interfaceObj, interfaceName)
            return
        }
        if (mJsCallJavas == null) {
            mJsCallJavas = HashMap()
        }
        mJsCallJavas!![interfaceName] = JsCallJava(interfaceObj, interfaceName)
        injectJavaScript()
        addJavascriptInterfaceSupport(interfaceObj, interfaceName)
    }

    protected fun addJavascriptInterfaceSupport(interfaceObj: Any?, interfaceName: String?) {
        settings.javaScriptEnabled = true
    }

    override fun destroy() {
        if (mJsCallJavas != null) {
            mJsCallJavas!!.clear()
        }
        if (mInjectJavaScripts != null) {
            mInjectJavaScripts!!.clear()
        }
        removeAllViewsInLayout()
        fixedStillAttached()
        releaseConfigCallback()
        //清除webview本地缓存
        clearCache(true)
        if (mIsInited) {
            resetAccessibilityEnabled()
            super.destroy()
        }
    }

    override fun clearHistory() {
        if (mIsInited) {
            super.clearHistory()
        }
    }

    override fun setOverScrollMode(mode: Int) {
        try {
            super.setOverScrollMode(mode)
        } catch (e: Throwable) {
            val pair = isWebViewPackageException(e)
            if (pair.first) {
                Toast.makeText(context, pair.second, Toast.LENGTH_SHORT).show()
                destroy()
            } else {
                throw e
            }
        }
    }

    override fun isPrivateBrowsingEnabled(): Boolean {
        return if (Build.VERSION.SDK_INT == Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
            && settings == null
        ) {
            false // getSettings().isPrivateBrowsingEnabled()
        } else {
            super.isPrivateBrowsingEnabled()
        }
    }

    /**
     * 添加并注入JavaScript脚本（和“addJavascriptInterface”注入对象的注入时机一致，100%能注入成功）；
     * 注意：为了做到能100%注入，需要在注入的js中自行判断对象是否已经存在（如：if (typeof(window.Android) = 'undefined')）；
     *
     * @param javaScript
     */
    fun addInjectJavaScript(javaScript: String) {
        if (mInjectJavaScripts == null) {
            mInjectJavaScripts = HashMap()
        }
        mInjectJavaScripts!![javaScript.hashCode().toString()] = javaScript
        injectExtraJavaScript()
    }

    private fun injectJavaScript() {
        for ((key, value) in mJsCallJavas!!) {
            this.loadUrl(buildNotRepeatInjectJS(key, value.preloadInterfaceJs))
        }
    }

    private fun injectExtraJavaScript() {
        for ((key, value) in mInjectJavaScripts!!) {
            this.loadUrl(buildNotRepeatInjectJS(key, value))
        }
    }

    /**
     * 构建一个“不会重复注入”的js脚本；
     *
     * @param key
     * @param js
     * @return
     */
    fun buildNotRepeatInjectJS(key: String?, js: String?): String {
        val obj = String.format("__injectFlag_%1\$s__", key)
        val sb = StringBuilder()
        sb.append("javascript:try{(function(){if(window.")
        sb.append(obj)
        sb.append("){console.log('")
        sb.append(obj)
        sb.append(" has been injected');return;}window.")
        sb.append(obj)
        sb.append("=true;")
        sb.append(js)
        sb.append("}())}catch(e){console.warn(e)}")
        return sb.toString()
    }

    /**
     * 构建一个“带try catch”的js脚本；
     *
     * @param js
     * @return
     */
    fun buildTryCatchInjectJS(js: String?): String {
        val sb = StringBuilder()
        sb.append("javascript:try{")
        sb.append(js)
        sb.append("}catch(e){console.warn(e)}")
        return sb.toString()
    }

    private fun fixedStillAttached() {
        val parent = parent
        if (parent is ViewGroup) {
            val mWebViewContainer = getParent() as ViewGroup
            mWebViewContainer.removeAllViewsInLayout()
        }
    }

    // 解决WebView内存泄漏问题；
    private fun releaseConfigCallback() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            try {
                var field = WebView::class.java.getDeclaredField("mWebViewCore")
                field = field.type.getDeclaredField("mBrowserFrame")
                field = field.type.getDeclaredField("sConfigCallback")
                field.isAccessible = true
                field[null] = null
            } catch (ignored: NoSuchFieldException) {
            } catch (ignored: IllegalAccessException) {
            }
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            try {
                val sConfigCallback = Class.forName("android.webkit.BrowserFrame").getDeclaredField("sConfigCallback")
                sConfigCallback.isAccessible = true
                sConfigCallback[null] = null
            } catch (ignored: NoSuchFieldException) {
            } catch (ignored: ClassNotFoundException) {
            } catch (ignored: IllegalAccessException) {
            }
        }
    }

    @TargetApi(11)
    protected fun removeSearchBoxJavaBridge(): Boolean {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1
            ) {
                val method = this.javaClass.getMethod("removeJavascriptInterface", String::class.java)
                method.invoke(this, "searchBoxJavaBridge_")
                return true
            }
        } catch (ignored: Exception) {
        }
        return false
    }

    protected fun fixedAccessibilityInjectorException() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR1 && mIsAccessibilityEnabledOriginal == null && isAccessibilityEnabled) {
            mIsAccessibilityEnabledOriginal = true
            isAccessibilityEnabled = false
        }
    }

    protected fun fixedAccessibilityInjectorExceptionForOnPageFinished(url: String?) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN && settings.javaScriptEnabled
            && mIsAccessibilityEnabledOriginal == null && isAccessibilityEnabled
        ) {
            try {
                try {
                    URLEncoder.encode(URI(url).toString(), "UTF-8")
                    //                    URLEncodedUtils.parse(new URI(url), null); // AccessibilityInjector.getAxsUrlParameterValue
                } catch (e: IllegalArgumentException) {
                    if ("bad parameter" == e.message) {
                        mIsAccessibilityEnabledOriginal = true
                        isAccessibilityEnabled = false
                    }
                }
            } catch (ignored: Throwable) {
            }
        }
    }

    private var isAccessibilityEnabled: Boolean
        private get() {
            val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
            return am.isEnabled
        }
        private set(enabled) {
            val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
            try {
                val setAccessibilityState =
                    am.javaClass.getDeclaredMethod("setAccessibilityState", Boolean::class.javaPrimitiveType)
                setAccessibilityState.isAccessible = true
                setAccessibilityState.invoke(am, enabled)
                setAccessibilityState.isAccessible = false
            } catch (ignored: Throwable) {
            }
        }

    private fun resetAccessibilityEnabled() {
        if (mIsAccessibilityEnabledOriginal != null) {
            isAccessibilityEnabled = mIsAccessibilityEnabledOriginal!!
        }
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        mOnScrollChangeListener?.onScroll(l - oldl, t - oldt)
        //处于顶端
        if (scrollY == 0) {
            mOnScrollChangeListener?.onPageTop(l, t, oldl, oldt)
        }
    }

    /**
     * 设置滚动监听
     * @param listener OnScrollListener
     */
    fun setOnScrollListener(listener: OnScrollListener) {
        this.mOnScrollChangeListener = listener
    }


    abstract class OnScrollListener {
        open fun onScroll(dx: Int, dy: Int) {}

        //处于顶端
        open fun onPageTop(l: Int, t: Int, oldl: Int, oldt: Int) {}
    }

    companion object {
        private val TAG = AgentWebView::class.java.simpleName
        fun isWebViewPackageException(e: Throwable): Pair<Boolean, String> {
            val messageCause = if (e.cause == null) e.toString() else e.cause.toString()
            val trace = Log.getStackTraceString(e)
            return if (trace.contains("android.content.pm.PackageManager\$NameNotFoundException")
                || trace.contains("java.lang.RuntimeException: Cannot load WebView")
                || trace.contains("android.webkit.WebViewFactory\$MissingWebViewPackageException: Failed to load WebView provider: No WebView installed")
            ) {
                Pair(true, "WebView load failed, $messageCause")
            } else Pair(false, messageCause)
        }


    }

    init {
        removeSearchBoxJavaBridge()
        mIsInited = true

        WebViewSetting.initWebSettings(this)
    }
}