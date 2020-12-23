package com.theone.framework.ext

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Outline
import android.os.Build
import android.view.View
import android.view.ViewOutlineProvider
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView

/**
 * @Author zhiqiang
 * @Email liuzhiqiang@theone.com
 * @Date 2019-06-19
 * @Description
 */

/**
 * 获取editText的字符串内容
 */
fun EditText.getTextString(): String? {
    return editableText?.toString()?.trim()
}

/**
 * 隐藏键盘
 * @receiver View
 * @return Boolean
 */
fun View.hideKeyboard(): Boolean {
    clearFocus()
    return (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
        windowToken,
        0
    )
}

/**
 * 显示键盘
 * @receiver View
 * @return Boolean
 */
fun View.showKeyboard(): Boolean {
    requestFocus()
    return (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(
        this,
        InputMethodManager.SHOW_IMPLICIT
    )
}


/**
 * 带显示状态的 TextView setText 扩展
 * @param char text
 */
fun <T : TextView> T.setTextWithVisible(char: CharSequence?) {
    text = char
    visibility = if (char.isNullOrEmpty()) View.GONE else View.VISIBLE
}

/**
 * 设置 shadow 阴影
 * @param elevation Z 轴偏移
 * @param alpha outline 透明度
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
fun <T : View> T.setShadow(elevation: Float = 30f, alpha: Float = 0.3f) {
    if (Build.VERSION.SDK_INT >= 21) {
        setElevation(elevation)
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline?) {
                val background = view.background
                if (background != null) {
                    background.getOutline(outline!!)
                    outline.alpha = alpha
                } else {
                    outline!!.setRect(0, 0, view.width, view.height)
                    outline.alpha = 0.0f
                }
            }
        }
    }
}

/***************************延迟点击相关 Start******************************/

/***
 * 带延迟过滤的点击事件View扩展
 * @param delay Long 延迟时间，默认600毫秒
 * @param block: (T) -> Unit 函数
 * @return Unit
 */
fun <T : View> T.clickWithTrigger(time: Long = 600, block: (T) -> Unit) {
    triggerDelay = time
    setOnClickListener {
        if (clickEnable()) {
            block(it as T)
        }
    }
}

private var <T : View> T.triggerLastTime: Long
    get() = if (getTag(1123460103) != null) getTag(1123460103) as Long else -601
    set(value) {
        setTag(1123460103, value)
    }

private var <T : View> T.triggerDelay: Long
    get() = if (getTag(1123461123) != null) getTag(1123461123) as Long else 600
    set(value) {
        setTag(1123461123, value)
    }

private fun <T : View> T.clickEnable(): Boolean {
    var flag = false
    val currentClickTime = System.currentTimeMillis()
    if (currentClickTime - triggerLastTime >= triggerDelay) {
        flag = true
        triggerLastTime = currentClickTime
    }
    return flag
}

fun <T : View> T.longClick(block: (T) -> Boolean) = setOnLongClickListener { block(it as T) }

/***
 * 带延迟过滤的点击事件监听 View.OnClickListener
 * 延迟时间根据triggerDelay获取：600毫秒，不能动态设置
 */
interface OnLazyClickListener : View.OnClickListener {

    override fun onClick(v: View?) {
        if (v?.clickEnable() == true) {
            onLazyClick(v)
        }
    }

    fun onLazyClick(v: View)
}
/***************************延迟点击相关 End******************************/
