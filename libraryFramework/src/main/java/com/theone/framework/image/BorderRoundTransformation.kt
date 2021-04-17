package com.theone.framework.image

import android.content.Context
import android.graphics.*
import androidx.annotation.IntRange
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapResource
import com.theone.framework.R
import com.theone.framework.ext.dp2px
import com.theone.framework.ext.getColor
import com.theone.framework.ext.getDimensionPixelSize
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.security.MessageDigest

/**
 * @Author ZhiQiang
 * @Date 2020/6/23
 * @Description TODO 绘制带边框圆角矩形
 */
class BorderRoundTransformation(context: Context, radius: Int = dp2px(8),mBorderColor: Int =Color.parseColor("#FFDDDDDD"), mBorderWidth: Int = dp2px(1),  @RadiusPosition position: Int = 0b1111, margin: Int = 0) : Transformation<Bitmap?> {

    private val mBitmapPool: BitmapPool = Glide.get(context).bitmapPool


    @IntRange(from = 0b0000, to = 0b1111)
    @Retention(RetentionPolicy.SOURCE)
    annotation class RadiusPosition


    /**
     * 圆角半径
     */
    private val mRadius: Int = radius

    /**
     * 边距
     */
    private val mMargin: Int = margin

    /**
     * 边框宽度
     */
    private val mBorderWidth: Int = mBorderWidth

    /**
     * 边框颜色
     */
    private val mBorderColor: Int = mBorderColor

    /**
     * 用一个整形表示哪些边角需要加圆角边框
     * 例如：0b1000,表示左上角需要加圆角边框
     * 0b1110 表示左上右上右下需要加圆角边框
     * 0b0000表示不加圆形边框
     */
    @RadiusPosition
    private val mCornerPos: Int = position

    //这里一定要是设置一个独一无二的ID，要不然重用会导致第二次调用不起效果，最好加上相应的变量参数，保证唯一性
    private val id: String by lazy {
        "RoundedTransformation(radius=" + mRadius + ", margin=" + mMargin + ", mBorderWidth" + mBorderWidth + ", mBorderColor" + mBorderColor + "mCornerPos" + mCornerPos + ")"
    }

    override fun transform(context: Context, resource: Resource<Bitmap?>, outWidth: Int, outHeight: Int): Resource<Bitmap?> {
        val source = resource.get()
        val width = source.width
        val height = source.height
        var bitmap: Bitmap? = mBitmapPool[width, height, Bitmap.Config.ARGB_8888]
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        }
        val canvas = Canvas(bitmap!!) //新建一个空白的bitmap
        val paint = Paint()
        paint.isAntiAlias = true
        paint.shader = BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP) //设置要绘制的图形
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG) //设置边框样式
        borderPaint.color = mBorderColor
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = mBorderWidth.toFloat()
        drawRoundRect(canvas, paint, width.toFloat(), height.toFloat(), borderPaint)
        return BitmapResource.obtain(bitmap, mBitmapPool)!!
    }

    private fun drawRoundRect(canvas: Canvas, paint: Paint, width: Float, height: Float, borderPaint: Paint) {
        val right = width - mMargin
        val bottom = height - mMargin
        val halfBorder = mBorderWidth / 2.toFloat()
        val path = Path()
        val pos = FloatArray(8)
        var shift = mCornerPos
        var index = 3
        while (index >= 0) { //设置四个边角的弧度半径
            pos[2 * index + 1] = if (shift and 1 > 0) mRadius.toFloat() else 0f
            pos[2 * index] = if (shift and 1 > 0) mRadius.toFloat() else 0f
            shift = shift shr 1
            index--
        }
        path.addRoundRect(RectF(mMargin + halfBorder, mMargin + halfBorder, right - halfBorder, bottom - halfBorder),
                pos
                , Path.Direction.CW)
        canvas.drawPath(path, paint) //绘制要加载的图形
        canvas.drawPath(path, borderPaint) //绘制边框
    }


    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(id.toByteArray())
    }

}