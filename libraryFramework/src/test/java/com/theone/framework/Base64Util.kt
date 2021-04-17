package com.theone.framework

import java.nio.charset.Charset
import java.util.*

/**
 * @Author zhiqiang
 * @Date 2019-05-15
 * @Email liuzhiqiang@moretickets.com
 * @Description Base64工具类，以保持跟后台统一
 */
object Base64Util {

    fun encode(data: ByteArray): String {
        return Base641.encodeToString(data, Base641.NO_WRAP)
//        return Base64.getEncoder().encodeToString(data)
    }

    fun decode(decode: String?): ByteArray {
        if (decode == null) {
            return byteArrayOf()
        }
        return Base641.decode(decode, Base641.NO_WRAP)
//        return Base64.getDecoder().decode(decode.toByteArray(Charset.forName("UTF-8")))
    }

}
