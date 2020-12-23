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

package com.theone.framework.widget.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.theone.framework.R
import com.theone.framework.widget.dialog.iface.IDialogCancelListener
import com.theone.framework.widget.dialog.iface.IDialogDismissListener
import com.theone.framework.widget.dialog.iface.IDialogNegativeListener
import com.theone.framework.widget.dialog.iface.IDialogPositiveListener
import java.util.*

/**
 * @Author zhiqiang
 * @Date 2019/2/16
 * @Description dialog的基类
 * TODO:调用setArguments()时，请使用 val bundle = fragment.arguments?:Bundle()来获取Bundle,否则会影响setScale()等方法
 */
open class BaseDialogFragment : AppCompatDialogFragment() {
    protected lateinit var mContext: Context

    private var mRequestCode = DEFAULT_REQUEST_CODE

    /**
     * 点击外部隐藏dialog，默认开启
     */
    private var canceledOnTouchOutside = DEFAULT_CANCELABLE_ON_TOUCH_OUTSIDE

    /**
     * 背景灰度深浅
     */
    private var bgAlphaDimAmount: Float = DEFAULT_DIM_AMOUNT

    /**
     * 宽度缩放
     */
    private var scale = DEFAULT_SCALE

    /**
     * 是否全屏
     */
    private var fullScreen: Boolean = DEFAULT_FULLSCREEN

    /**
     * 是否底部显示
     */
    private var showFromBottom: Boolean = DEFAULT_SHOW_FROM_BOTTOM

    /**
     * 当Dialog为BottomSheet时，是否完全展开
     */
    private var expandBottomSheet: Boolean = DEFAULT_EXPAND_BOTTOM_SHEET

    /**
     * 主题
     */
    private var mTheme: Int = R.style.Dialog

    /**
     * 动画
     */
    private var animStyle: Int = 0

    fun setRequestCode(value: Int): BaseDialogFragment {
        mRequestCode = value
        if (arguments == null) {
            val bundle = Bundle()
            bundle.putInt(ARG_REQUEST_CODE, value)
            arguments = bundle
        } else {
            arguments?.putInt(ARG_REQUEST_CODE, value)
        }
        return this
    }


    /**
     * 背景灰度深浅
     */
    fun setBgAlphaDimAmount(value: Float): BaseDialogFragment {
        bgAlphaDimAmount = value
        if (arguments == null) {
            val bundle = Bundle()
            bundle.putFloat(ARG_DIM_AMOUNT, value)
            arguments = bundle
        } else {
            arguments?.putFloat(ARG_DIM_AMOUNT, value)
        }
        return this
    }

    /**
     * 宽度缩放
     */
    fun setScale(value: Double): BaseDialogFragment {
        scale = value
        if (arguments == null) {
            val bundle = Bundle()
            bundle.putDouble(ARG_SCALE, value)
            arguments = bundle
        } else {
            arguments?.putDouble(ARG_SCALE, value)
        }
        return this
    }

    /**
     * 点击外部隐藏dialog，默认开启
     */
    fun setCanceledOnTouchOutside(value: Boolean): BaseDialogFragment {
        canceledOnTouchOutside = value
        if (arguments == null) {
            val bundle = Bundle()
            bundle.putBoolean(ARG_CANCELABLE_ON_TOUCH_OUTSIDE, value)
            arguments = bundle
        } else {
            arguments?.putBoolean(ARG_CANCELABLE_ON_TOUCH_OUTSIDE, value)
        }
        return this
    }

    /**
     * 是否全屏
     */
    fun setFullscreen(value: Boolean): BaseDialogFragment {
        fullScreen = value
        if (arguments == null) {
            val bundle = Bundle()
            bundle.putBoolean(ARG_FULLSCREEN, value)
            arguments = bundle
        } else {
            arguments?.putBoolean(ARG_FULLSCREEN, value)
        }
        return this
    }

    /**
     * 是否底部显示
     */
    fun setShowFromBottom(value: Boolean): BaseDialogFragment {
        showFromBottom = value
        if (arguments == null) {
            val bundle = Bundle()
            bundle.putBoolean(ARG_SHOW_FROM_BOTTOM, value)
            arguments = bundle
        } else {
            arguments?.putBoolean(ARG_SHOW_FROM_BOTTOM, value)
        }
        return this
    }

