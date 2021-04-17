package com.theone.framework.util

import android.content.Context
import android.content.SharedPreferences
import com.theone.framework.base.BaseApp

/**
 * @author zhiqiang
 * @date 2019-06-05
 * @desc
 */
object SpUtil {

    /**
     * 用户个人信息相关sp
     */
    const val USER_INFO = "user_info"

    /**
     * 手机设置信息 相关sp
     */
    const val SETTING_INFO = "setting_info"

    @JvmStatic
    @JvmOverloads
    fun getSp(
        context: Context = BaseApp.application,
        spName: String = SETTING_INFO
    ): SharedPreferences {
        return context.getSharedPreferences(spName, Context.MODE_PRIVATE)
    }

}