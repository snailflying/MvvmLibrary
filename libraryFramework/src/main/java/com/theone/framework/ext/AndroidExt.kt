package com.theone.framework.ext

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.theone.framework.base.BaseApp

/**
 * @Author ZhiQiang
 * @Date 2020/10/12
 * @Description
 */

fun dp2px(dipValue: Float): Int {
    val sMetrics = Resources.getSystem().displayMetrics
    val scale: Float = sMetrics?.density ?: 1f
    return (dipValue * scale + 0.5f).toInt()
}

fun dp2px(dipValue: Int): Int {
    val sMetrics = Resources.getSystem().displayMetrics
    val scale: Float = sMetrics?.density ?: 1f
    return (dipValue * scale + 0.5f).toInt()
}

fun getCompatDrawable(@DrawableRes resId: Int, context: Context? = BaseApp.application): Drawable? {
    return if (null == context || context.resources == null) {
        ColorDrawable()
    } else ContextCompat.getDrawable(context, resId)
}

fun getDrawable(@DrawableRes resId: Int, context: Context? = BaseApp.application): Drawable? {
    return if (null == context || context.resources == null) {
        ColorDrawable()
    } else ContextCompat.getDrawable(context, resId)
}


/**
 * get the color by id
 */
fun getCompatColor(@ColorRes resId: Int, context: Context? = BaseApp.application): Int {
    return if (null == context || context.resources == null) {
        Color.TRANSPARENT
    } else ContextCompat.getColor(context, resId)
}

/**
 * get the color by id
 */
fun getColor(@ColorRes resId: Int, context: Context? = BaseApp.application): Int {
    return if (null == context || context.resources == null) {
        Color.TRANSPARENT
    } else ContextCompat.getColor(context, resId)
}

/**
 * 获取dimen的数值(没有转px的原始值)
 */
fun getDimension(@DimenRes resId: Int, context: Context? = BaseApp.application): Float {
    return if (null == context || context.resources == null) {
        0f
    } else context.resources.getDimension(resId)
}

/**
 * 获取dimen转成px的数值
 */
fun getDimensionPixelSize(@DimenRes resId: Int, context: Context? = BaseApp.application): Int {
    return if (null == context || context.resources == null) {
        0
    } else context.resources.getDimensionPixelSize(resId)
}

/**
 * get the String by id
 */
fun getString(@StringRes resId: Int, context: Context? = BaseApp.application): String {
    return if (null == context || context.resources == null) {
        ""
    } else context.getString(resId)
}