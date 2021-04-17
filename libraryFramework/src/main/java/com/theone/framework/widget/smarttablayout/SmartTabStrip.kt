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

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.theone.framework.R

/**
 *
 *
 * Forked from Google Samples &gt; SlidingTabsBasic &gt;
 * [SlidingTabStrip](https://developer.android.com/samples/SlidingTabsBasic/src/com.example.android.common/view/SlidingTabLayout.html)
 */
class SmartTabStrip(context: Context, attrs: AttributeSet?) : LinearLayout(context) {
    private val topBorderThickness: Int
    private val topBorderColor: Int
    private val bottomBorderThickness: Int
    private val bottomBorderColor: Int
    private val borderPaint: Paint
    private val indicatorRectF = RectF()
    private val indicatorWithoutPadding: Boolean
    val isIndicatorAlwaysInCenter: Boolean
    private val indicatorInFront: Boolean
    private val indicatorThickness: Int
    private val indicatorWidth: Int
    private val indicatorHeight: Int
    private val indicatorGravity: Int
    private val indicatorCornerRadius: Float
    private val indicatorPaint: Paint
    private val dividerThickness: Int
    private val dividerPaint: Paint
    private val dividerHeight: Float
    private val defaultTabColorizer: SimpleTabColorizer
    private val drawDecorationAfterTab: Boolean
    private var lastPosition = 0
    private var selectedPosition = 0
    private var selectionOffset = 0f
    private var indicationInterpolator: SmartTabIndicationInterpolator
    private var customTabColorizer: SmartTabLayout.TabColorizer? = null
    private val indicatorStyle: Int

    private val titleImageViewLoop: Boolean
    private val titleImageViewFileName: String?
    private val titleImageViewImageAssetsFolder: String?
    private val titleImageViewId: Int
    fun setIndicationInterpolator(interpolator: SmartTabIndicationInterpolator) {
        indicationInterpolator = interpolator
        invalidate()
    }

    fun setCustomTabColorizer(customTabColorizer: SmartTabLayout.TabColorizer?) {
        this.customTabColorizer = customTabColorizer
        invalidate()
    }

    fun setSelectedIndicatorColors(vararg colors: Int) { // Make sure that the custom colorizer is removed
        customTabColorizer = null
        defaultTabColorizer.indicatorColors = colors
        invalidate()
    }

    fun setTabTextColorBothIndicatorColor(colors: IntArray, indicatorColors: IntArray) {
        customTabColorizer = null
        defaultTabColorizer.indicatorColors = indicatorColors
        invalidate()
    }

    fun setDividerColors(vararg colors: Int) { // Make sure that the custom colorizer is removed
        customTabColorizer = null
        defaultTabColorizer.setDividerColors(*colors)
        invalidate()
    }

    fun onViewPagerPageChanged(position: Int, positionOffset: Float) {
        selectedPosition = position
        selectionOffset = positionOffset
        if (positionOffset == 0f && lastPosition != selectedPosition) {
            lastPosition = selectedPosition
        }
        invalidate()
    }

    val tabColorizer: SmartTabLayout.TabColorizer
        get() = if (customTabColorizer != null) customTabColorizer!! else defaultTabColorizer

