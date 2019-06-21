package com.juqitech.moretickets.core.util

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils

/**
 * @author zhiqiang
 * @date 2019-06-05
 * @desc
 */
object SpUtil {

    /**
     * 用户个人信息相关sp
     */
    private const val USER_INFO = "user_info"
    /**
     * 手机设置信息 相关sp
     */
    private const val SETTING_INFO = "setting_info"

    @JvmStatic
    fun getSpUser(context: Context): SharedPreferences {
        return context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE)
    }

    @JvmStatic
    fun getSpSetting(context: Context): SharedPreferences {
        return context.getSharedPreferences(SETTING_INFO, Context.MODE_PRIVATE)
    }

}
