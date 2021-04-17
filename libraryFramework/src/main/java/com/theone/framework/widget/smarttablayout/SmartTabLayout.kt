/**
 * Copyright (C) 2015 ogaclejapan
 * Copyright (C) 2013 The Android Open Source Project
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.theone.framework.widget.smarttablayout

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.database.DataSetObserver
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.*
import com.theone.framework.R
import com.theone.framework.widget.smarttablayout.SmartTabLayout.TabColorizer

/**
 * To be used with ViewPager to provide a tab indicator component which give constant feedback as
 * to
 * the user's scroll progress.
 *
 *
 * To use the component, simply add it to your view hierarchy. Then in your
 * [android.app.Activity] or [android.app.Fragment], [ ] call
 * [.setViewPager] providing it the ViewPager this layout
 * is being used for.
 *
 *
 * The colors can be customized in two ways. The first and simplest is to provide an array of
 * colors
 * via [.setSelectedIndicatorColors] and [.setDividerColors]. The
 * alternative is via the [TabColorizer] interface which provides you complete control over
 * which color is used for any individual position.
 *
 *
 * The views used as tabs can be customized by calling [.setCustomTabView],
 * providing the layout ID of your custom layout.
 *
 *
 * Forked from Google Samples &gt; SlidingTabsBasic &gt;
 * 将原SmartTabLayout与ViewPager解耦
 * [SlidingTabLayout](https://github.com/ogaclejapan/SmartTabLayout.git)
 */