    /**
     * 当Dialog为BottomSheet时，是否完全展开
     */
    fun setExpandBottomSheet(value: Boolean): BaseDialogFragment {
        expandBottomSheet = value
        if (arguments == null) {
            val bundle = Bundle()
            bundle.putBoolean(ARG_EXPAND_BOTTOM_SHEET, value)
            arguments = bundle
        } else {
            arguments?.putBoolean(ARG_EXPAND_BOTTOM_SHEET, value)
        }
        return this
    }

    /**
     * 主题
     */
    fun setTheme(value: Int): BaseDialogFragment {
        mTheme = value
        if (arguments == null) {
            val bundle = Bundle()
            bundle.putInt(ARG_USE_THEME, value)
            arguments = bundle
        } else {
            arguments?.putInt(ARG_ANIM_STYLE, value)
        }
        return this
    }

    /**
     * 动画
     */
    fun setAnimStyle(value: Int): BaseDialogFragment {
        animStyle = value

        if (arguments == null) {
            val bundle = Bundle()
            bundle.putInt(ARG_ANIM_STYLE, value)
            arguments = bundle
        } else {
            arguments?.putInt(ARG_ANIM_STYLE, value)
        }
        return this
    }

    /**
     * 如果两个dialog的tag一致，则隐藏上一个dialog
     *
     * 报"IllegalStateException : Can not perform this action after onSaveInstanceState()"异常的时候使用此show
     * @param manager FragmentManager
     * @param tag String
     */
    @JvmOverloads
    fun showAllowingStateLoss(
        manager: FragmentManager?,
        tag: String? = null,
        dismissPreDialog: Boolean? = true
    ): BaseDialogFragment {
        val ft = manager?.beginTransaction()
        //将之前的dialog隐藏
        val targetFragment = manager?.findFragmentByTag(tag)
        if (dismissPreDialog!! && targetFragment is BaseDialogFragment) {
            ft?.remove(targetFragment)
        }

        ft?.add(this, tag)
        ft?.commitAllowingStateLoss()
        return this
    }

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

    /**
     * 步骤1
     * @param savedInstanceState Bundle?
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initArguments()
    }

    /**
     * 步骤2
     * 支持BottomSheetDialog
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return (if (showFromBottom) {
            BottomSheetDialog(this.context!!, this.theme)
        } else {
            Dialog(activity!!, theme)
        })
    }

    /**
     * 步骤3
     * @param savedInstanceState Bundle?
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //放在onCreateDialog()不起作用
        initDialogParams(dialog)
    }

    override fun onDestroyView() {
        if (dialog != null && retainInstance) {
            dialog?.setDismissMessage(null)
        }
        super.onDestroyView()
    }

    override fun onDismiss(dialog: DialogInterface) {
        for (listener in dismissListeners) {
            listener.onDismissed(mRequestCode)
        }
        super.onDismiss(dialog)
    }

    override fun onCancel(dialog: DialogInterface) {
        for (listener in cancelListeners) {
            listener.onCancelled(mRequestCode)
        }
        super.onCancel(dialog)
    }


    /**
     * 采用fragment interface pattern方式传递callback回调
     * targetFragment需要配合[setTargetFragment]
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

    /**
     * 因为setStyle()的原因，必须在必须在[onCreate]或[onStart]或[onResume]内调用
     */
    private fun initArguments() {
        val targetFragment = targetFragment
        if (targetFragment != null) {
            mRequestCode = targetRequestCode
        } else {
            val args = arguments
            if (args != null) {
                mRequestCode = args.getInt(ARG_REQUEST_CODE, DEFAULT_REQUEST_CODE)
            }
        }

        val args = arguments
        if (args != null) {
            canceledOnTouchOutside = args.getBoolean(
                ARG_CANCELABLE_ON_TOUCH_OUTSIDE,
                DEFAULT_CANCELABLE_ON_TOUCH_OUTSIDE
            )
            showFromBottom = args.getBoolean(
                ARG_SHOW_FROM_BOTTOM,
                DEFAULT_SHOW_FROM_BOTTOM
            )
            fullScreen = args.getBoolean(ARG_FULLSCREEN, DEFAULT_FULLSCREEN)
            expandBottomSheet = args.getBoolean(
                ARG_EXPAND_BOTTOM_SHEET,
                DEFAULT_EXPAND_BOTTOM_SHEET
            )
            bgAlphaDimAmount = args.getFloat(ARG_DIM_AMOUNT, DEFAULT_DIM_AMOUNT)
            scale = args.getDouble(ARG_SCALE, DEFAULT_SCALE)
            animStyle = args.getInt(ARG_ANIM_STYLE)
            val defaultTheme = if (theme == 0) -1 else theme
            mTheme = args.getInt(ARG_USE_THEME, R.style.Dialog)
        }
        //必须在onCreate()内调用才起作用
        setStyle(DialogFragment.STYLE_NO_TITLE, mTheme)
    }

