package com.theone.framework.widget.banner

import android.content.Context
import android.graphics.Outline
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2

/**
 * @Author ZhiQiang
 * @Date 2020/5/27
 * @Description 利用viewpager2实现的Banner
 * Git：https://github.com/zguop/banner.git
 */
class Banner @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {
    private var changeCallback: ViewPager2.OnPageChangeCallback? = null
    private var compositePageTransformer: CompositePageTransformer? = null
    private var bannerAdapterWrapper: BannerAdapterWrapper? = null
    private var holderRestLoader: HolderRestLoader? = null
    var viewPager2: ViewPager2? = null
        private set
    private var indicator: Indicator? = null
    private var isAutoPlay = true
    private var autoTurningTime = DEFAULT_AUTO_TIME
    private var pagerScrollDuration = DEFAULT_PAGER_DURATION
    private var lastX = 0f
    private var lastY = 0f
    private var startX = 0f
    private var startY = 0f
    private val scaledTouchSlop: Int
    private var currentPage = 0
    private var realCount = 0
    private var needCount = 0
    private var sidePage = 0
    private var needPage = NORMAL_COUNT
    private fun initViews(context: Context) {
        viewPager2 = ViewPager2(context)
        viewPager2!!.layoutParams = ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        viewPager2!!.setPageTransformer(CompositePageTransformer().also { compositePageTransformer = it })
        bannerAdapterWrapper = BannerAdapterWrapper()
        viewPager2!!.registerOnPageChangeCallback(OnPageChangeCallback())
        initViewPagerScrollProxy()
        addView(viewPager2)
    }

    private fun startPager(startPosition: Int) {
        val adapter = viewPager2!!.adapter
        if (adapter == null || sidePage == NORMAL_COUNT) {
            viewPager2!!.adapter = bannerAdapterWrapper
        } else {
            adapter.notifyDataSetChanged()
        }
        currentPage = startPosition + sidePage
        viewPager2!!.isUserInputEnabled = realCount > 1
        viewPager2!!.setCurrentItem(currentPage, false)
        if (indicator != null) {
            indicator!!.initIndicatorCount(realCount)
        }
        if (isAutoPlay()) {
            startTurning()
        }
    }

    private fun initPagerCount() {
        val adapter = bannerAdapterWrapper!!.adapter
        if (adapter == null || adapter.itemCount == 0) {
            realCount = 0
            needCount = 0
        } else {
            realCount = adapter.itemCount
            needCount = realCount + needPage
        }
        sidePage = needPage / NORMAL_COUNT
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isAutoPlay()) {
            startTurning()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (isAutoPlay()) {
            stopTurning()
        }
    }

    private val task: Runnable = object : Runnable {
        override fun run() {
            if (isAutoPlay()) {
                currentPage++
                if (currentPage == realCount + sidePage + 1) {
                    viewPager2!!.setCurrentItem(sidePage, false)
                    post(this)
                } else {
                    viewPager2!!.currentItem = currentPage
                    postDelayed(this, autoTurningTime)
                }
            }
        }
    }

    private fun toRealPosition(position: Int): Int {
        var realPosition = 0
        if (realCount != 0) {
            realPosition = (position - sidePage) % realCount
        }
        if (realPosition < 0) {
            realPosition += realCount
        }
        return realPosition
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (isAutoPlay() && viewPager2!!.isUserInputEnabled) {
            val action = ev.action
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_OUTSIDE) {
                startTurning()
            } else if (action == MotionEvent.ACTION_DOWN) {
                stopTurning()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.action
        if (action == MotionEvent.ACTION_DOWN) {
            lastX = ev.rawX
            startX = lastX
            lastY = ev.rawY
            startY = lastY
        } else if (action == MotionEvent.ACTION_MOVE) {
            lastX = ev.rawX
            lastY = ev.rawY
            if (viewPager2!!.isUserInputEnabled) {
                val distanceX = Math.abs(lastX - startX)
                val distanceY = Math.abs(lastY - startY)
                val disallowIntercept: Boolean
                disallowIntercept = if (viewPager2!!.orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
                    distanceX > scaledTouchSlop && distanceX > distanceY
                } else {
                    distanceY > scaledTouchSlop && distanceY > distanceX
                }
                parent.requestDisallowInterceptTouchEvent(disallowIntercept)
            }
        } else if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            return Math.abs(lastX - startX) > scaledTouchSlop || Math.abs(lastY - startY) > scaledTouchSlop
        }
        return super.onInterceptTouchEvent(ev)
    }

