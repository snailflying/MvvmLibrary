package com.theone.framework.widget.toast

import android.app.Application
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast

/**
 * @Author zhiqiang
 * @Email liuzhiqiang@theone.com
 * @Date 2019-06-22
 * @Description Toast 基类
 */
open class BaseToast internal constructor(application: Application) : Toast(application) {

    // 吐司消息 View
    private var mMessageView: TextView? = null

    override fun setView(view: View) {
        super.setView(view)
        mMessageView = getMessageView(view)
    }

    override fun setText(s: CharSequence) {
        mMessageView!!.text = s
    }

    /**
     * 智能获取用于显示消息的 TextView
     */
    private fun getMessageView(view: View): TextView {
        if (view is TextView) {
            return view
        } else if (view.findViewById<View>(android.R.id.message) is TextView) {
            return view.findViewById<View>(android.R.id.message) as TextView
        } else if (view is ViewGroup) {
            val textView = findTextView(view)
            if (textView != null) {
                return textView
            }
        }
        // 如果设置的布局没有包含一个 TextView 则抛出异常，必须要包含一个 TextView 作为 MessageView
        throw IllegalArgumentException("The layout must contain a TextView")
    }

    /**
     * 递归获取 ViewGroup 中的 TextView 对象
     */
    private fun findTextView(group: ViewGroup): TextView? {
        for (i in 0 until group.childCount) {
            val view = group.getChildAt(i)
            if (view is TextView) {
                return view
            } else if (view is ViewGroup) {
                val textView = findTextView(view)
                if (textView != null) return textView
            }
        }
        return null
    }
}