    private fun initDialogParams(dialog: Dialog?) {
        if (dialog == null) return
        dialog.setCanceledOnTouchOutside(canceledOnTouchOutside)
        val window = dialog.window
        if (window != null) {
            val lp = window.attributes
            //调节灰色背景透明度[0-1]，默认0.5f
            lp.dimAmount = bgAlphaDimAmount
            //是否在底部显示
            if (showFromBottom) {
                lp.gravity = Gravity.BOTTOM
                if (animStyle == 0) {
                    animStyle = R.style.Dialog_Animation
                }
                expandBottomSheet(dialog)
            }
            if (fullScreen) {
                lp.width = WindowManager.LayoutParams.MATCH_PARENT
                lp.height = WindowManager.LayoutParams.MATCH_PARENT
            } else {
                //占用屏幕宽度一定比例
                if (scale > 1) {
                    scale = 1.0
                }
                lp.width = (getScreenWidth(mContext) * scale).toInt()
                //设置dialog高度
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            }

            //设置dialog进入、退出的动画
            if (animStyle != 0) {
                window.setWindowAnimations(animStyle)
            }
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.attributes = lp
        }
    }

    /**
     * BottomSheetDialog的默认高度为256dp，所以要处理一下全部展开。
     * @param dialog Dialog
     */
    private fun expandBottomSheet(dialog: Dialog) {
        if (expandBottomSheet && dialog is BottomSheetDialog) {
            view?.run {
                if (this is CoordinatorLayout) {
                    val child = this.getChildAt(0)
                    BottomSheetBehavior.from((child))
                        .state = BottomSheetBehavior.STATE_EXPANDED
                } else {
                    BottomSheetBehavior.from(this.parent as View)
                        .state = BottomSheetBehavior.STATE_EXPANDED
                }

            }
        }

    }

    private fun getScreenWidth(context: Context): Int {
        val dm = context.resources.displayMetrics
        return dm.widthPixels
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    companion object {

        internal const val ARG_REQUEST_CODE = "request_code"
        internal const val ARG_FULLSCREEN = "arg_fullscreen"
        internal const val ARG_SHOW_FROM_BOTTOM = "arg_show_from_bottom"
        internal const val ARG_EXPAND_BOTTOM_SHEET = "arg_expand_bottom_sheet"
        internal const val ARG_CANCELABLE_ON_TOUCH_OUTSIDE = "cancelable_oto"
        internal const val ARG_USE_THEME = "arg_use_theme_type"
        internal const val ARG_DIM_AMOUNT = "arg_dim_amount"
        internal const val ARG_ANIM_STYLE = "arg_anim_style"
        internal const val ARG_SCALE = "arg_scale"

        /**
         * 默认值
         */
        internal const val DEFAULT_DIM_AMOUNT = 0.5f
        internal const val DEFAULT_SCALE = 0.75
        internal const val DEFAULT_CANCELABLE_ON_TOUCH_OUTSIDE = true
        internal const val DEFAULT_REQUEST_CODE = -42
        internal const val DEFAULT_SHOW_FROM_BOTTOM = false
        internal const val DEFAULT_EXPAND_BOTTOM_SHEET = true
        internal const val DEFAULT_FULLSCREEN = false
    }
}