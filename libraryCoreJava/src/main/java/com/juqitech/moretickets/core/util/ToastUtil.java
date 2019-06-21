package com.juqitech.moretickets.core.util;

import android.content.Context;
import android.view.Gravity;
import androidx.annotation.StringRes;
import android.widget.Toast;

/**
 * @author zhiqiang
 * @date 2019-06-04
 * @desc
 */
public class ToastUtil {

    private static ToastUtil td;

    public static void showLong(Context context, @StringRes int resId) {
        showLong(context, context.getString(resId));
    }

    public static void showLong(Context context, String msg) {
        if (td == null) {
            td = new ToastUtil(context);
        }
        td.setText(msg);
        td.create().show();
    }

    public static void showShort(Context context, @StringRes int resId) {
        showShort(context, context.getString(resId));
    }

    public static void showShort(Context context, String msg) {
        if (td == null) {
            td = new ToastUtil(context);
        }
        td.setText(msg);
        td.createShort().show();
    }

    private Context context;
    private Toast toast;
    private String msg;

    private ToastUtil(Context context) {
        this.context = context.getApplicationContext();
    }

    private Toast create() {
        if (null == toast) {
            toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);

        }
        toast.setText(msg);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.setDuration(Toast.LENGTH_LONG);
        return toast;
    }

    private Toast createShort() {
        if (null == toast) {
            toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        }
        toast.setText(msg);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.setDuration(Toast.LENGTH_SHORT);
        return toast;
    }

    private void setText(String text) {
        msg = text;
    }

}
