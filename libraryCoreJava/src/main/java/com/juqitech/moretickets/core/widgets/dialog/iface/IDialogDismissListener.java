package com.juqitech.moretickets.core.widgets.dialog.iface;

/**
 * @Author zhiqiang
 * @Email liuzhiqiang@moretickets.com
 * @Date 2019/2/26
 * @Description 按返回键或者点击Dialog之外的屏幕范围
 */
public interface IDialogDismissListener {
    void onDismissed(int requestCode);
}