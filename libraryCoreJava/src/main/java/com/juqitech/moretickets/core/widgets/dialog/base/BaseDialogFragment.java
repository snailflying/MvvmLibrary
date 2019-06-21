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

package com.juqitech.moretickets.core.widgets.dialog.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.juqitech.moretickets.core.widgets.dialog.iface.IButtonCancelDialogListener;
import com.juqitech.moretickets.core.widgets.dialog.iface.IButtonConfirmDialogListener;
import com.juqitech.moretickets.core.widgets.dialog.iface.IDialogCancelListener;
import com.juqitech.moretickets.core.widgets.dialog.iface.IDialogDismissListener;
import com.juqitech.moretickets.library.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.juqitech.moretickets.core.widgets.dialog.base.BaseDialogBuilder.*;

/**
 * @Author zhiqiang
 * @Email liuzhiqiang@moretickets.com
 * @Date 2019/2/16
 * @Description dialog的基类
 */
public abstract class BaseDialogFragment extends DialogFragment {

    protected int mRequestCode = DEFAULT_REQUEST_CODE;
    /**
     * 点击外部隐藏dialog，默认开启
     */
    private boolean canceledOnTouchOutside = DEFAULT_CANCELABLE_ON_TOUCH_OUTSIDE;
    /**
     * 灰度深浅
     */
    private float dimAmount = DEFAULT_DIM_AMOUNT;
    /**
     * 缩放
     */
    private double scale = DEFAULT_SCALE;
    /**
     * 是否全屏
     */
    private boolean fullScreen = DEFAULT_FULL_SCREEN;
    /**
     * 是否底部显示
     */
    private boolean showFromBottom = DEFAULT_SHOW_FROM_BOTTOM;
    /**
     * 主题
     */
    private int theme;
    /**
     * 动画
     */
    private int animStyle;


    protected Activity mContext;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Fragment targetFragment = getTargetFragment();
        if (targetFragment != null) {
            mRequestCode = getTargetRequestCode();
        } else {
            Bundle args = getArguments();
            if (args != null) {
                mRequestCode = args.getInt(BaseDialogBuilder.ARG_REQUEST_CODE, DEFAULT_REQUEST_CODE);
            }
        }
        //放在onCreateDialog()不起作用
        initDialogParams(getDialog());
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        for (IDialogDismissListener listener : getDismissListeners()) {
            listener.onDismissed(mRequestCode);
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        for (IDialogCancelListener listener : getCancelListeners()) {
            listener.onCancelled(mRequestCode);
        }
    }

    protected List<IDialogCancelListener> getCancelListeners() {
        return getDialogListeners(IDialogCancelListener.class);
    }

    protected List<IDialogDismissListener> getDismissListeners() {
        return getDialogListeners(IDialogDismissListener.class);
    }


    /**
     * positive 按钮事件，可能不止一个（activity嵌套fragment）
     *
     * @return Dialog listeners
     */
    protected List<IButtonConfirmDialogListener> getPositiveButtonDialogListeners() {
        return getDialogListeners(IButtonConfirmDialogListener.class);
    }

    /**
     * negative 按钮事件，可能不止一个（activity嵌套fragment）
     *
     * @return Dialog listeners
     */
    protected List<IButtonCancelDialogListener> getNegativeButtonDialogListeners() {
        return getDialogListeners(IButtonCancelDialogListener.class);
    }

    /**
     * 采用fragment interface pattern方式传递callback回调
     * targetFragment需要配合{@link BaseDialogBuilder#setTargetFragment(Fragment, int)}
     *
     * @param listenerInterface
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    protected <T> List<T> getDialogListeners(Class<T> listenerInterface) {
        final Fragment targetFragment = getTargetFragment();
        List<T> listeners = new ArrayList<T>(2);
        if (targetFragment != null && listenerInterface.isAssignableFrom(targetFragment.getClass())) {
            listeners.add((T) targetFragment);
        }
        if (getActivity() != null && listenerInterface.isAssignableFrom(getActivity().getClass())) {
            listeners.add((T) getActivity());
        }
        return Collections.unmodifiableList(listeners);
    }

    private void initDialogParams(Dialog dialog) {
        Bundle args = getArguments();
        if (args != null) {
            canceledOnTouchOutside = args.getBoolean(BaseDialogBuilder.ARG_CANCELABLE_ON_TOUCH_OUTSIDE, DEFAULT_CANCELABLE_ON_TOUCH_OUTSIDE);
            fullScreen = args.getBoolean(BaseDialogBuilder.ARG_FULL_SCREEN, DEFAULT_FULL_SCREEN);
            showFromBottom = args.getBoolean(BaseDialogBuilder.ARG_SHOW_FROM_BOTTOM, DEFAULT_SHOW_FROM_BOTTOM);
            dimAmount = args.getFloat(BaseDialogBuilder.ARG_DIM_AMOUNT, DEFAULT_DIM_AMOUNT);
            scale = args.getDouble(BaseDialogBuilder.ARG_SCALE, DEFAULT_SCALE);
            animStyle = args.getInt(BaseDialogBuilder.ARG_ANIM_STYLE);
            theme = args.getInt(BaseDialogBuilder.ARG_USE_THEME, getTheme());
        }
        setStyle(STYLE_NO_TITLE, theme);

        dialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            //调节灰色背景透明度[0-1]，默认0.5f
            lp.dimAmount = dimAmount;
            //是否在底部显示
            if (showFromBottom) {
                lp.gravity = Gravity.BOTTOM;
                if (animStyle == 0) {
                    animStyle = R.style.Dialog_Animation;
                }
            }
            //占用屏幕宽度一定比例
            if (fullScreen) {
                lp.width = getScreenWidth(mContext);
            } else {
                if (scale > 1) {
                    scale = 1;
                }
                lp.width = (int) (getScreenWidth(mContext) * scale);
            }
            //设置dialog高度
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            //设置dialog进入、退出的动画
            if (animStyle != 0) {
                window.setWindowAnimations(animStyle);
            }
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setAttributes(lp);
        }
    }

    private int getScreenWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    void showAllowingStateLoss(FragmentManager manager, String tag, Boolean dismissPreDialog) {
        FragmentTransaction ft = manager.beginTransaction();
        //将之前的dialog隐藏
        Fragment targetFragment = manager.findFragmentByTag(tag);
        if (dismissPreDialog && targetFragment instanceof BaseDialogFragment) {
            ft.remove(targetFragment);
        }

        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }


    void showWithDismissPreDialog(FragmentManager manager, String tag, Boolean dismissPreDialog) {
        FragmentTransaction ft = manager.beginTransaction();
        //将之前的dialog隐藏
        Fragment targetFragment = manager.findFragmentByTag(tag);
        if (dismissPreDialog && targetFragment instanceof BaseDialogFragment) {
            ft.remove(targetFragment).commit();
        }
        show(manager, tag);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }
}