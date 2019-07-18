package com.theone.framework.widget.toast.style

/**
 * @Author zhiqiang
 * @Email liuzhiqiang@theone.com
 * @Date 2019-06-22
 * @Description 默认样式接口
 */
interface IToastStyle {

    val gravity: Int // 吐司的重心
    val xOffset: Int // X轴偏移
    val yOffset: Int // Y轴偏移
    val z: Int // 吐司Z轴坐标

    val cornerRadius: Int // 圆角大小
    val backgroundColor: Int // 背景颜色

    val textColor: Int // 文本颜色
    val textSize: Float // 文本大小
    val maxLines: Int // 最大行数

    val paddingLeft: Int // 左边内边距
    val paddingTop: Int // 顶部内边距
    val paddingRight: Int // 右边内边距
    val paddingBottom: Int // 底部内边距
}