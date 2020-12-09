package com.theone.framework.image

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.load.resource.bitmap.CenterCrop

/**
 * @Author ZhiQiang
 * @Date 2020/6/23
 * @Description 图片加载框架封装
 */
object AppImageLoader {
    fun with(context: Context): ImageLoaderBuilder {
        return ImageLoaderBuilder().with(context)
    }

    fun with(view: View): ImageLoaderBuilder {
        return ImageLoaderBuilder().with(view)
    }

    fun with(activity: Activity): ImageLoaderBuilder {
        return ImageLoaderBuilder().with(activity)
    }

    fun with(fragment: Fragment): ImageLoaderBuilder {
        return ImageLoaderBuilder().with(fragment)
    }

}