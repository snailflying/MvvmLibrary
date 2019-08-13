package com.theone.mvvm.base.user

import com.themone.core.http.HttpClient
import com.theone.mvvm.base.user.entity.LoginEn
import java.io.Serializable

/**
 * @Author zhiqiang
 * @Email liuzhiqiang@moretickets.com
 * @Date 2019-06-11
 * @Description
 */
class User private constructor() : Serializable {

    var accessToken: String = ""
        get() = if (field.isNotBlank()) field else Settings.create().accessToken
        set(value) {
            Settings.create().accessToken = value
            field = value
        }
    var refreshToken: String = ""
        get() = if (field.isNotBlank()) field else Settings.create().refreshToken
        set(value) {
            Settings.create().refreshToken = value
            field = value
        }
    var cellphone: String = ""
        get() = if (field.isNotBlank()) field else Settings.create().cellphone
        set(value) {
            Settings.create().cellphone = value
            field = value
        }

    var userId: String = ""
        get() = if (field.isNotBlank()) field else Settings.create().userId
        set(value) {
            Settings.create().userId = value
            field = value
        }

    var name: String = ""
        get() = if (field.isNotBlank()) field else Settings.create().name
        set(value) {
            Settings.create().name = value
            field = value
        }


    init {
        if (Settings.create().accessToken.isNotBlank())
            this.accessToken = Settings.create().accessToken
        if (Settings.create().refreshToken.isNotBlank())
            this.refreshToken = Settings.create().refreshToken
        if (Settings.create().cellphone.isNotBlank())
            this.cellphone = Settings.create().cellphone
        if (Settings.create().userId.isNotBlank())
            this.userId = Settings.create().userId
        if (Settings.create().name.isNotBlank())
            this.name = Settings.create().name
    }

    /**
     * 是否登录
     * @return Boolean
     */
    fun isLogin(): Boolean = accessToken.isNotBlank()

    /**
     * 登录
     * @param userLogin LoginEn
     */
    fun login(userLogin: LoginEn) {

        //init
        this.cellphone = userLogin.userVO?.cellphone ?: ""
        this.accessToken = userLogin.token ?: ""
        //fixme: 刷新token暂时没有
        this.refreshToken = userLogin.refreshToken ?: ""
        this.userId = userLogin.userVO?.userId ?: ""
        this.name = userLogin.userVO?.name ?: ""
        currentUser = this@User
    }

    /**
     * 退出登录
     */
    fun logout() {
        accessToken = ""
        refreshToken = ""
        userId = ""
        cellphone = ""
        name = ""
        currentUser = User()

        //退出登录时删除网络缓存
        HttpClient.okHttpClient.cache()?.delete()
    }

    /**
     * {@kotlin User.currentUser.isLogin()}
     * {@java User.Companion.getCurrentUser().isLogin();}
     */
    companion object {
        var currentUser: User = User()
    }


}