package com.theone.framework.router

import android.content.Context
import android.os.Bundle
import androidx.annotation.AnimRes
import androidx.fragment.app.Fragment
import com.chenenyu.router.IRouter
import com.chenenyu.router.RouteCallback
import com.chenenyu.router.Router

/**
 * @Author zhiqiang
 * @Date 2020/6/13
 * 详见[IRouter]
 * @Description Router路由封装
 */
class AppRouterBuilder(uri: String?) {
    private val router: IRouter? = Router.build(uri)

    fun with(key: String, value: Any?): AppRouterBuilder {
        router?.with(key, value)
        return this
    }

    fun with(bundle: Bundle): AppRouterBuilder {
        router?.with(bundle)
        return this
    }

    fun go(context: Context?, callback: RouteCallback?) {
        router?.go(context, callback)
    }

    fun anim(@AnimRes enterAnim: Int, @AnimRes exitAnim: Int): AppRouterBuilder {
        router?.anim(enterAnim, exitAnim)
        return this
    }

    fun go(context: Context?) {
        router?.go(context)
    }

    fun addFlags(flags: Int): AppRouterBuilder {
        router?.addFlags(flags);
        return this;
    }

    fun requestCode(requestCode: Int): AppRouterBuilder {
        router?.requestCode(requestCode);
        return this;
    }

    fun go(fragment: Fragment?, callback: RouteCallback?) {
        router?.go(fragment, callback)
    }

    fun go(fragment: Fragment?) {
        router?.go(fragment)
    }

    fun getFragment(source: Any): Fragment? {
        return router?.getFragment(source)
    }

}