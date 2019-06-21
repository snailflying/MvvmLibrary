package com.themone.core.widgets.dialog.base;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.io.Serializable;

/**
 * @Author zhiqiang
 * @Email liuzhiqiang@theone.com
 * @Date 2019/2/16
 */
public class BaseDialogBuilder {

    final static String ARG_REQUEST_CODE = "request_code";
    final static String ARG_FULL_SCREEN = "arg_full_screen";
    final static String ARG_SHOW_FROM_BOTTOM = "arg_show_from_bottom";
    final static String ARG_CANCELABLE_ON_TOUCH_OUTSIDE = "cancelable_oto";
    final static String ARG_USE_THEME = "arg_use_theme_type";
    final static String ARG_DIM_AMOUNT = "arg_dim_amount";
    final static String ARG_ANIM_STYLE = "arg_anim_style";
    final static String ARG_SCALE = "arg_scale";
    /**
     * 默认值
     */
    final static float DEFAULT_DIM_AMOUNT = 0.5f;
    final static double DEFAULT_SCALE = 0.75;
    final static boolean DEFAULT_CANCELABLE_ON_TOUCH_OUTSIDE = true;
    final static int DEFAULT_REQUEST_CODE = -42;
    final static boolean DEFAULT_FULL_SCREEN = false;
    final static boolean DEFAULT_SHOW_FROM_BOTTOM = false;

    protected final Context mContext;
    protected final FragmentManager mFragmentManager;
    private final Class<? extends BaseDialogFragment> mClass;
    private BaseDialogFragment mDialogFragment;
    private Fragment mTargetFragment;

    private String mTag;
    private int mRequestCode = DEFAULT_REQUEST_CODE;
    /**
     * 默认非全屏
     */
    private boolean mFullScreen = DEFAULT_FULL_SCREEN;
    /**
     * 是否底部显示
     */
    private boolean mShowFromBottom = DEFAULT_SHOW_FROM_BOTTOM;
    /**
     * 灰度深浅
     */
    private float mDimAmount = DEFAULT_DIM_AMOUNT;
    /**
     * 占用屏幕宽度一定比例
     */
    private double mScale = DEFAULT_SCALE;
    /**
     * 默认点击屏幕取消dialog
     */
    private boolean mCancelableOnTouchOutside = DEFAULT_CANCELABLE_ON_TOUCH_OUTSIDE;
    private boolean mCancelable = true;
    private Boolean mDismissPreDialog = true;
    /**
     * 主题
     */
    private int mTheme;
    /**
     * 动画
     */
    private int mAnimStyle;
    private Bundle args;

    public BaseDialogBuilder(Context context, FragmentManager fragmentManager, Class<? extends BaseDialogFragment> clazz) {
        mContext = context.getApplicationContext();
        mFragmentManager = fragmentManager;
        mClass = clazz;
        mTag = clazz.getSimpleName();
    }

    /**
     * 如果要增加额外传递参数的方法，需要重载此方法添加
     *
     * @return
     */
    protected Bundle prepareArguments() {
        if (args == null) {
            args = new Bundle();
        }
        return args;
    }

    /**
     * @param args 直接传Bundle
     * @return
     */
    public BaseDialogBuilder putBundle(Bundle args) {
        this.args = args;
        return this;
    }

    /**
     * @param key   Bundle传值key
     * @param value Bundle传值value
     * @return
     */
    public BaseDialogBuilder putArgs(String key, Serializable value) {
        prepareArguments().putSerializable(key, value);
        return this;
    }

    /**
     * @param cancelable 能否取消显示
     * @return
     */
    public BaseDialogBuilder setCancelable(boolean cancelable) {
        mCancelable = cancelable;
        return this;
    }

    /**
     * @param cancelable 点击屏幕取消dialog
     * @return
     */
    public BaseDialogBuilder setCancelableOnTouchOutside(boolean cancelable) {
        mCancelableOnTouchOutside = cancelable;
        if (cancelable) {
            mCancelable = true;
        }
        return this;
    }

    /**
     * @param fullScreen 是否全屏
     * @return
     */
    public BaseDialogBuilder setFullScreen(boolean fullScreen) {
        mFullScreen = fullScreen;
        return this;
    }

