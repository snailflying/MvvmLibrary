/*
 * Copyright 2013 Inmite s.r.o. (www.inmite.eu).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.themone.core.widgets.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.themone.core.widgets.dialog.BaseDialogBuilder.Companion.DEFAULT_CANCELABLE_ON_TOUCH_OUTSIDE
import com.themone.core.widgets.dialog.BaseDialogBuilder.Companion.DEFAULT_DIM_AMOUNT
import com.themone.core.widgets.dialog.BaseDialogBuilder.Companion.DEFAULT_REQUEST_CODE
import com.themone.core.widgets.dialog.BaseDialogBuilder.Companion.DEFAULT_SCALE
import com.themone.core.widgets.dialog.BaseDialogBuilder.Companion.DEFAULT_SHOW_FROM_BOTTOM
import com.themone.core.widgets.dialog.iface.IDialogNegativeListener
import com.themone.core.widgets.dialog.iface.IDialogPositiveListener
import com.themone.core.widgets.dialog.iface.IDialogCancelListener
import com.themone.core.widgets.dialog.iface.IDialogDismissListener
import com.themone.theone.library.R
import java.util.*

/**
 * @Author zhiqiang
 * @Email liuzhiqiang@theone.com
 * @Date 2019/2/16
 * @Description dialog的基类
 */
abstract class BaseDialogFragment : DialogFragment() {

    protected var mRequestCode = DEFAULT_REQUEST_CODE
    /**
     * 点击外部隐藏dialog，默认开启
     */
    private var canceledOnTouchOutside = DEFAULT_CANCELABLE_ON_TOUCH_OUTSIDE
    /**
     * 灰度深浅
     */
    private var dimAmount = DEFAULT_DIM_AMOUNT
    /**
     * 宽度缩放
     */
    private var scale = DEFAULT_SCALE

    /**
     * 是否底部显示
     */
    private var showFromBottom = DEFAULT_SHOW_FROM_BOTTOM
    /**
     * 主题
     */
    private var mTheme: Int = 0
    /**
     * 动画
     */
    private var animStyle: Int = 0


    protected var mContext: Activity? = null

    protected val cancelListeners: List<IDialogCancelListener>
        get() = getDialogListeners(IDialogCancelListener::class.java)

    protected val dismissListeners: List<IDialogDismissListener>
        get() = getDialogListeners(IDialogDismissListener::class.java)


    /**
     * positive 按钮事件，可能不止一个（activity嵌套fragment）
     *
     * @return Dialog listeners
     */
    protected val positiveListeners: List<IDialogPositiveListener>
        get() = getDialogListeners(IDialogPositiveListener::class.java)

    /**
     * negative 按钮事件，可能不止一个（activity嵌套fragment）
     *
     * @return Dialog listeners
     */
    protected val negativeListeners: List<IDialogNegativeListener>
        get() = getDialogListeners(IDialogNegativeListener::class.java)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val targetFragment = targetFragment
        if (targetFragment != null) {
            mRequestCode = targetRequestCode
        } else {
            val args = arguments
            if (args != null) {
                mRequestCode = args.getInt(BaseDialogBuilder.ARG_REQUEST_CODE, DEFAULT_REQUEST_CODE)
            }
        }
        //放在onCreateDialog()不起作用
        initDialogParams(dialog)
    }

    override fun onDestroyView() {
        if (dialog != null && retainInstance) {
            dialog.setDismissMessage(null)
        }
        super.onDestroyView()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        for (listener in dismissListeners) {
            listener.onDismissed(mRequestCode)
        }
    }

    override fun onCancel(dialog: DialogInterface?) {
        super.onCancel(dialog)
        for (listener in cancelListeners) {
            listener.onCancelled(mRequestCode)
        }
    }

    /**
     * 采用fragment interface pattern方式传递callback回调
     * targetFragment需要配合[BaseDialogBuilder.setTargetFragment]
     *
     * @param listenerInterface
     * @param <T>
     * @return
    </T> */
    fun <T> getDialogListeners(listenerInterface: Class<T>): List<T> {
        val targetFragment = targetFragment
        val listeners = ArrayList<T>(2)
        if (targetFragment != null && listenerInterface.isAssignableFrom(targetFragment.javaClass)) {
            listeners.add(targetFragment as T)
        }
        if (activity != null && listenerInterface.isAssignableFrom(activity!!.javaClass)) {
            listeners.add(activity as T)
        }
        return Collections.unmodifiableList(listeners)
    }

    private fun initDialogParams(dialog: Dialog) {
        val args = arguments
        if (args != null) {
            canceledOnTouchOutside =
                args.getBoolean(BaseDialogBuilder.ARG_CANCELABLE_ON_TOUCH_OUTSIDE, DEFAULT_CANCELABLE_ON_TOUCH_OUTSIDE)
            showFromBottom = args.getBoolean(BaseDialogBuilder.ARG_SHOW_FROM_BOTTOM, DEFAULT_SHOW_FROM_BOTTOM)
            dimAmount = args.getFloat(BaseDialogBuilder.ARG_DIM_AMOUNT, DEFAULT_DIM_AMOUNT)
            scale = args.getDouble(BaseDialogBuilder.ARG_SCALE, DEFAULT_SCALE)
            animStyle = args.getInt(BaseDialogBuilder.ARG_ANIM_STYLE)
            mTheme = args.getInt(BaseDialogBuilder.ARG_USE_THEME, theme)
        }
        setStyle(DialogFragment.STYLE_NO_TITLE, mTheme)

        dialog.setCanceledOnTouchOutside(canceledOnTouchOutside)
        val window = dialog.window
        if (window != null) {
            val lp = window.attributes
            //调节灰色背景透明度[0-1]，默认0.5f
            lp.dimAmount = dimAmount
            //是否在底部显示
            if (showFromBottom) {
                lp.gravity = Gravity.BOTTOM
                if (animStyle == 0) {
                    animStyle = R.style.Dialog_Animation
                }
            }
            //占用屏幕宽度一定比例
            if (scale > 1) {
                scale = 1.0
            }
            lp.width = (getScreenWidth(mContext!!) * scale).toInt()
            //设置dialog高度
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            //设置dialog进入、退出的动画
            if (animStyle != 0) {
                window.setWindowAnimations(animStyle)
            }
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.attributes = lp
        }
    }

    private fun getScreenWidth(context: Context): Int {
        val dm = context.resources.displayMetrics
        return dm.widthPixels
    }

    internal fun showAllowingStateLoss(manager: FragmentManager, tag: String, dismissPreDialog: Boolean?) {
        val ft = manager.beginTransaction()
        //将之前的dialog隐藏
        val targetFragment = manager.findFragmentByTag(tag)
        if (dismissPreDialog!! && targetFragment is BaseDialogFragment) {
            ft.remove(targetFragment)
        }

        ft.add(this, tag)
        ft.commitAllowingStateLoss()
    }


    internal fun showWithDismissPreDialog(manager: FragmentManager, tag: String, dismissPreDialog: Boolean?) {
        val ft = manager.beginTransaction()
        //将之前的dialog隐藏
        val targetFragment = manager.findFragmentByTag(tag)
        if (dismissPreDialog!! && targetFragment is BaseDialogFragment) {
            ft.remove(targetFragment).commit()
        }
        show(manager, tag)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = activity
    }
}