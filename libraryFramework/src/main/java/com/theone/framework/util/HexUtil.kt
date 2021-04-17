package com.theone.framework.util

import android.util.Log

import java.math.BigInteger

/**
 * @Author zhiqiang
 * @Date 2019-06-07
 * @Email liuzhiqiang@theone.com
 * @Description
 */
object HexUtil {

    fun hex36To10Long(num: String): Long {
        return java.lang.Long.parseLong(num, 36)
    }

    fun hex36To10(num: String): String? {
        return hexChange(num, 36, 10)
    }

    fun hex10To36(valueOf: String): String? {
        return hexChange(valueOf, 10, 36)
    }

    fun hexChange(input: String, fromRadix: Int, toRadix: Int): String? {
        try {
            return BigInteger(input, fromRadix).toString(toRadix)
        } catch (e: Exception) {
            Log.e("Hex36Util", e.toString() + "")
        }

        return null
    }
}
