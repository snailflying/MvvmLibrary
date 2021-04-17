package com.theone.framework.http.exception

/**
 * @Author zhiqiang
 * @Date 2019-06-20
 * @Email liuzhiqiang@theone.com
 * @Description
 */
class NetworkException(val code: Int, val msg: String) : Exception("|CODE:$code|MESSAGE:$msg")