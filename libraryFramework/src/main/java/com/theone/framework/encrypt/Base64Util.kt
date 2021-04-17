package com.theone.framework.encrypt

import android.util.Base64

/**
 * @Author zhiqiang
 * @Date 2019-05-15
 * @Email liuzhiqiang@theone.com
 * @Description Base64工具类，以保持跟后台统一
 */
object Base64Util {

    fun encode(data: ByteArray): String {
        return Base64.encodeToString(data, Base64.DEFAULT)
    }

    fun decode(decode: String?): ByteArray {
        if (decode==null){
            return byteArrayOf()
        }
        return Base64.decode(decode, Base64.DEFAULT)
    }

}