    override fun onDraw(canvas: Canvas) {
        if (!drawDecorationAfterTab) {
            drawDecoration(canvas)
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        if (drawDecorationAfterTab) {
            drawDecoration(canvas)
        }
    }

    /*fun setTitleTextStyle(view: View?, selected: Boolean) {
        if (view is TextView) {
           *//* if (titleSelectorSize != 0 && titleNormalSize != 0) {
                view.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    if (selected) titleSelectorSize.toFloat() else titleNormalSize.toFloat()
                )
            }
            if (titleSelectorTextStyle == 1) {
                view.paint.isFakeBoldText = if (selected) titleSelectorTextStyle == 1 else titleNormalTextStyle == 1
            }*//*
            textColors?.let {
                view.setTextColor(if (selected) it[0] else it[1])
            }
        }
    }*/

    private fun drawDecoration(canvas: Canvas) {
        if (titleImageViewId != View.NO_ID) {
            return
        }
        val height = height
        val width = width
        val tabCount = childCount
        val tabColorizer = tabColorizer
        val isLayoutRtl = Utils.isLayoutRtl(this)
        if (indicatorInFront) {
            drawOverline(canvas, 0, width)
            drawUnderline(canvas, 0, width, height)
        }
        // Thick colored underline below the current selection
        if (tabCount > 0) {
            val selectedTab = getChildAt(selectedPosition)
            val selectedStart = Utils.getStart(selectedTab, indicatorWithoutPadding)
            val selectedEnd = Utils.getEnd(selectedTab, indicatorWithoutPadding)
            var left: Int
            var right: Int
            if (isLayoutRtl) {
                left = selectedEnd
                right = selectedStart
            } else {
                left = selectedStart
                right = selectedEnd
            }
            var color = tabColorizer.getIndicatorColor(selectedPosition)
            var thickness = indicatorThickness.toFloat()
            if (selectionOffset > 0f && selectedPosition < childCount - 1) {
                val nextColor = tabColorizer.getIndicatorColor(selectedPosition + 1)
                if (color != nextColor) {
                    color = blendColors(nextColor, color, selectionOffset)
                }
                // Draw the selection partway between the tabs
                val startOffset = indicationInterpolator.getLeftEdge(selectionOffset)
                val endOffset = indicationInterpolator.getRightEdge(selectionOffset)
                val thicknessOffset = indicationInterpolator.getThickness(selectionOffset)
                val nextTab = getChildAt(selectedPosition + 1)
                val nextStart = Utils.getStart(nextTab, indicatorWithoutPadding)
                val nextEnd = Utils.getEnd(nextTab, indicatorWithoutPadding)
                if (isLayoutRtl) {
                    left = (endOffset * nextEnd + (1.0f - endOffset) * left).toInt()
                    right = (startOffset * nextStart + (1.0f - startOffset) * right).toInt()
                } else {
                    left = (startOffset * nextStart + (1.0f - startOffset) * left).toInt()
                    right = (endOffset * nextEnd + (1.0f - endOffset) * right).toInt()
                }
                thickness = thickness * thicknessOffset
            }
            drawIndicator(canvas, left, right, height, thickness, color)
        }
        if (!indicatorInFront) {
            drawOverline(canvas, 0, width)
            drawUnderline(canvas, 0, getWidth(), height)
        }
        drawSeparator(canvas)
    }

    private fun drawSeparator(canvas: Canvas) {
        if (dividerThickness <= 0) {
            return
        }
        val height = height
        val tabCount = childCount
        val dividerHeightPx = (Math.min(Math.max(0f, dividerHeight), 1f) * height).toInt()
        val tabColorizer = tabColorizer
        // Vertical separators between the titles
        val separatorTop = (height - dividerHeightPx) / 2
        val separatorBottom = separatorTop + dividerHeightPx
        val isLayoutRtl = Utils.isLayoutRtl(this)
        for (i in 0 until tabCount - 1) {
            val child = getChildAt(i)
            val end = Utils.getEnd(child)
            val endMargin = Utils.getMarginEnd(child)
            val separatorX = if (isLayoutRtl) end - endMargin else end + endMargin
            dividerPaint.color = tabColorizer.getDividerColor(i)
            canvas.drawLine(
                separatorX.toFloat(),
                separatorTop.toFloat(),
                separatorX.toFloat(),
                separatorBottom.toFloat(),
                dividerPaint
            )
        }
    }

    private fun drawIndicator(canvas: Canvas, left: Int, right: Int, height: Int, thickness: Float, color: Int) {
        if (indicatorThickness <= 0 || indicatorWidth == 0) {
            return
        }
        val center: Float
        val top: Float
        val bottom: Float
        when (indicatorGravity) {
            GRAVITY_TOP -> {
                center = indicatorThickness / 2f
                top = center - thickness / 2f
                bottom = center + thickness / 2f
            }
            GRAVITY_CENTER -> {
                center = height / 2f
                top = center - thickness / 2f
                bottom = center + thickness / 2f
            }
            GRAVITY_BOTTOM -> {
                center = height - indicatorThickness / 2f
                top = center - thickness / 2f
                bottom = center + thickness / 2f
            }
            else -> {
                center = height - indicatorThickness / 2f
                top = center - thickness / 2f
                bottom = center + thickness / 2f
            }
        }
        if (indicatorWidth == AUTO_WIDTH) {
            indicatorRectF[left.toFloat(), top, right.toFloat()] = bottom
        } else if (indicatorHeight != AUTO_HEIGHT) {
            val padding = (Math.abs(left - right) - indicatorWidth) / 2f
            val addHeight = indicatorHeight - bottom + top
            indicatorRectF[left + padding, top - addHeight, right - padding] = bottom
        } else {
            val padding = (Math.abs(left - right) - indicatorWidth) / 2f
            indicatorRectF[left + padding, top, right - padding] = bottom
        }
        //指示器颜色
        if (indicatorStyle != DEFAULT_INDICATOR_STYLE) {
            val indicatorColors = defaultTabColorizer.indicatorColors
            if (indicatorColors.size < 2) {
                throw RuntimeException("when SmartTabLayout indicatorStyle is gradient,stl_indicatorColors must has more than one colors")
            }
            val shader = LinearGradient(
                indicatorRectF.left,
                indicatorRectF.top,
                indicatorRectF.right,
                indicatorRectF.bottom,
                indicatorColors,
                floatArrayOf(0f, 1f),
                Shader.TileMode.REPEAT
            )
            indicatorPaint.shader = shader
        } else {
            indicatorPaint.color = defaultTabColorizer.indicatorColors[0]
        }
        if (indicatorCornerRadius > 0f) {
            canvas.drawRoundRect(
                indicatorRectF, indicatorCornerRadius,
                indicatorCornerRadius, indicatorPaint
            )
        } else {
            canvas.drawRect(indicatorRectF, indicatorPaint)
        }
    }

    private fun drawOverline(canvas: Canvas, left: Int, right: Int) {
        if (topBorderThickness <= 0) {
            return
        }
        // Thin overline along the entire top edge
        borderPaint.color = topBorderColor
        canvas.drawRect(left.toFloat(), 0f, right.toFloat(), topBorderThickness.toFloat(), borderPaint)
    }

    private fun drawUnderline(canvas: Canvas, left: Int, right: Int, height: Int) {
        if (bottomBorderThickness <= 0) {
            return
        }
        // Thin underline along the entire bottom edge
        borderPaint.color = bottomBorderColor
        canvas.drawRect(
            left.toFloat(),
            height - bottomBorderThickness.toFloat(),
            right.toFloat(),
            height.toFloat(),
            borderPaint
        )
    }

    private class SimpleTabColorizer : SmartTabLayout.TabColorizer {
        var indicatorColors: IntArray = intArrayOf()
        private var dividerColors: IntArray = intArrayOf()
        override fun getIndicatorColor(position: Int): Int {
            return indicatorColors[position % indicatorColors.size]
        }

        override fun getDividerColor(position: Int): Int {
            return dividerColors.get(position % dividerColors.size)
        }

        fun setDividerColors(vararg colors: Int) {
            dividerColors = colors
        }
    }

    companion object {
        private const val GRAVITY_BOTTOM = 0
        private const val GRAVITY_TOP = 1
        private const val GRAVITY_CENTER = 2
        private const val AUTO_HEIGHT = -1
        private const val AUTO_WIDTH = -1
        private const val DEFAULT_TOP_BORDER_THICKNESS_DIPS = 0
        private const val DEFAULT_TOP_BORDER_COLOR_ALPHA: Byte = 0x26
        private const val DEFAULT_BOTTOM_BORDER_THICKNESS_DIPS = 2
        private const val DEFAULT_BOTTOM_BORDER_COLOR_ALPHA: Byte = 0x26
        private const val SELECTED_INDICATOR_THICKNESS_DIPS = 8
        private const val DEFAULT_SELECTED_INDICATOR_COLOR = -0xcc4a1b
        private const val DEFAULT_INDICATOR_CORNER_RADIUS = 0f
        private const val DEFAULT_DIVIDER_THICKNESS_DIPS = 1
        private const val DEFAULT_DIVIDER_COLOR_ALPHA: Byte = 0x20
        private const val DEFAULT_DIVIDER_HEIGHT = 0.5f
        private const val DEFAULT_INDICATOR_IN_CENTER = false
        private const val DEFAULT_INDICATOR_IN_FRONT = false
        private const val DEFAULT_INDICATOR_WITHOUT_PADDING = false
        private const val DEFAULT_INDICATOR_GRAVITY = GRAVITY_BOTTOM
        private const val DEFAULT_DRAW_DECORATION_AFTER_TAB = false
        private const val DEFAULT_INDICATOR_STYLE = 0
        private const val DEFAULT_IMAGEVIEW_LOOP = false

        /**
         * Set the alpha value of the `color` to be the given `alpha` value.
         */
        private fun setColorAlpha(color: Int, alpha: Byte): Int {
            return Color.argb(alpha.toInt(), Color.red(color), Color.green(color), Color.blue(color))
        }

        /**
         * Blend `color1` and `color2` using the given ratio.
         *
         * @param ratio of which to blend. 1.0 will return `color1`, 0.5 will give an even blend,
         * 0.0 will return `color2`.
         */
        private fun blendColors(color1: Int, color2: Int, ratio: Float): Int {
            val inverseRation = 1f - ratio
            val r = Color.red(color1) * ratio + Color.red(color2) * inverseRation
            val g = Color.green(color1) * ratio + Color.green(color2) * inverseRation
            val b = Color.blue(color1) * ratio + Color.blue(color2) * inverseRation
            return Color.rgb(r.toInt(), g.toInt(), b.toInt())
        }
    }

    init {
        setWillNotDraw(false)
        val density = resources.displayMetrics.density
        val outValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.colorForeground, outValue, true)
        val themeForegroundColor = outValue.data
        var indicatorWithoutPadding = DEFAULT_INDICATOR_WITHOUT_PADDING
        var indicatorInFront = DEFAULT_INDICATOR_IN_FRONT
        var indicatorAlwaysInCenter = DEFAULT_INDICATOR_IN_CENTER
        var indicationInterpolatorId: Int = SmartTabIndicationInterpolator.Companion.ID_SMART
        var indicatorGravity = DEFAULT_INDICATOR_GRAVITY
        var indicatorColor = DEFAULT_SELECTED_INDICATOR_COLOR
        var indicatorColorsId = View.NO_ID
        var indicatorThickness = (SELECTED_INDICATOR_THICKNESS_DIPS * density).toInt()
        var indicatorWidth = AUTO_WIDTH
        var indicatorHeight = AUTO_HEIGHT
        var indicatorCornerRadius = DEFAULT_INDICATOR_CORNER_RADIUS * density
        var overlineColor = setColorAlpha(themeForegroundColor, DEFAULT_TOP_BORDER_COLOR_ALPHA)
        var overlineThickness = (DEFAULT_TOP_BORDER_THICKNESS_DIPS * density).toInt()
        var underlineColor = setColorAlpha(themeForegroundColor, DEFAULT_BOTTOM_BORDER_COLOR_ALPHA)
        var underlineThickness = (DEFAULT_BOTTOM_BORDER_THICKNESS_DIPS * density).toInt()
        var dividerColor = setColorAlpha(themeForegroundColor, DEFAULT_DIVIDER_COLOR_ALPHA)
        var dividerColorsId = View.NO_ID
        var dividerThickness = (DEFAULT_DIVIDER_THICKNESS_DIPS * density).toInt()
        var drawDecorationAfterTab = DEFAULT_DRAW_DECORATION_AFTER_TAB
        val a = context.obtainStyledAttributes(attrs, R.styleable.stl_SmartTabLayout)
        indicatorAlwaysInCenter = a.getBoolean(
            R.styleable.stl_SmartTabLayout_stl_indicatorAlwaysInCenter, indicatorAlwaysInCenter
        )
        indicatorWithoutPadding = a.getBoolean(
            R.styleable.stl_SmartTabLayout_stl_indicatorWithoutPadding, indicatorWithoutPadding
        )
        indicatorInFront = a.getBoolean(
            R.styleable.stl_SmartTabLayout_stl_indicatorInFront, indicatorInFront
        )
        indicationInterpolatorId = a.getInt(
            R.styleable.stl_SmartTabLayout_stl_indicatorInterpolation, indicationInterpolatorId
        )
        indicatorGravity = a.getInt(
            R.styleable.stl_SmartTabLayout_stl_indicatorGravity, indicatorGravity
        )
        indicatorColor = a.getColor(
            R.styleable.stl_SmartTabLayout_stl_indicatorColor, indicatorColor
        )
        indicatorColorsId = a.getResourceId(
            R.styleable.stl_SmartTabLayout_stl_indicatorColors, indicatorColorsId
        )
        indicatorThickness = a.getDimensionPixelSize(
            R.styleable.stl_SmartTabLayout_stl_indicatorThickness, indicatorThickness
        )
        indicatorWidth = a.getLayoutDimension(
            R.styleable.stl_SmartTabLayout_stl_indicatorWidth, indicatorWidth
        )
        indicatorHeight = a.getLayoutDimension(
            R.styleable.stl_SmartTabLayout_stl_indicatorHeight, indicatorHeight
        )
        indicatorCornerRadius = a.getDimension(
            R.styleable.stl_SmartTabLayout_stl_indicatorCornerRadius, indicatorCornerRadius
        )
        overlineColor = a.getColor(
            R.styleable.stl_SmartTabLayout_stl_overlineColor, overlineColor
        )
        overlineThickness = a.getDimensionPixelSize(
            R.styleable.stl_SmartTabLayout_stl_overlineThickness, overlineThickness
        )
        underlineColor = a.getColor(
            R.styleable.stl_SmartTabLayout_stl_underlineColor, underlineColor
        )
        underlineThickness = a.getDimensionPixelSize(
            R.styleable.stl_SmartTabLayout_stl_underlineThickness, underlineThickness
        )
        dividerColor = a.getColor(
            R.styleable.stl_SmartTabLayout_stl_dividerColor, dividerColor
        )
        dividerColorsId = a.getResourceId(
            R.styleable.stl_SmartTabLayout_stl_dividerColors, dividerColorsId
        )
        dividerThickness = a.getDimensionPixelSize(
            R.styleable.stl_SmartTabLayout_stl_dividerThickness, dividerThickness
        )
        drawDecorationAfterTab = a.getBoolean(
            R.styleable.stl_SmartTabLayout_stl_drawDecorationAfterTab, drawDecorationAfterTab
        )
        indicatorStyle = a.getInt(R.styleable.stl_SmartTabLayout_stl_indicatorStyle, DEFAULT_INDICATOR_STYLE)
        titleSelectorSize = a.getDimensionPixelSize(R.styleable.stl_SmartTabLayout_stl_titleSelectorTextSize, 0)
        titleNormalSize = a.getDimensionPixelSize(R.styleable.stl_SmartTabLayout_stl_titleNormalTextSize, 0)
        titleNormalTextStyle = a.getInt(R.styleable.stl_SmartTabLayout_stl_titleNormalTextStyle, 0)
        titleSelectorTextStyle = a.getInt(R.styleable.stl_SmartTabLayout_stl_titleSelectorTextStyle, 0)
        titleImageViewLoop = a.getBoolean(
            R.styleable.stl_SmartTabLayout_stl_customTabImageViewLoop, DEFAULT_IMAGEVIEW_LOOP
        )
        titleImageViewFileName = a.getString(R.styleable.stl_SmartTabLayout_stl_customTabImageViewFileName)
        titleImageViewImageAssetsFolder =
            a.getString(R.styleable.stl_SmartTabLayout_stl_customTabImageViewImageAssetsFolder)
        titleImageViewId = a.getResourceId(
            R.styleable.stl_SmartTabLayout_stl_customTabImageViewId, View.NO_ID
        )
        a.recycle()
        val indicatorColors =
            if (indicatorColorsId == View.NO_ID) intArrayOf(indicatorColor) else resources.getIntArray(indicatorColorsId)
        val dividerColors =
            if (dividerColorsId == View.NO_ID) intArrayOf(dividerColor) else resources.getIntArray(dividerColorsId)
        defaultTabColorizer = SimpleTabColorizer()
        defaultTabColorizer.indicatorColors = indicatorColors
        this.defaultTabColorizer.setDividerColors(*dividerColors)
        topBorderThickness = overlineThickness
        topBorderColor = overlineColor
        bottomBorderThickness = underlineThickness
        bottomBorderColor = underlineColor
        borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        isIndicatorAlwaysInCenter = indicatorAlwaysInCenter
        this.indicatorWithoutPadding = indicatorWithoutPadding
        this.indicatorInFront = indicatorInFront
        this.indicatorThickness = indicatorThickness
        this.indicatorWidth = indicatorWidth
        this.indicatorHeight = indicatorHeight
        indicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        this.indicatorCornerRadius = indicatorCornerRadius
        this.indicatorGravity = indicatorGravity
        dividerHeight = DEFAULT_DIVIDER_HEIGHT
        dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        dividerPaint.strokeWidth = dividerThickness.toFloat()
        this.dividerThickness = dividerThickness
        this.drawDecorationAfterTab = drawDecorationAfterTab
        indicationInterpolator = SmartTabIndicationInterpolator.of(indicationInterpolatorId)
    }
}