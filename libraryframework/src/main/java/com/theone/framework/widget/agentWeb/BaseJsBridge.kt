package com.theone.framework.widget.agentWeb

import android.os.Build
import android.text.TextUtils
import android.webkit.WebView
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.ref.WeakReference

/**
 * @Author ZhiQiang
 * @Date 2020/11/15
 * @Description
 */
abstract class BaseJsBridge(webView: WebView?) {
    private val reference: WeakReference<WebView?> = WeakReference(webView)

    protected fun quickCallJs(method: String?, vararg params: String? = arrayOf()) {
        if (!isAvailable) {
            return
        }
        val webView = reference.get()
        val sb = StringBuilder()
        sb.append("javascript:").append(method)
        if (null == params || params.size == 0) {
            sb.append("()")
        } else {
            sb.append("(").append(concat(*params)).append(")")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView!!.evaluateJavascript(sb.toString(), null)
        } else {
            webView!!.loadUrl(sb.toString())
        }
    }

    protected val isAvailable: Boolean
        protected get() = null != reference.get()

    private fun concat(vararg params: String?): String {
        val mStringBuilder = StringBuilder()
        for (i in 0 until params.size) {
            val param = params[i]
            if (!isJson(param)) {
                mStringBuilder.append("\"").append(param).append("\"")
            } else {
                mStringBuilder.append(param)
            }
            if (i != params.size - 1) {
                mStringBuilder.append(" , ")
            }
        }
        return mStringBuilder.toString()
    }

    companion object {
        private fun isJson(target: String?): Boolean {
            if (TextUtils.isEmpty(target)) {
                return false
            }
            var tag = false
            tag = try {
                if (target!!.startsWith("[")) {
                    JSONArray(target)
                } else {
                    JSONObject(target)
                }
                true
            } catch (ignore: JSONException) {
                false
            }
            return tag
        }
    }

}