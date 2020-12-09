package com.theone.framework.router

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import com.chenenyu.router.RouteRequest
import com.chenenyu.router.matcher.AbsExplicitMatcher

/**
 * @Author zhiqiang
 * @Email liuzhiqiang@piaoyou.com
 * @Date 2019-03-29
 * @Description 自定义router匹配规则:根据path路径匹配(path为空时根据host匹配)
 * 优先级介于[com.chenenyu.router.matcher.ImplicitMatcher]
 * 和[com.chenenyu.router.matcher.SchemeMatcher]之间
 * 1.支持webview打开http|https协议网页
 * 2.支持其他scheme协议的router
 */
class AppMatcher : AbsExplicitMatcher {
    private var webviewRoute = AppRouteUrl.ROUTE_WEB_URL
    private var webDataUrl = AppRouteUrl.WEB_DATA_URL

    //    scheme = "(https|http|piaoyou|native)://";
//    host = "(\\w+\\.)?piaoyou\\.com";
    constructor() : super(0x0011) {}

    /**
     * 优先级高于[com.chenenyu.router.matcher.BrowserMatcher]的0x0000
     *
     * @param webviewRoute webview的路径
     */
    constructor(webviewRoute: String, webDataUrl: String) : super(0x0011) {
        this.webviewRoute = webviewRoute
        this.webDataUrl = webDataUrl
    }

    /**
     * @param context      前后文
     * @param uri          匹配项
     * @param route        被匹配项
     * @param routeRequest 参数
     * @return 是否匹配
     */
    override fun match(context: Context, uri: Uri?, route: String?, routeRequest: RouteRequest): Boolean {
        if (uri == null || isEmpty(route)) {
            return false
        }
        // scheme != null
        return if (uri.isAbsolute) {
            if (matchHttp(uri, route, routeRequest)) {
                true
            } else {
                matchNative(uri, route, routeRequest)
            }
        } else {
            false
        }
    }

    /**
     * @param uri          匹配项
     * @param route        被匹配项
     * @param routeRequest 参数
     * @return 是否匹配Http跳转Webview
     */
    private fun matchHttp(uri: Uri, route: String?, routeRequest: RouteRequest): Boolean {
        if (uri.scheme == null) {
            return false
        }
        return if (webviewRoute == route && uri.scheme!!.matches(Regex("https?"))) {
            parseHttpParams(uri, routeRequest)
            true
        } else {
            false
        }
    }

    /**
     * @param uri          匹配项
     * @param route        被匹配项
     * @param routeRequest 参数
     * @return 是否匹配原生APP页面跳转
     */
     fun matchNative(uri: Uri, route: String?, routeRequest: RouteRequest): Boolean {
        return if (TextUtils.isEmpty(uri.path)) { //path为空时通过host判断
            if (cutSlash(uri.host) == cutSlash(route)) {
                parseParams(uri, routeRequest)
                true
            } else {
                false
            }
        } else {
            if (cutSlash(uri.path) == cutSlash(route)) {
                parseParams(uri, routeRequest)
                true
            } else {
                false
            }
        }
    }

    /**
     * http增加uri传递
     *
     * @param uri，http的url
     * @param routeRequest 传递的参数
     */
    private fun parseHttpParams(uri: Uri, routeRequest: RouteRequest) {
        var bundle = routeRequest.extras
        if (bundle == null) {
            bundle = Bundle()
            routeRequest.extras = bundle
        }
        //过滤所有的空格
        bundle.putString(webDataUrl, uri.toString().replace(" ", ""))
        parseParams(uri, routeRequest)
    }

    /**
     * 剔除path头部和尾部的斜杠/
     *
     * @param path 路径
     * @return 无/的路径
     */
    private fun cutSlash(path: String?): String {
        if (path == null) {
            return ""
        }
        if (path.startsWith("/")) {
            return cutSlash(path.substring(1))
        }
        return if (path.endsWith("/")) {
            cutSlash(path.substring(0, path.length - 1))
        } else path
    }
}