class SmartTabLayout<VH : SmartTabAdapter.Holder, T : SmartTabAdapter<VH>> @JvmOverloads constructor(
    context: Context,
    val attrs: AttributeSet? = null,
    defStyle: Int = 0
) : HorizontalScrollView(context, attrs, defStyle) {
    private val tabStrip: SmartTabStrip
    private val titleOffset: Int
    private val tabViewBackgroundResId: Int
    private val tabViewTextAllCaps: Boolean
    private var tabViewTextColors: ColorStateList
    private val tabViewTextSize: Float
    private val tabViewTextHorizontalPadding: Int
    private val tabViewTextMinWidth: Int
    private val tabViewTextViewId: Int
    private val tabViewImageViewId: Int

    /**
     * 第一个按钮距离左边距离，滑动时可以超出
     */
    private var firstItemMarginLeft: Int = 0

    /**
     * 第一个按钮距离右边距离，滑动时可以超出
     */
    private var lastItemMarginRight: Int = 0

    //    private var viewPager: ViewPager? = null
//    private var viewPagerPageChangeListener: OnPageChangeListener? = null
    private var onScrollChangeListener: OnScrollChangeListener? = null
    private val internalTabClickListener: InternalTabClickListener?
    private var onTabClickListener: OnTabClickListener? = null
    private var distributeEvenly: Boolean
    private var smartTabAdapter: T? = null
    private var isTabAdapterChanged = false

    private var smartTabAdapterObserver: SmartTabAdapterObserver? = null

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.stl_SmartTabLayout)
        firstItemMarginLeft = a.getDimensionPixelSize(R.styleable.stl_SmartTabLayout_stl_firstItemMarginLeft, 0)
        lastItemMarginRight = a.getDimensionPixelSize(R.styleable.stl_SmartTabLayout_stl_lastItemMarginRight, 0)
        a.recycle()
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        if (onScrollChangeListener != null) {
            onScrollChangeListener!!.onScrollChanged(l, oldl)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (tabStrip.isIndicatorAlwaysInCenter && tabStrip.childCount > 0) {
            val firstTab = tabStrip.getChildAt(0)
            val lastTab = tabStrip.getChildAt(tabStrip.childCount - 1)
            val start = (w - Utils.getMeasuredWidth(firstTab)) / 2 - Utils.getMarginStart(firstTab)
            val end = (w - Utils.getMeasuredWidth(lastTab)) / 2 - Utils.getMarginEnd(lastTab)
            tabStrip.minimumWidth = tabStrip.measuredWidth
            ViewCompat.setPaddingRelative(this, start, paddingTop, end, paddingBottom)
            clipToPadding = false
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        // Ensure first scroll
        if (isTabAdapterChanged && smartTabAdapter != null) {
            isTabAdapterChanged = false
            scrollToTab(smartTabAdapter!!.currTabIndex)
        }
    }

    /**
     * Set the behavior of the Indicator scrolling feedback.
     *
     * @param interpolator [SmartTabIndicationInterpolator]
     */
    fun setIndicationInterpolator(interpolator: SmartTabIndicationInterpolator) {
        tabStrip.setIndicationInterpolator(interpolator)
    }

    /**
     * Set the custom [TabColorizer] to be used.
     *
     *
     * If you only require simple customisation then you can use
     * [.setSelectedIndicatorColors] and [.setDividerColors] to achieve
     * similar effects.
     */
    fun setCustomTabColorizer(tabColorizer: TabColorizer?) {
        tabStrip.setCustomTabColorizer(tabColorizer)
    }

    /**
     * Set the color used for styling the tab text. This will need to be called prior to calling
     * [.setViewPager] otherwise it will not get set
     *
     * @param color to use for tab text
     */
    fun setDefaultTabTextColor(color: Int) {
        tabViewTextColors = ColorStateList.valueOf(color)
    }

    /**
     * Sets the colors used for styling the tab text. This will need to be called prior to calling
     * [.setViewPager] otherwise it will not get set
     *
     * @param colors ColorStateList to use for tab text
     */
    fun setDefaultTabTextColor(colors: ColorStateList) {
        tabViewTextColors = colors
    }

    fun setTabTextColorBothIndicatorColor(colors: IntArray, indicatorColors: IntArray) {
        if (colors.size < 2) {
            throw IllegalArgumentException("length 不可小于 2")
        }
        tabStrip.setTabTextColorBothIndicatorColor(colors, indicatorColors)
        smartTabAdapterObserver?.onInvalidated()
    }

    /**
     * Set the same weight for tab
     */
    fun setDistributeEvenly(distributeEvenly: Boolean) {
        this.distributeEvenly = distributeEvenly
    }

    /**
     * Sets the colors to be used for indicating the selected tab. These colors are treated as a
     * circular array. Providing one color will mean that all tabs are indicated with the same color.
     */
    fun setSelectedIndicatorColors(vararg colors: Int) {
        tabStrip.setSelectedIndicatorColors(*colors)
    }

    /**
     * Sets the colors to be used for tab dividers. These colors are treated as a circular array.
     * Providing one color will mean that all tabs are indicated with the same color.
     */
    fun setDividerColors(vararg colors: Int) {
        tabStrip.setDividerColors(*colors)
    }

    /**
     * Set [OnScrollChangeListener] for obtaining values of scrolling.
     *
     * @param listener the [OnScrollChangeListener] to set
     */
    fun setOnScrollChangeListener(listener: OnScrollChangeListener?) {
        onScrollChangeListener = listener
    }

    /**
     * Set [OnTabClickListener] for obtaining click event.
     *
     * @param listener the [OnTabClickListener] to set
     */
    fun setOnTabClickListener(listener: OnTabClickListener?) {
        onTabClickListener = listener
    }

    /**
     * Sets the associated SmartTabAdapter. Note that the assumption here is that the pager content
     * (number of tabs and tab titles) does not change after this call has been made.
     */
    fun setTabAdapter(adapter: T?, addObserver: Boolean = false) {
        if (null == adapter) {
            return
        }
        //处理监听
        if (smartTabAdapterObserver != null) {
            adapter.unregisterDataSetObserver(smartTabAdapterObserver!!)
        }
        if (addObserver) {
            if (smartTabAdapterObserver == null) {
                smartTabAdapterObserver = SmartTabAdapterObserver()
            }
            adapter.registerDataSetObserver(smartTabAdapterObserver!!)
        }
        //设置Adapter
        smartTabAdapter = adapter
        isTabAdapterChanged = true
        populateTabStrip(adapter)
        scrollToSmartTab(adapter)
    }

    private fun scrollToSmartTab(adapter: T) {
        val currIndex = adapter.currTabIndex
        val count = adapter.getCount()
        if (currIndex >= count || currIndex < 0) {
            return
        }
        for (i in 0 until count) {
            setTabStrip(tabStrip.getChildAt(i), currIndex == i)
        }
        tabStrip.onViewPagerPageChanged(currIndex, 0f)
        scrollToTab(currIndex)
    }

    /**
     * Returns the view at the specified position in the tabs.
     *
     * @param position the position at which to get the view from
     * @return the view at the specified position or null if the position does not exist within the
     * tabs
     */
    fun getTabAt(position: Int): View {
        return tabStrip.getChildAt(position)
    }

    /**
     * Create a default view to be used for tabs. This is called if a custom tab view is not set via
     * [.setCustomTabView].
     */
    @Deprecated("废弃")
    protected fun createDefaultTabView(title: CharSequence?): TextView {
        val textView = TextView(context)
        textView.gravity = Gravity.CENTER
        textView.text = title
        textView.setTextColor(tabViewTextColors)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabViewTextSize)
        textView.typeface = Typeface.DEFAULT_BOLD
        textView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT
        )
        if (tabViewBackgroundResId != View.NO_ID) {
            textView.setBackgroundResource(tabViewBackgroundResId)
        } else { // If we're running on Honeycomb or newer, then we can use the Theme's
// selectableItemBackground to ensure that the View has a pressed state
            val outValue = TypedValue()
            context.theme.resolveAttribute(
                R.attr.selectableItemBackground,
                outValue, true
            )
            textView.setBackgroundResource(outValue.resourceId)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) { // If we're running on ICS or newer, enable all-caps to match the Action Bar tab style
            textView.isAllCaps = tabViewTextAllCaps
        }
        textView.setPadding(
            tabViewTextHorizontalPadding, 0,
            tabViewTextHorizontalPadding, 0
        )
        if (tabViewTextMinWidth > 0) {
            textView.minWidth = tabViewTextMinWidth
        }
        return textView
    }


    private fun setTabStrip(tabView: View, isSelected: Boolean) {
        //自定义 provider，重写 tabView 的 setSelect 方法
        tabView.isSelected = isSelected

        val customText: View = if (tabViewTextViewId != View.NO_ID) {
            tabView.findViewById(tabViewTextViewId)
        } else {
            tabView
        }
//        tabStrip.setTitleTextStyle(customText, isSelected)
        //自定义 View
/*        val customImg: ImageView
        if (tabViewImageViewId != View.NO_ID) {
            customImg = tabView.findViewById(tabViewImageViewId)
            tabStrip.setTitleImageStyle(customImg, isSelected)
        }*/
    }

    /**
     * 添加Item
     * @param tabAdapter SmartTabAdapter
     */
    private fun populateTabStrip(tabAdapter: T) {
        tabStrip.removeAllViews()
        for (i in 0 until tabAdapter.getCount()) {
            val holder = tabAdapter.onCreateViewHolder(tabStrip)
            val tabView = holder.itemView
            if (distributeEvenly) {
                val lp = tabView.layoutParams as LinearLayout.LayoutParams
                lp.width = 0
                lp.weight = 1f
            }
            if (internalTabClickListener != null) {
                tabView.setOnClickListener(internalTabClickListener)
            }
            if (firstItemMarginLeft != 0 && i == 0) {
                val param =
                    LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                param.setMargins(
                    firstItemMarginLeft,
                    tabView.marginTop,
                    tabView.marginRight,
                    tabView.marginBottom
                )
                tabStrip.addView(tabView, param)
            } else if (lastItemMarginRight != 0 && i == tabAdapter.getCount() - 1) {
                val param =
                    LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                param.setMargins(
                    tabView.marginLeft,
                    tabView.marginTop,
                    lastItemMarginRight,
                    tabView.marginBottom
                )
                tabStrip.addView(tabView, param)
            } else {
                tabStrip.addView(tabView)
            }
            tabAdapter.onBindViewHolder(holder, i)
//            setTabStrip(tabView, i == tabAdapter.currTabIndex)
        }
    }

    /**
     * 第一个按钮距离左边距离，滑动时可以超出
     */
    fun setFirstItemMarginLeft(left: Int) {
        firstItemMarginLeft = dp2px(left).toInt()
    }

    /**
     * 第一个按钮距离右边距离，滑动时可以超出
     */
    fun setLastItemMarginRight(right: Int) {
        lastItemMarginRight = dp2px(right).toInt()
    }

    private fun dp2px(dp: Int): Float {
        val scale = context.resources.displayMetrics.density
        return dp * scale + 0.5f
    }

    /**
     * 滚动到当前Item
     * @param tabAdapter SmartTabAdapter
     */
    private fun scrollToTab(tabIndex: Int, positionOffset: Float) {
        val tabStripChildCount = tabStrip.childCount
        if (tabStripChildCount == 0 || tabIndex < 0 || tabIndex >= tabStripChildCount) {
            return
        }
        val isLayoutRtl = Utils.isLayoutRtl(this)
        val selectedTab = tabStrip.getChildAt(tabIndex)
        val widthPlusMargin = Utils.getWidth(selectedTab) + Utils.getMarginHorizontally(selectedTab)
        var extraOffset = (positionOffset * widthPlusMargin).toInt()
        if (tabStrip.isIndicatorAlwaysInCenter) {
            if (0f < positionOffset && positionOffset < 1f) {
                val nextTab = tabStrip.getChildAt(tabIndex + 1)
                val selectHalfWidth = Utils.getWidth(selectedTab) / 2 + Utils.getMarginEnd(selectedTab)
                val nextHalfWidth = Utils.getWidth(nextTab) / 2 + Utils.getMarginStart(nextTab)
                extraOffset = Math.round(positionOffset * (selectHalfWidth + nextHalfWidth))
            }
            val firstTab = tabStrip.getChildAt(0)
            var x: Int
            if (isLayoutRtl) {
                val first = Utils.getWidth(firstTab) + Utils.getMarginEnd(firstTab)
                val selected = Utils.getWidth(selectedTab) + Utils.getMarginEnd(selectedTab)
                x = Utils.getEnd(selectedTab) - Utils.getMarginEnd(selectedTab) - extraOffset
                x -= (first - selected) / 2
            } else {
                val first = Utils.getWidth(firstTab) + Utils.getMarginStart(firstTab)
                val selected = Utils.getWidth(selectedTab) + Utils.getMarginStart(selectedTab)
                x = Utils.getStart(selectedTab) - Utils.getMarginStart(selectedTab) + extraOffset
                x -= (first - selected) / 2
            }
            scrollTo(x, 0)
            return
        }
        var x: Int
        if (titleOffset == TITLE_OFFSET_AUTO_CENTER) {
            if (0f < positionOffset && positionOffset < 1f) {
                val nextTab = tabStrip.getChildAt(tabIndex + 1)
                val selectHalfWidth = Utils.getWidth(selectedTab) / 2 + Utils.getMarginEnd(selectedTab)
                val nextHalfWidth = Utils.getWidth(nextTab) / 2 + Utils.getMarginStart(nextTab)
                extraOffset = Math.round(positionOffset * (selectHalfWidth + nextHalfWidth))
            }
            if (isLayoutRtl) {
                x = -Utils.getWidthWithMargin(selectedTab) / 2 + width / 2
                x -= Utils.getPaddingStart(this)
            } else {
                x = Utils.getWidthWithMargin(selectedTab) / 2 - width / 2
                x += Utils.getPaddingStart(this)
            }
        } else {
            x = if (isLayoutRtl) {
                if (tabIndex > 0 || positionOffset > 0) titleOffset else 0
            } else {
                if (tabIndex > 0 || positionOffset > 0) -titleOffset else 0
            }
        }
        val start = Utils.getStart(selectedTab)
        val startMargin = Utils.getMarginStart(selectedTab)
        x += if (isLayoutRtl) {
            start + startMargin - extraOffset - width + Utils.getPaddingHorizontally(this)
        } else {
            start - startMargin + extraOffset
        }
        scrollTo(x, 0)
    }

    /**
     * Allows complete control over the colors drawn in the tab layout. Set with
     * [.setCustomTabColorizer].
     */
    interface TabColorizer {
        /**
         * @return return the color of the indicator used when `position` is selected.
         */
        fun getIndicatorColor(position: Int): Int

        /**
         * @return return the color of the divider drawn to the right of `position`.
         */
        fun getDividerColor(position: Int): Int
    }

    /**
     * Interface definition for a callback to be invoked when the scroll position of a view changes.
     */
    interface OnScrollChangeListener {
        /**
         * Called when the scroll position of a view changes.
         *
         * @param scrollX    Current horizontal scroll origin.
         * @param oldScrollX Previous horizontal scroll origin.
         */
        fun onScrollChanged(scrollX: Int, oldScrollX: Int)
    }

    /**
     * Interface definition for a callback to be invoked when a tab is clicked.
     */
    interface OnTabClickListener {
        /**
         * Called when a tab is clicked.
         *
         * @param position tab's position
         */
        fun onTabClicked(position: Int)
    }

    private inner class InternalTabClickListener : OnClickListener {
        override fun onClick(v: View) {
            if (smartTabAdapter == null) {
                return
            }
            for (i in 0 until tabStrip.childCount) {
                if (v !== tabStrip.getChildAt(i)) {
                    setTabStrip(tabStrip.getChildAt(i), false)
                    continue
                }
                setTabStrip(tabStrip.getChildAt(i), true)
                if (i != smartTabAdapter!!.currTabIndex) {
                    smartTabAdapter!!.currTabIndex = i
                    tabStrip.onViewPagerPageChanged(i, 0f)
                    scrollToTab(i)
                    if (onTabClickListener != null) {
                        onTabClickListener!!.onTabClicked(i)
                    }
                }
            }
        }
    }

