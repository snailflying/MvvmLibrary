package com.theone.framework.image

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.request.RequestOptions

/**
 * @Author zhiqiang
 * @Date 2020/6/14
 * @Description 图片加载框架封装
 */
class ImageLoaderBuilder {
    private lateinit var glideManager: RequestManager
    private lateinit var glideBuilder: RequestBuilder<Drawable>
    internal fun with(context: Context): ImageLoaderBuilder {
        glideManager = GlideApp.with(context)
        return this
    }

    internal fun with(view: View): ImageLoaderBuilder {
        glideManager = GlideApp.with(view)
        return this
    }

    internal fun with(activity: Activity): ImageLoaderBuilder {
        glideManager = GlideApp.with(activity)
        return this
    }

    internal fun with(fragment: Fragment): ImageLoaderBuilder {
        glideManager = GlideApp.with(fragment)
        return this
    }

    fun load(uri: String?): ImageLoaderBuilder {
        glideBuilder = glideManager.load(uri)
        return this
    }

    fun load(drawable: Drawable): ImageLoaderBuilder {
        glideBuilder = glideManager.load(drawable)
        return this
    }

    fun dontAnimate(): ImageLoaderBuilder {
        glideBuilder = glideBuilder.dontAnimate()
        return this
    }

    fun placeholder(@DrawableRes resourceId: Int): ImageLoaderBuilder {
        glideBuilder = glideBuilder.placeholder(resourceId)
        return this
    }

    fun error(@DrawableRes resourceId: Int): ImageLoaderBuilder {
        glideBuilder = glideBuilder.error(resourceId)
        return this
    }

    fun transform(transformation: Transformation<Bitmap?>): ImageLoaderBuilder {
        glideBuilder = glideBuilder.transform(transformation)
        return this
    }

    fun apply(requestOptions: RequestOptions): ImageLoaderBuilder {
        glideBuilder = glideBuilder.apply(requestOptions)
        return this
    }

    fun transform(vararg transformation: Transformation<Bitmap?>): ImageLoaderBuilder {
        glideBuilder = glideBuilder.transform(*transformation)
        return this
    }

    fun thumbnail(thumbnailRequest: RequestBuilder<Drawable>?): ImageLoaderBuilder {
        glideBuilder = glideBuilder.thumbnail(thumbnailRequest)
        return this
    }

    fun into(view: ImageView) {
        glideBuilder.into(view)
    }

}