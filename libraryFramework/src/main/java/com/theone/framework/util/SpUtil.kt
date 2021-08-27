package com.theone.framework.util

import android.content.Context
import android.content.SharedPreferences
import com.tencent.mmkv.MMKV
import com.theone.framework.base.BaseApp

/**
 * @author zhiqiang
 * @date 2019-06-05
 * @desc
 */
object SpUtil {

    /**
     * 手机设置信息 相关sp
     */
    const val SETTING_INFO = "setting_info"

    fun init(context: Context){
        MMKV.initialize(context)
    }

    @JvmStatic
    @JvmOverloads
    fun getSp(
        context: Context = BaseApp.application,
        spName: String = SETTING_INFO
    ): SharedPreferences {
        if (MMKV.getRootDir().isNullOrEmpty()){
            MMKV.initialize(context)
        }
        val preferences: MMKV? = MMKV.mmkvWithID(SETTING_INFO, Context.MODE_PRIVATE)
        return preferences!!
    }

    @JvmStatic
    fun getMMKV(context: Context = BaseApp.application): MMKV {
        if (MMKV.getRootDir().isNullOrEmpty()){
            MMKV.initialize(context)
        }
        return MMKV.mmkvWithID(SETTING_INFO, Context.MODE_PRIVATE)!!
    }
}