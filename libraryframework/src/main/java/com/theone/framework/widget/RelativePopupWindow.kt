package com.theone.framework.widget

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.annotation.IntDef
import androidx.core.widget.PopupWindowCompat

/**
 * @Author zhiqiang
 * @Date 2019-07-18
 * @Description 可以设置相对位置的PopupWindow
 * https://github.com/kakajika/RelativePopupWindow
 */
class RelativePopupWindow : PopupWindow {

    @IntDef(Vertical.CENTER, Vertical.ABOVE, Vertical.BELOW, Vertical.ALIGN_TOP, Vertical.ALIGN_BOTTOM)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Vertical {
        companion object {
            const val CENTER = 0
            const val ABOVE = 1
            const val BELOW = 2
            const val ALIGN_TOP = 3
            const val ALIGN_BOTTOM = 4
        }
    }

    @IntDef(Horizontal.CENTER, Horizontal.LEFT, Horizontal.RIGHT, Horizontal.ALIGN_LEFT, Horizontal.ALIGN_RIGHT)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Horizontal {
        companion object {
            const val CENTER = 0
            const val LEFT = 1
            const val RIGHT = 2
            const val ALIGN_LEFT = 3
            const val ALIGN_RIGHT = 4
        }
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    constructor() : super()

    constructor(contentView: View) : super(contentView)

    constructor(width: Int, height: Int) : super(width, height)

    constructor(contentView: View, width: Int, height: Int) : super(contentView, width, height)

    constructor(contentView: View, width: Int, height: Int, focusable: Boolean) : super(
        contentView,
        width,
        height,
        focusable
    )

    /**
     * Show at relative position to anchor View.
     *
     * @param anchor      Anchor View
     * @param vertPos     Vertical Position Flag
     * @param horizPos    Horizontal Position Flag
     * @param fitInScreen Automatically fit in screen or not
     */
    fun showOnAnchor(anchor: View, @Vertical vertPos: Int, @Horizontal horizPos: Int, fitInScreen: Boolean) {
        showOnAnchor(anchor, vertPos, horizPos, 0, 0, fitInScreen)
    }

    /**
     * Show at relative position to anchor View with translation.
     *
     * @param anchor      Anchor View
     * @param vertPos     Vertical Position Flag
     * @param horizPos    Horizontal Position Flag
     * @param x           Translation X
     * @param y           Translation Y
     * @param fitInScreen Automatically fit in screen or not
     */
    @JvmOverloads
    fun showOnAnchor(
        anchor: View, @Vertical vertPos: Int, @Horizontal horizPos: Int,
        x: Int = 0,
        y: Int = 0,
        fitInScreen: Boolean = true
    ) {
        var xVar = x
        var yVar = y
        isClippingEnabled = fitInScreen
        val contentView = contentView
        contentView.measure(makeDropDownMeasureSpec(width), makeDropDownMeasureSpec(height))
        val measuredW = contentView.measuredWidth
        val measuredH = contentView.measuredHeight
        if (!fitInScreen) {
            val anchorLocation = IntArray(2)
            anchor.getLocationInWindow(anchorLocation)
            xVar += anchorLocation[0]
            yVar += anchorLocation[1] + anchor.height
        }
        when (vertPos) {
            Vertical.ABOVE -> yVar -= measuredH + anchor.height
            Vertical.ALIGN_BOTTOM -> yVar -= measuredH
            Vertical.CENTER -> yVar -= anchor.height / 2 + measuredH / 2
            Vertical.ALIGN_TOP -> yVar -= anchor.height
            Vertical.BELOW -> {
            }
            else -> {
            }
        }// Default position.
        when (horizPos) {
            Horizontal.LEFT -> xVar -= measuredW
            Horizontal.ALIGN_RIGHT -> xVar -= measuredW - anchor.width
            Horizontal.CENTER -> xVar += anchor.width / 2 - measuredW / 2
            Horizontal.ALIGN_LEFT -> {
            }
            Horizontal.RIGHT -> xVar += anchor.width
            else -> xVar += anchor.width
        }// Default position.
        if (fitInScreen) {
            PopupWindowCompat.showAsDropDown(this, anchor, xVar, yVar, Gravity.NO_GRAVITY)
        } else {
            showAtLocation(anchor, Gravity.NO_GRAVITY, xVar, yVar)
        }
    }

    private fun makeDropDownMeasureSpec(measureSpec: Int): Int {
        return View.MeasureSpec.makeMeasureSpec(
            View.MeasureSpec.getSize(measureSpec),
            getDropDownMeasureSpecMode(measureSpec)
        )
    }

    private fun getDropDownMeasureSpecMode(measureSpec: Int): Int {
        when (measureSpec) {
            ViewGroup.LayoutParams.WRAP_CONTENT -> return View.MeasureSpec.UNSPECIFIED
            else -> return View.MeasureSpec.EXACTLY
        }
    }

}