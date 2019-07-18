package com.theone.framework.widget.toast.style

import android.view.Gravity

/**
 * @Author zhiqiang
 * @Email liuzhiqiang@theone.com
 * @Date 2019-06-22
 * @Description 默认黑色样式实现
 */
class ToastBlackStyle : IToastStyle {

    override val gravity: Int
        get() = Gravity.CENTER

    override val xOffset: Int
        get() = 0

    override val yOffset: Int
        get() = 0

    override val z: Int
        get() = 30

    override val cornerRadius: Int
        get() = 6

    override val backgroundColor: Int
        get() = -0x78000000

    override val textColor: Int
        get() = -0x11000001

    override val textSize: Float
        get() = 14f

    override val maxLines: Int
        get() = 3

    override val paddingLeft: Int
        get() = 24

    override val paddingTop: Int
        get() = 16

    override val paddingRight: Int
        get() = paddingLeft

    override val paddingBottom: Int
        get() = paddingTop
}