package com.juqitech.moretickets.core.widgets.dialog

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

import java.io.Serializable

/**
 * @Author zhiqiang
 * @Email liuzhiqiang@moretickets.com
 * @Date 2019/2/16
 */
open class BaseDialogBuilder(val mContext: Context, protected val mFragmentManager: FragmentManager, private val mClass: Class<out BaseDialogFragment>) {

    private var mDialogFragment: BaseDialogFragment? = null
    private var mTargetFragment: Fragment? = null

    private var mTag: String
    private var mRequestCode =
        DEFAULT_REQUEST_CODE

    /**
     * 是否底部显示
     */
    private var mShowFromBottom =
        DEFAULT_SHOW_FROM_BOTTOM
    /**
     * 灰度深浅
     */
    private var mDimAmount = DEFAULT_DIM_AMOUNT
    /**
     * 占用屏幕宽度一定比例
     */
    private var mScale = DEFAULT_SCALE
    /**
     * 默认点击屏幕取消dialog
     */
    private var mCancelableOnTouchOutside =
        DEFAULT_CANCELABLE_ON_TOUCH_OUTSIDE
    private var mCancelable = true
    private var mDismissPreDialog: Boolean? = true
    /**
     * 主题
     */
    private var mTheme: Int = 0
    /**
     * 动画
     */
    private var mAnimStyle: Int = 0
    private var args: Bundle? = null

    val fragment: BaseDialogFragment?
        get() {
            if (mDialogFragment == null) {
                build()
            }
            return mDialogFragment
        }

    init {
        mTag = mClass.simpleName
    }

    /**
     * 如果要增加额外传递参数的方法，需要重载此方法添加
     *
     * @return
     */
    protected fun prepareArguments(): Bundle {
        if (args == null) {
            args = Bundle()
        }
        return args!!
    }

    /**
     * @param args 直接传Bundle
     * @return
     */
    fun putBundle(args: Bundle): BaseDialogBuilder {
        this.args = args
        return this
    }

    /**
     * @param key   Bundle传值key
     * @param value Bundle传值value
     * @return
     */
    fun putArgs(key: String, value: Serializable): BaseDialogBuilder {
        prepareArguments().putSerializable(key, value)
        return this
    }

    /**
     * @param cancelable 能否取消显示
     * @return
     */
    fun setCancelable(cancelable: Boolean): BaseDialogBuilder {
        mCancelable = cancelable
        return this
    }

    /**
     * @param cancelable 点击屏幕取消dialog
     * @return
     */
    fun setCancelableOnTouchOutside(cancelable: Boolean): BaseDialogBuilder {
        mCancelableOnTouchOutside = cancelable
        if (cancelable) {
            mCancelable = true
        }
        return this
    }

    /**
     * @param alpha 透明度
     * @return
     */
    fun setBgAlpha(alpha: Float): BaseDialogBuilder {
        mDimAmount = alpha
        return this
    }

    /**
     * @param scale 占用屏幕宽度比例
     * @return
     */
    fun setWidthScale(scale: Double): BaseDialogBuilder {
        mScale = scale
        return this
    }

    /**
     * @param fromBottom 是否从底部弹起
     * @return
     */
    fun setShowFromBottom(fromBottom: Boolean): BaseDialogBuilder {
        mShowFromBottom = fromBottom
        return this
    }

    /**
     * @param animStyle 动画样式
     * @return
     */
    fun setAnimStyle(animStyle: Int): BaseDialogBuilder {
        mAnimStyle = animStyle
        return this
    }

    /**
     * 如果是从Fragment调用，则必须调用此方法
     * 否则[BaseDialogFragment.getDialogListeners]
     * 获取不到值，导致callback回调不起作用
     *
     * @param fragment
     * @param requestCode 返回值code
     * @return
     */
    fun setTargetFragment(fragment: Fragment, requestCode: Int): BaseDialogBuilder {
        mTargetFragment = fragment
        mRequestCode = requestCode
        return this
    }

    /**
     * 如果设置了mTag则自动不会隐藏，否则可调用此方法不隐藏
     *
     * @param dismissPreDialog Boolean
     * @return DialogBuilder
     */
    fun setDismissPreDialog(dismissPreDialog: Boolean?): BaseDialogBuilder {
        mDismissPreDialog = dismissPreDialog
        return this
    }

    /**
     * @param requestCode 返回值code
     * @return
     */
    fun setRequestCode(requestCode: Int): BaseDialogBuilder {
        mRequestCode = requestCode
        return this
    }

    /**
     * @param tag fragment的tag
     * @return
     */
    fun setTag(tag: String): BaseDialogBuilder {
        mTag = tag
        return this
    }

    /**
     * @param theme 主题
     * @return
     */
    fun setTheme(theme: Int): BaseDialogBuilder {
        mTheme = theme
        return this
    }

    fun build(): BaseDialogBuilder {
        val args = prepareArguments()
        val fragment = Fragment.instantiate(mContext, mClass.name, args) as BaseDialogFragment
        args.putBoolean(ARG_CANCELABLE_ON_TOUCH_OUTSIDE, mCancelableOnTouchOutside)
        //显示底部
        args.putBoolean(ARG_SHOW_FROM_BOTTOM, mShowFromBottom)
        //设置主题
        args.putInt(ARG_USE_THEME, mTheme)
        //透明度
        args.putFloat(ARG_DIM_AMOUNT, mDimAmount)
        //动画
        args.putInt(ARG_ANIM_STYLE, mAnimStyle)
        //占用屏幕宽度一定比例
        args.putDouble(ARG_SCALE, mScale)

        if (mTargetFragment != null) {
            fragment.setTargetFragment(mTargetFragment, mRequestCode)
        } else {
            args.putInt(ARG_REQUEST_CODE, mRequestCode)
        }
        fragment.isCancelable = mCancelable
        this.mDialogFragment = fragment
        return this
    }

    fun show(): BaseDialogFragment {
        if (mDialogFragment == null) {
            build()
        }
        mDialogFragment!!.showWithDismissPreDialog(mFragmentManager, mTag, mDismissPreDialog)
        return mDialogFragment!!
    }

    /**
     * 报"IllegalStateException : Can not perform this action after onSaveInstanceState()"异常的时候使用此show
     */
    fun showAllowingStateLoss(): BaseDialogFragment {
        if (mDialogFragment == null) {
            build()
        }
        mDialogFragment!!.showAllowingStateLoss(mFragmentManager, mTag, mDismissPreDialog)
        return mDialogFragment!!
    }

    companion object {

        internal val ARG_REQUEST_CODE = "request_code"
        internal val ARG_FULL_SCREEN = "arg_full_screen"
        internal val ARG_SHOW_FROM_BOTTOM = "arg_show_from_bottom"
        internal val ARG_CANCELABLE_ON_TOUCH_OUTSIDE = "cancelable_oto"
        internal val ARG_USE_THEME = "arg_use_theme_type"
        internal val ARG_DIM_AMOUNT = "arg_dim_amount"
        internal val ARG_ANIM_STYLE = "arg_anim_style"
        internal val ARG_SCALE = "arg_scale"
        /**
         * 默认值
         */
        internal val DEFAULT_DIM_AMOUNT = 0.5f
        internal val DEFAULT_SCALE = 0.75
        internal val DEFAULT_CANCELABLE_ON_TOUCH_OUTSIDE = true
        internal val DEFAULT_REQUEST_CODE = -42
        internal val DEFAULT_SHOW_FROM_BOTTOM = false
    }
}