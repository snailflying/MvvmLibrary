package com.theone.framework.widget.dialog

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.theone.framework.R

/**
 * @Author zhiqiang
 * @Email liuzhiqiang@theone.com
 * @Date 2019/2/16
 */
open class BaseDialogBuilder(
    private val mContext: Context,
    private val mClass: Class<out BaseDialogFragment>
) {

    private var mDialogFragment: BaseDialogFragment? = null
    private var mTargetFragment: Fragment? = null

    private var mTag: String
    private var mRequestCode =
        DEFAULT_REQUEST_CODE

    /**
     * 是否底部显示
     */
    private var mShowFromBottom = DEFAULT_SHOW_FROM_BOTTOM

    /**
     * 当Dialog为BottomSheet时，是否完全展开
     */
    private var mExpandBottomSheet = DEFAULT_EXPAND_BOTTOM_SHEET

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
    private var mTheme: Int = R.style.Dialog

    /**
     * 动画
     */
    private var mAnimStyle: Int = 0
    private var args: Bundle? = null

    /**
     * 如果要增加额外传递参数的方法，需要重载此方法添加
     *
     * @return
     */
    protected open fun prepareArguments(): Bundle {
        if (args == null) {
            args = Bundle()
        }
        return args!!
    }

    /**
     * @param args 直接传Bundle
     * @return
     */
    fun setBundle(args: Bundle): BaseDialogBuilder {
        this.args = args
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
     * 当Dialog为BottomSheet时，是否完全展开
     * @param fromBottom 是否完全展开
     * @return
     */
    fun setExpandBottomSheet(expand: Boolean): BaseDialogBuilder {
        mExpandBottomSheet = expand
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

    open fun getFragment(): BaseDialogFragment? {
        if (mDialogFragment == null) {
            build()
        }
        return mDialogFragment
    }

    open fun build(): BaseDialogBuilder {
        val args = prepareArguments()
        val fragment = Fragment.instantiate(mContext, mClass.name, args) as BaseDialogFragment
        args.putBoolean(ARG_CANCELABLE_ON_TOUCH_OUTSIDE, mCancelableOnTouchOutside)
        //显示底部
        args.putBoolean(ARG_SHOW_FROM_BOTTOM, mShowFromBottom)
        //完全展开BottomSheetDialog
        args.putBoolean(ARG_EXPAND_BOTTOM_SHEET, mExpandBottomSheet)
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

    /**
     * 报"IllegalStateException : Can not perform this action after onSaveInstanceState()"异常的时候使用此show
     */
    fun showAllowingStateLoss(fragmentManager: FragmentManager): BaseDialogFragment {
        if (mDialogFragment == null) {
            build()
        }
        mDialogFragment!!.showAllowingStateLoss(fragmentManager, mTag, mDismissPreDialog)
        return mDialogFragment!!
    }

    init {
        mTag = mClass.name
    }

    companion object {

        internal const val ARG_REQUEST_CODE = "request_code"
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
    }
}