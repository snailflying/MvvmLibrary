package com.theone.framework.image

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.theone.framework.http.HttpClient
import java.io.InputStream


/**
 * @Author zhiqiang
 * @Date 2020/7/28
 * @Email liuzhiqiang@moretickets.com
 * @Description
 */
@GlideModule
class GlideModule : AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(HttpClient.okHttpClient))
    }
}