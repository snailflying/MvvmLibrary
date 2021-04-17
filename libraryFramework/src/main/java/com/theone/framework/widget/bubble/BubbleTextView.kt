package com.theone.framework.widget.bubble

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.theone.framework.R

/**
 * @Author zhiqiang
 * @Date 2019-07-18

 * @Description 气泡TextView
 * https://github.com/lguipeng/BubbleView
 */
class BubbleTextView : AppCompatTextView {
    private var bubbleDrawable: BubbleDrawable? = null
    private var mArrowWidth: Float = 0.toFloat()
    private var mRadius: Float = 0.toFloat()
    private var mArrowHeight: Float = 0.toFloat()
    private var mArrowPosition: Float = 0.toFloat()
    private var bubbleColor: Int = 0
    @BubbleDrawable.ArrowLocation
    private var mArrowLocation: Int? = null
    private var mArrowCenter: Boolean = false

    constructor(context: Context) : super(context) {
        initView(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        initView(attrs)
    }

    private fun initView(attrs: AttributeSet?) {
        if (attrs != null) {
            @SuppressLint("CustomViewStyleable")
            val array = context.obtainStyledAttributes(attrs, R.styleable.BubbleView)
            mArrowWidth = array.getDimension(
                R.styleable.BubbleView_arrowWidth,
                BubbleDrawable.Builder.DEFAULT_ARROW_WITH
            )
            mArrowHeight = array.getDimension(
                R.styleable.BubbleView_arrowHeight,
                BubbleDrawable.Builder.DEFAULT_ARROW_HEIGHT
            )
            mRadius = array.getDimension(
                R.styleable.BubbleView_radius,
                BubbleDrawable.Builder.DEFAULT_ANGLE
            )
            mArrowPosition = array.getDimension(
                R.styleable.BubbleView_arrowPosition,
                BubbleDrawable.Builder.DEFAULT_ARROW_POSITION
            )
            bubbleColor = array.getColor(
                R.styleable.BubbleView_bubbleColor,
                BubbleDrawable.Builder.DEFAULT_BUBBLE_COLOR
            )
            val location = array.getInt(R.styleable.BubbleView_arrowLocation, 0)
            mArrowLocation = location
            mArrowCenter = array.getBoolean(R.styleable.BubbleView_arrowCenter, false)
            array.recycle()
        }
        setUpPadding()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0) {
            setUp(w, h)
        }
    }

    override fun layout(l: Int, t: Int, r: Int, b: Int) {
        super.layout(l, t, r, b)
        setUp()
    }

    override fun onDraw(canvas: Canvas) {
        if (bubbleDrawable != null) {
            bubbleDrawable!!.draw(canvas)
        }
        super.onDraw(canvas)
    }

    private fun setUp(width: Int = getWidth(), height: Int = getHeight()) {
        setUp(0, width, 0, height)
    }

    private fun setUp(left: Int, right: Int, top: Int, bottom: Int) {
        val rectF = RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
        bubbleDrawable = mArrowLocation?.let {
            BubbleDrawable.Builder()
                .rect(rectF)
                .arrowLocation(it)
                .bubbleType(BubbleDrawable.BubbleType.COLOR)
                .angle(mRadius)
                .arrowHeight(mArrowHeight)
                .arrowWidth(mArrowWidth)
                .bubbleColor(bubbleColor)
                .arrowPosition(mArrowPosition)
                .arrowCenter(mArrowCenter)
                .build()
        }
    }

    private fun setUpPadding() {
        var left = paddingLeft
        var right = paddingRight
        var top = paddingTop
        var bottom = paddingBottom
        when (mArrowLocation) {
            BubbleDrawable.ArrowLocation.LEFT -> left += mArrowWidth.toInt()
            BubbleDrawable.ArrowLocation.RIGHT -> right += mArrowWidth.toInt()
            BubbleDrawable.ArrowLocation.TOP -> top += mArrowHeight.toInt()
            BubbleDrawable.ArrowLocation.BOTTOM -> bottom += mArrowHeight.toInt()
            else -> bottom += mArrowHeight.toInt()
        }
        setPadding(left, top, right, bottom)
    }
}