    /**
     * @param alpha 透明度
     * @return
     */
    public BaseDialogBuilder setBgAlpha(float alpha) {
        mDimAmount = alpha;
        return this;
    }

    /**
     * @param scale 占用屏幕宽度比例
     * @return
     */
    public BaseDialogBuilder setScale(double scale) {
        mScale = scale;
        return this;
    }

    /**
     * @param fromBottom 是否从底部弹起
     * @return
     */
    public BaseDialogBuilder setShowFromBottom(boolean fromBottom) {
        mShowFromBottom = fromBottom;
        return this;
    }

    /**
     * @param animStyle 动画样式
     * @return
     */
    public BaseDialogBuilder setAnimStyle(int animStyle) {
        mAnimStyle = animStyle;
        return this;
    }

    /**
     * 如果是从Fragment调用，则必须调用此方法
     * 否则{@link BaseDialogFragment#getDialogListeners(Class)}
     * 获取不到值，导致callback回调不起作用
     *
     * @param fragment
     * @param requestCode 返回值code
     * @return
     */
    public BaseDialogBuilder setTargetFragment(Fragment fragment, int requestCode) {
        mTargetFragment = fragment;
        mRequestCode = requestCode;
        return this;
    }

    /**
     * 如果设置了mTag则自动不会隐藏，否则可调用此方法不隐藏
     *
     * @param dismissPreDialog Boolean
     * @return DialogBuilder
     */
    public BaseDialogBuilder setDismissPreDialog(Boolean dismissPreDialog) {
        mDismissPreDialog = dismissPreDialog;
        return this;
    }

    /**
     * @param requestCode 返回值code
     * @return
     */
    public BaseDialogBuilder setRequestCode(int requestCode) {
        mRequestCode = requestCode;
        return this;
    }

    /**
     * @param tag fragment的tag
     * @return
     */
    public BaseDialogBuilder setTag(String tag) {
        mTag = tag;
        return this;
    }

    /**
     * @param theme 主题
     * @return
     */
    public BaseDialogBuilder setTheme(int theme) {
        mTheme = theme;
        return this;
    }

    public BaseDialogBuilder build() {
        final Bundle args = prepareArguments();
        final BaseDialogFragment fragment = (BaseDialogFragment) Fragment.instantiate(mContext, mClass.getName(), args);
        args.putBoolean(ARG_CANCELABLE_ON_TOUCH_OUTSIDE, mCancelableOnTouchOutside);
        //全屏
        args.putBoolean(ARG_FULL_SCREEN, mFullScreen);
        //显示底部
        args.putBoolean(ARG_SHOW_FROM_BOTTOM, mShowFromBottom);
        //设置主题
        args.putInt(ARG_USE_THEME, mTheme);
        //透明度
        args.putFloat(ARG_DIM_AMOUNT, mDimAmount);
        //动画
        args.putInt(ARG_ANIM_STYLE, mAnimStyle);
        //占用屏幕宽度一定比例
        args.putDouble(ARG_SCALE, mScale);

        if (mTargetFragment != null) {
            fragment.setTargetFragment(mTargetFragment, mRequestCode);
        } else {
            args.putInt(ARG_REQUEST_CODE, mRequestCode);
        }
        fragment.setCancelable(mCancelable);
        this.mDialogFragment = fragment;
        return this;
    }

    public BaseDialogFragment getFragment() {
        if (mDialogFragment == null) {
            build();
        }
        return mDialogFragment;
    }

    public BaseDialogFragment show() {
        if (mDialogFragment == null) {
            build();
        }
        mDialogFragment.showWithDismissPreDialog(mFragmentManager, mTag, mDismissPreDialog);
        return mDialogFragment;
    }

    /**
     * 报"IllegalStateException : Can not perform this action after onSaveInstanceState()"异常的时候使用此show
     */
    public BaseDialogFragment showAllowingStateLoss() {
        if (mDialogFragment == null) {
            build();
        }
        mDialogFragment.showAllowingStateLoss(mFragmentManager, mTag, mDismissPreDialog);
        return mDialogFragment;
    }
}