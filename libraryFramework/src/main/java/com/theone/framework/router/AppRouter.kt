package com.theone.framework.router

/**
 * @Author ZhiQiang
 * @Date 2020/6/18
 * @Description Router跳转封装
 */
object AppRouter {
    fun build(uri: String?): AppRouterBuilder {
        return AppRouterBuilder(uri)
    }

}