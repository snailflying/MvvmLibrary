package com.shownow.shownow.base.constant

/**
 * @Author zhiqiang
 * @Date 2019-06-11
 * @Email liuzhiqiang@theone.com
 * @Description 用来定义路由器的路由地址，以及传参
 * ！！！！name和key默认以"_KEY"结尾，值需要显示以"_VALUE"结尾！！！！
 */
object RouterUrl {

    /**
     * React Native 页面
     */
    const val REACT_NATIVE_URL = "react_native"

    /**
     * React Native login 页面
     */

    const val REACT_LOGIN_NATIVE_URL = "login"

    /**
     *
     */
    const val TIP_DIALOG_URL = "tips"

    /**
     * 首页
     */
    const val HOME_URL = "home"

    /**
     * 搜索演出
     */
    const val SEARCH_URL = "show_search"

    /**
     * webview
     */
    const val WEBVIEW_URL = "webview_url"

    /**
     * 选座
     */
    const val SEAT_URL = "show_pick_seat"


    /**
     * 定位
     */
    const val LOCATION_URL = "location"

    /**
     * 登录页面拦截器
     */
    const val LOGIN_ROUTE_INTERCEPTOR = "LoginRouteInterceptor"

    /**
     * -------------------- EXTRA 、BUNDLE  start-------------------
     * ！！！！name和key默认以"_KEY"结尾，值需要显示以"_VALUE"结尾！！！！
     */

    /**
     * 进入web页面时的url
     */
    const val BUNDLE_WEB_URL = "webUrl"

    /**
     * 登录时记录 路由 url,用于登录成功后跳转
     */
    const val BUNDLE_ROUTER_URL = "router_url"

    /**
     * 登录时记录 路由 bundle 信息,用于登录成功后跳转传参
     */
    const val BUNDLE_ROUTER_EXTRA = "router_extra"

    /**
     * 热搜词
     */
    const val BUNDLE_KEYWORD = "keyword"

    /**
     * 演出id 选座、确认订单
     */
    const val BUNDLE_SHOW_ID = "showId"


    /**
     * 点击票面
     */
    const val BUNDLE_CHOSE_TICKET_INFO = "choseTicketInfo"

    /**
     * -------------------- EXTRA 、BUNDLE  end-------------------
     */

}