/*
    */
    /**
     * viewpager等控件滚动时相应处理
     * @param position Int
     * @param positionOffset Float
     *//*

    fun onPageScrolled(position: Int, positionOffset: Float) {
        val tabStripChildCount = tabStrip.childCount
        if (tabStripChildCount == 0 || position < 0 || position >= tabStripChildCount) {
            return
        }
        tabStrip.onViewPagerPageChanged(position, positionOffset)
        scrollToTab(position, positionOffset)
    }
*/

    private var selectedPosition: Int = 0
    private var lastPosition = 0

    private fun scrollToTab(tabIndex: Int) {
        val tabStripChildCount = tabStrip.childCount
        if (tabIndex < 0 || tabIndex >= tabStripChildCount) {
            return
        }
        selectedPosition = tabIndex
        if (lastPosition == selectedPosition) {
            return
        }
        //模拟 viewPager.onPageScrolled 监听 position 和 positionOffset 状态
        //左滑动 position ->0-1,positionOffset-> 0-0.5-0.9-0,其中 position 改变为1时 positionOffset 变为0
        //右滑动 position ->preIndex,positionOffset-> 0.9-0,其中 position 为前一个position, positionOffset 从1变为0
        val start: Float
        val end: Float
        val stripPosition: Int
        if (selectedPosition > lastPosition) {
            start = 0f
            end = 1 - start
            stripPosition = lastPosition
        } else {
            end = 0f
            start = 1f - end
            stripPosition = selectedPosition
        }
        val valueAnimator = ValueAnimator.ofFloat(start, end)
        valueAnimator.addUpdateListener { animation ->
            tabStrip.onViewPagerPageChanged(stripPosition, (animation.animatedValue as Float))
            scrollToTab(stripPosition, (animation.animatedValue as Float))
        }
        valueAnimator.addListener(object : Listener() {
            override fun onAnimationEnd(animation: Animator) {
                lastPosition = selectedPosition
                tabStrip.onViewPagerPageChanged(lastPosition, 0.toFloat())
                scrollToTab(lastPosition, 0f)
            }
        })
        valueAnimator.duration = 200
        valueAnimator.start()
    }

    open class Listener : AnimatorListenerAdapter()

    /**
     * 设置当前页面
     * @param position Int
     */
    fun setCurrentItem(position: Int) {
        if (smartTabAdapter == null) return

        var i = 0
        val size = tabStrip.childCount
        while (i < size) {
            setTabStrip(tabStrip.getChildAt(i), position == i)
            i++
        }
        if (position != smartTabAdapter?.currTabIndex) {
            smartTabAdapter?.currTabIndex = position
            tabStrip.onViewPagerPageChanged(position, 0f)
            scrollToTab(position)
            onTabClickListener?.onTabClicked(position)
        }
    }

    private inner class SmartTabAdapterObserver : DataSetObserver() {
        override fun onChanged() {
            if (null != smartTabAdapter) {
                populateTabStrip(smartTabAdapter!!)
                scrollToSmartTab(smartTabAdapter!!)
            }
        }

        override fun onInvalidated() {
            if (null != smartTabAdapter) {
                scrollToSmartTab(smartTabAdapter!!)
            }
        }
    }

    companion object {
        private const val DEFAULT_DISTRIBUTE_EVENLY = false
        private const val TITLE_OFFSET_DIPS = 24
        private const val TITLE_OFFSET_AUTO_CENTER = -1
        private const val TAB_VIEW_PADDING_DIPS = 16
        private const val TAB_VIEW_TEXT_ALL_CAPS = true
        private const val TAB_VIEW_TEXT_SIZE_SP = 12
        private const val TAB_VIEW_TEXT_COLOR = -0x4000000
        private const val TAB_VIEW_TEXT_MIN_WIDTH = 0
        private const val TAB_CLICKABLE = true
    }

    init {
        // Disable the Scroll Bar
        isHorizontalScrollBarEnabled = false
        val dm = resources.displayMetrics
        val density = dm.density
        var tabBackgroundResId = View.NO_ID
        var textAllCaps = TAB_VIEW_TEXT_ALL_CAPS
        val textColors: ColorStateList?
        var textSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, TAB_VIEW_TEXT_SIZE_SP.toFloat(), dm
        )
        var textHorizontalPadding = (TAB_VIEW_PADDING_DIPS * density).toInt()
        var textMinWidth = (TAB_VIEW_TEXT_MIN_WIDTH * density).toInt()
        var distributeEvenly = DEFAULT_DISTRIBUTE_EVENLY
        var customTabTextViewId = View.NO_ID
        var customTabImageViewId = View.NO_ID
        var clickable = TAB_CLICKABLE
        var titleOffset = (TITLE_OFFSET_DIPS * density).toInt()
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.stl_SmartTabLayout, defStyle, 0
        )
        tabBackgroundResId = a.getResourceId(
            R.styleable.stl_SmartTabLayout_stl_defaultTabBackground, tabBackgroundResId
        )
        textAllCaps = a.getBoolean(
            R.styleable.stl_SmartTabLayout_stl_defaultTabTextAllCaps, textAllCaps
        )
        textColors = a.getColorStateList(
            R.styleable.stl_SmartTabLayout_stl_defaultTabTextColor
        )
        textSize = a.getDimension(
            R.styleable.stl_SmartTabLayout_stl_defaultTabTextSize, textSize
        )
        textHorizontalPadding = a.getDimensionPixelSize(
            R.styleable.stl_SmartTabLayout_stl_defaultTabTextHorizontalPadding, textHorizontalPadding
        )
        textMinWidth = a.getDimensionPixelSize(
            R.styleable.stl_SmartTabLayout_stl_defaultTabTextMinWidth, textMinWidth
        )
        customTabTextViewId = a.getResourceId(
            R.styleable.stl_SmartTabLayout_stl_customTabTextViewId, customTabTextViewId
        )
        customTabImageViewId = a.getResourceId(
            R.styleable.stl_SmartTabLayout_stl_customTabImageViewId, customTabImageViewId
        )
        distributeEvenly = a.getBoolean(
            R.styleable.stl_SmartTabLayout_stl_distributeEvenly, distributeEvenly
        )
        clickable = a.getBoolean(
            R.styleable.stl_SmartTabLayout_stl_clickable, clickable
        )
        titleOffset = a.getLayoutDimension(
            R.styleable.stl_SmartTabLayout_stl_titleOffset, titleOffset
        )
        a.recycle()
        this.titleOffset = titleOffset
        tabViewBackgroundResId = tabBackgroundResId
        tabViewTextAllCaps = textAllCaps
        tabViewTextColors = textColors ?: ColorStateList.valueOf(TAB_VIEW_TEXT_COLOR)
        tabViewTextSize = textSize
        tabViewTextHorizontalPadding = textHorizontalPadding
        tabViewTextMinWidth = textMinWidth
        internalTabClickListener = if (clickable) InternalTabClickListener() else null
        this.distributeEvenly = distributeEvenly
        tabViewTextViewId = customTabTextViewId
        tabViewImageViewId = customTabImageViewId
        tabStrip = SmartTabStrip(context, attrs)
        if (distributeEvenly && tabStrip.isIndicatorAlwaysInCenter) {
            throw UnsupportedOperationException(
                "'distributeEvenly' and 'indicatorAlwaysInCenter' both use does not support"
            )
        }
        // Make sure that the Tab Strips fills this View
        isFillViewport = !tabStrip.isIndicatorAlwaysInCenter
        addView(tabStrip, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }
}