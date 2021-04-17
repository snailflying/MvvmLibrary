package com.theone.framework.widget.dialog.iface

/**
 * @Author zhiqiang
 * @Email liuzhiqiang@theone.com
 * @Date 2019/2/26
 * @Description 按返回键或者点击Dialog之外的屏幕范围
 */
interface IDialogDismissListener {
    fun onDismissed(requestCode: Int)
}