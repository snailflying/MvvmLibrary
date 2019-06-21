package com.juqitech.moretickets.core.util

import android.content.Context
import android.view.Gravity
import androidx.annotation.StringRes
import android.widget.Toast

/**
 * @author zhanfeng
 * @date 2019-06-04
 * @desc
 */
class ToastUtil private constructor(context: Context) {

    private val context: Context = context.applicationContext
    private var toast: Toast? = null
    private var msg: String? = null

    private fun create(): Toast {
        if (null == toast) {
            toast = Toast.makeText(context, "", Toast.LENGTH_SHORT)

        }
        toast!!.setText(msg)
        toast!!.setGravity(Gravity.CENTER, 0, 0)
        toast!!.duration = Toast.LENGTH_LONG
        return toast!!
    }

    private fun createShort(): Toast {
        if (null == toast) {
            toast = Toast.makeText(context, "", Toast.LENGTH_SHORT)
        }
        toast!!.setText(msg)
        toast!!.setGravity(Gravity.CENTER, 0, 0)
        toast!!.duration = Toast.LENGTH_SHORT
        return toast!!
    }

    private fun setText(text: String) {
        msg = text
    }

    companion object {

        private var td: ToastUtil? = null

        @JvmStatic
        fun showLong(context: Context, @StringRes resId: Int) {
            showLong(context, context.getString(resId))
        }

        @JvmStatic
        fun showLong(context: Context, msg: String) {
            if (td == null) {
                td = ToastUtil(context)
            }
            td!!.setText(msg)
            td!!.create().show()
        }

        @JvmStatic
        fun showShort(context: Context, @StringRes resId: Int) {
            showShort(context, context.getString(resId))
        }

        @JvmStatic
        fun showShort(context: Context, msg: String) {
            if (td == null) {
                td = ToastUtil(context)
            }
            td!!.setText(msg)
            td!!.createShort().show()
        }
    }

}
