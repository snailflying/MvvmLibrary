package com.shownow.shownow.base.constant

/**
 * @author zhanfeng
 * @date 2019-06-05
 * @desc
 */
object HttpStatusCode {

    val NETWORK_EXCEPTION = -1000
    val NOT_NETWORK_EXCEPTION = -1
    val PARSE_EXCEPTION = -2
    val EXCEPTION = -10
    val SUCCESS = 200
    val SESSION_EXPIRED = 1003
    val LOGIN_EXPIRED = 1006
    val REFRESH_TOKEN_EXPIRED = 1042
    val REFRESH_SESSION_FAILURE = -1005

}