    private inner class OnPageChangeCallback : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            val realPosition = toRealPosition(position)
            if (changeCallback != null) {
                changeCallback!!.onPageScrolled(realPosition, positionOffset, positionOffsetPixels)
            }
            if (indicator != null) {
                indicator!!.onPageScrolled(realPosition, positionOffset, positionOffsetPixels)
            }
        }

        override fun onPageSelected(position: Int) {
            val resetItem = currentPage == sidePage - 1 || currentPage == needCount - (sidePage - 1) || position != currentPage && needCount - currentPage == sidePage
            currentPage = position
            val realPosition = toRealPosition(position)
            if (holderRestLoader != null) {
                holderRestLoader!!.onItemRestLoader(realPosition, resetItem)
            }
            if (resetItem) {
                return
            }
            if (changeCallback != null) {
                changeCallback!!.onPageSelected(realPosition)
            }
            if (indicator != null) {
                indicator!!.onPageSelected(realPosition)
            }
        }

        override fun onPageScrollStateChanged(state: Int) {
            if (changeCallback != null) {
                changeCallback!!.onPageScrollStateChanged(state)
            }
            if (indicator != null) {
                indicator!!.onPageScrollStateChanged(state)
            }
            if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                if (currentPage == sidePage - 1) {
                    viewPager2!!.setCurrentItem(realCount + currentPage, false)
                } else if (currentPage == needCount - sidePage) {
                    viewPager2!!.setCurrentItem(sidePage, false)
                }
            }
        }
    }

    private inner class BannerAdapterWrapper : Adapter<ViewHolder>() {
        var adapter: Adapter<ViewHolder>? = null
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return adapter!!.onCreateViewHolder(parent, viewType)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            adapter!!.onBindViewHolder(holder, toRealPosition(position))
        }

        override fun getItemViewType(position: Int): Int {
            return adapter!!.getItemViewType(toRealPosition(position))
        }

        override fun getItemCount(): Int {
            return if (realCount > 1) needCount else realCount
        }

        fun registerAdapter(adapter: Adapter<ViewHolder>?) {
            if (this.adapter != null) {
                this.adapter!!.unregisterAdapterDataObserver(itemDataSetChangeObserver)
            }
            this.adapter = adapter
            if (this.adapter != null) {
                this.adapter!!.registerAdapterDataObserver(itemDataSetChangeObserver)
            }
        }
    }

    private val itemDataSetChangeObserver: AdapterDataObserver = object : AdapterDataObserver() {
        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            onChanged()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            onChanged()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            onChanged()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            onChanged()
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            onChanged()
        }

        override fun onChanged() {
            if (viewPager2 != null && bannerAdapterWrapper != null) {
                initPagerCount()
                startPager(currentPager)
            }
        }
    }

    private fun initViewPagerScrollProxy() {
        try { //控制切换速度，采用反射方。法方法只会调用一次，替换掉内部的RecyclerView的LinearLayoutManager
            val recyclerView = viewPager2!!.getChildAt(0) as RecyclerView
            recyclerView.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            val o = recyclerView.layoutManager as LinearLayoutManager?
            val proxyLayoutManger = ProxyLayoutManger(context, o)
            //设置代理ProxyLayoutManger，这时候o上mRecyclerView会被置空。
            recyclerView.layoutManager = proxyLayoutManger
            //由于设置了代理的ProxyLayoutManger，方法调用上还是调用o中实现的方法，o其中还会使用到RecyclerView的方法，导致空指针，这里塞回去一个避免
            val mRecyclerView = LayoutManager::class.java.getDeclaredField("mRecyclerView")
            mRecyclerView.isAccessible = true
            mRecyclerView[o] = recyclerView
            val LayoutMangerField = ViewPager2::class.java.getDeclaredField("mLayoutManager")
            LayoutMangerField.isAccessible = true
            LayoutMangerField[viewPager2] = proxyLayoutManger
            val pageTransformerAdapterField = ViewPager2::class.java.getDeclaredField("mPageTransformerAdapter")
            pageTransformerAdapterField.isAccessible = true
            val mPageTransformerAdapter = pageTransformerAdapterField[viewPager2]
            if (mPageTransformerAdapter != null) {
                val aClass: Class<*> = mPageTransformerAdapter.javaClass
                val layoutManager = aClass.getDeclaredField("mLayoutManager")
                layoutManager.isAccessible = true
                layoutManager[mPageTransformerAdapter] = proxyLayoutManger
            }
            val scrollEventAdapterField = ViewPager2::class.java.getDeclaredField("mScrollEventAdapter")
            scrollEventAdapterField.isAccessible = true
            val mScrollEventAdapter = scrollEventAdapterField[viewPager2]
            if (mScrollEventAdapter != null) {
                val aClass: Class<*> = mScrollEventAdapter.javaClass
                val layoutManager = aClass.getDeclaredField("mLayoutManager")
                layoutManager.isAccessible = true
                layoutManager[mScrollEventAdapter] = proxyLayoutManger
            }
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }

    private inner class ProxyLayoutManger internal constructor(context: Context?, layoutManager: LinearLayoutManager?) : LinearLayoutManager(context, layoutManager!!.orientation, false) {
        private val linearLayoutManager: LayoutManager?
        override fun performAccessibilityAction(recycler: Recycler,
                                                state: State, action: Int, args: Bundle?): Boolean {
            return linearLayoutManager!!.performAccessibilityAction(recycler, state, action, args)
        }

        override fun onInitializeAccessibilityNodeInfo(recycler: Recycler,
                                                       state: State, info: AccessibilityNodeInfoCompat) {
            linearLayoutManager!!.onInitializeAccessibilityNodeInfo(recycler, state, info)
        }

        override fun requestChildRectangleOnScreen(parent: RecyclerView,
                                                   child: View, rect: Rect, immediate: Boolean,
                                                   focusedChildVisible: Boolean): Boolean {
            return linearLayoutManager!!.requestChildRectangleOnScreen(parent, child, rect, immediate, focusedChildVisible)
        }

        override fun smoothScrollToPosition(recyclerView: RecyclerView, state: State, position: Int) {
            val linearSmoothScroller: LinearSmoothScroller = object : LinearSmoothScroller(recyclerView.context) {
                override fun calculateTimeForDeceleration(dx: Int): Int {
                    return (pagerScrollDuration * (1 - .3356)).toInt()
                }
            }
            linearSmoothScroller.targetPosition = position
            startSmoothScroll(linearSmoothScroller)
        }

        override fun calculateExtraLayoutSpace(state: State,
                                               extraLayoutSpace: IntArray) {
            val pageLimit = viewPager2!!.offscreenPageLimit
            if (pageLimit == ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT) {
                super.calculateExtraLayoutSpace(state, extraLayoutSpace)
                return
            }
            val offscreenSpace = pageSize * pageLimit
            extraLayoutSpace[0] = offscreenSpace
            extraLayoutSpace[1] = offscreenSpace
        }

        private val pageSize: Int
            private get() {
                val rv = viewPager2!!.getChildAt(0) as RecyclerView
                return if (orientation == RecyclerView.HORIZONTAL) rv.width - rv.paddingLeft - rv.paddingRight else rv.height - rv.paddingTop - rv.paddingBottom
            }

        init {
            linearLayoutManager = layoutManager
        }
    }
    /*--------------- 下面是对外暴露的方法 ---------------*/
    /**
     * 设置一屏多页
     *
     * @param multiWidth 左右页面露出来的宽度一致
     * @param pageMargin item与item之间的宽度
     */
    fun setPageMargin(multiWidth: Int, pageMargin: Int): Banner {
        return setPageMargin(multiWidth, multiWidth, pageMargin)
    }

    /**
     * 设置一屏多页
     *
     * @param tlWidth    左边页面显露出来的宽度
     * @param brWidth    右边页面露出来的宽度
     * @param pageMargin item与item之间的宽度
     */
    fun setPageMargin(tlWidth: Int, brWidth: Int, pageMargin: Int): Banner {
        compositePageTransformer!!.addTransformer(MarginPageTransformer(pageMargin))
        val recyclerView = viewPager2!!.getChildAt(0) as RecyclerView
        if (viewPager2!!.orientation == ViewPager2.ORIENTATION_VERTICAL) {
            recyclerView.setPadding(viewPager2!!.paddingLeft, tlWidth + Math.abs(pageMargin), viewPager2!!.paddingRight, brWidth + Math.abs(pageMargin))
        } else {
            recyclerView.setPadding(tlWidth + Math.abs(pageMargin), viewPager2!!.paddingTop, brWidth + Math.abs(pageMargin), viewPager2!!.paddingBottom)
        }
        recyclerView.clipToPadding = false
        setOffscreenPageLimit(1)
        needPage = NORMAL_COUNT + NORMAL_COUNT
        return this
    }

    fun setPageTransformer(transformer: ViewPager2.PageTransformer?): Banner {
        compositePageTransformer!!.addTransformer(transformer!!)
        return this
    }

    fun setAutoTurningTime(autoTurningTime: Long): Banner {
        this.autoTurningTime = autoTurningTime
        return this
    }

    fun setOuterPageChangeListener(listener: ViewPager2.OnPageChangeCallback?): Banner {
        changeCallback = listener
        return this
    }

    fun setOffscreenPageLimit(limit: Int): Banner {
        viewPager2!!.offscreenPageLimit = limit
        return this
    }

    /**
     * 设置viewpager2的切换时长
     */
    fun setPagerScrollDuration(pagerScrollDuration: Long): Banner {
        this.pagerScrollDuration = pagerScrollDuration
        return this
    }

    /**
     * 设置轮播方向
     *
     * @param orientation Orientation.ORIENTATION_HORIZONTAL or default
     * Orientation.ORIENTATION_VERTICAL
     */
    fun setOrientation(@ViewPager2.Orientation orientation: Int): Banner {
        viewPager2!!.orientation = orientation
        return this
    }

    fun addItemDecoration(decor: ItemDecoration): Banner {
        viewPager2!!.addItemDecoration(decor)
        return this
    }

    fun addItemDecoration(decor: ItemDecoration, index: Int): Banner {
        viewPager2!!.addItemDecoration(decor, index)
        return this
    }

    /**
     * 是否自动轮播 大于1页轮播才生效
     */
    fun setAutoPlay(autoPlay: Boolean): Banner {
        isAutoPlay = autoPlay
        if (isAutoPlay && realCount > 1) {
            startTurning()
        }
        return this
    }

    fun isAutoPlay(): Boolean {
        return isAutoPlay && realCount > 1
    }

    fun setIndicator(indicator: Indicator?): Banner {
        return setIndicator(indicator, true)
    }

    /**
     * 设置indicator，支持在xml中创建
     *
     * @param attachToRoot true 添加到banner布局中
     */
    fun setIndicator(indicator: Indicator?, attachToRoot: Boolean): Banner {
        if (this.indicator != null) {
            removeView(this.indicator!!.getView())
        }
        if (indicator != null) {
            this.indicator = indicator
            if (attachToRoot) {
                addView(this.indicator!!.getView(), this.indicator!!.getIndicatorParams())
            }
        }
        return this
    }

    fun setHolderRestLoader(holderRestLoader: HolderRestLoader?): Banner {
        this.holderRestLoader = holderRestLoader
        return this
    }

    /**
     * 设置banner圆角 api21以上
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun setRoundCorners(radius: Float): Banner {
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, radius)
            }
        }
        clipToOutline = true
        return this
    }

    /**
     * 返回真实位置
     */
    val currentPager: Int
        get() {
            val position = toRealPosition(currentPage)
            return Math.max(position, 0)
        }

    var adapter: Adapter<ViewHolder>?
        get() = bannerAdapterWrapper!!.adapter
        set(adapter) {
            setAdapter(adapter, 0)
        }

    fun startTurning() {
        stopTurning()
        postDelayed(task, autoTurningTime)
    }

    fun stopTurning() {
        removeCallbacks(task)
    }

    fun setAdapter(adapter: Adapter<ViewHolder>?, startPosition: Int) {
        bannerAdapterWrapper!!.registerAdapter(adapter)
        initPagerCount()
        startPager(startPosition)
    }

    companion object {
        private const val DEFAULT_AUTO_TIME: Long = 2500
        private const val DEFAULT_PAGER_DURATION: Long = 800
        private const val NORMAL_COUNT = 2
    }

    init {
        scaledTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
        initViews(context)
    }
}