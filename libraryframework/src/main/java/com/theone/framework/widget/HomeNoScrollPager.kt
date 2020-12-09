package com.theone.framework.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * @Author ZhiQiang
 * @Date 2020-01-17
 * @Description 不可滑动的viewpager
 */
class HomeNoScrollPager : ViewPager {
    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {}

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return false
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return false
    }

    //去除页面切换时的滑动翻页效果
    override fun setCurrentItem(item: Int) {
        super.setCurrentItem(item, false)
    }
}