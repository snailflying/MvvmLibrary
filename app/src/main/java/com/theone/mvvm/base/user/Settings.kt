package com.theone.mvvm.base.user

import android.annotation.SuppressLint
import com.theone.framework.base.CoreApp
import com.theone.framework.ext.getEncryptString
import com.theone.framework.ext.putEncryptString
import com.theone.framework.util.SpUtil

/**
 * @Author zhiqiang
 * @Email liuzhiqiang@moretickets.com
 * @Date 2019-06-11
 * @Description User类的保存
 */
class Settings private constructor() {

    private val prefs by lazy { SpUtil.getSpSetting(CoreApp.application) }

    var accessToken = prefs.getEncryptString(SP_ACCESS_TOKEN)
        set(value) {
            prefs.edit().putEncryptString(SP_ACCESS_TOKEN, value).apply()
            field = value
        }
    var refreshToken = prefs.getEncryptString(SP_REFRESH_TOKEN)
        set(value) {
            prefs.edit().putEncryptString(SP_REFRESH_TOKEN, value).apply()
            field = value
        }
    var cellphone = prefs.getEncryptString(SP_MOBILE)
        set(value) {
            prefs.edit().putEncryptString(SP_MOBILE, value).apply()
            field = value
        }
    var userId = prefs.getEncryptString(SP_USER_ID)
        set(value) {
            prefs.edit().putEncryptString(SP_USER_ID, value).apply()
            field = value
        }

    var name = prefs.getEncryptString(SP_NAME)
        set(value) {
            prefs.edit().putEncryptString(SP_NAME, value).apply()
            field = value
        }

    companion object {

        const val SP_ACCESS_TOKEN = "sp_access_token"
        const val SP_REFRESH_TOKEN = "sp_refresh_token"
        const val SP_MOBILE = "sp_mobile"
        const val SP_USER_ID = "sp_user_id"
        const val SP_NAME = "sp_name"

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var defaultInstance: Settings? = null

        fun create(): Settings {
            if (defaultInstance == null) {
                synchronized(Settings::class.java) {
                    if (defaultInstance == null) {
                        defaultInstance = Settings()
                    }
                }
            }
            return defaultInstance!!
        }
    }
}