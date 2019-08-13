package com.theone.mvvm.base.user.entity

import java.io.Serializable

class LoginEn : Serializable {
    var token: String? = null
    var refreshToken: String? = null
    var userVO: UserVO? = null

    class UserVO {
        var userId: String? = null
        var name: String? = null
        var cellphone: String? = null
        var email: String? = null
    }

}