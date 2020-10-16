package com.theone.framework.ext

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

/**
 * @Author ZhiQiang
 * @Date 2020/10/15
 * @Description
 */
/**
 * 压缩图片到一定大小
 * @receiver Bitmap
 * @param maxSize Int K
 * @return Bitmap?
 */
fun Bitmap.compressToSize(maxSize: Int): Bitmap? {
    val baos = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 100, baos) // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
    var options = 90
    while (baos.toByteArray().size / 1024 > maxSize) { // 循环判断如果压缩后图片是否大于maxSizekb,大于继续压缩
        baos.reset() // 重置baos即清空baos
        this.compress(Bitmap.CompressFormat.JPEG, options, baos) // 这里压缩options
        // %，把压缩后的数据存放到baos中
        options -= 10 // 每次都减少10
    }
    val isBm = ByteArrayInputStream(baos.toByteArray()) // 把压缩后的数据baos
    // 存放到ByteArrayInputStream中
    return BitmapFactory.decodeStream(isBm, null, BitmapFactory.Options().apply {
        inPreferredConfig = Bitmap.Config.RGB_565
    })
}