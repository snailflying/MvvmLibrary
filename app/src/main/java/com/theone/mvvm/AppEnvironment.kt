package com.theone.mvvm

import android.text.TextUtils
import com.themone.core.DefaultEnvironment
import com.theone.mvvm.base.net.api.ApiService
import com.theone.mvvm.base.net.interceptor.HeaderInterceptor
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor

/**
 * @Author zhiqiang
 * @Email liuzhiqiang@moretickets.com
 * @Date 2019-06-11
 * @Description
 */
class AppEnvironment : DefaultEnvironment() {


    companion object {
        const val FLAVOR_DEV = "dev"
        const val FLAVOR_QA = "qa"
        const val FLAVOR_ONLINE = "online"
        const val FLAVOR_GOOGLE = "google"
        val productFlavor: String
            get() = BuildConfig.PRODUCT_FLAVORS
    }

    override val apiBaseServiceUrl: String
        get() {
            return when (productFlavor) {
                FLAVOR_ONLINE, FLAVOR_GOOGLE -> ApiService.BASE_URL_ONLINE
                FLAVOR_DEV -> ApiService.BASE_URL_DEV
                FLAVOR_QA -> ApiService.BASE_URL_QA
                else -> ApiService.BASE_URL_ONLINE
            }
        }
    override val interceptors: MutableList<Interceptor>
        get() {
            val interceptors = super.interceptors
            val headerInterceptor = HeaderInterceptor()


            interceptors.add(headerInterceptor)
            if (BuildConfig.DEBUG) {
                val logging = HttpLoggingInterceptor()
                logging.level = HttpLoggingInterceptor.Level.BODY
                interceptors.add(logging)
            }

            return interceptors
        }


}
