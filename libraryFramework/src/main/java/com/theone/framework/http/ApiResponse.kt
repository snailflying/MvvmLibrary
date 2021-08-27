package com.theone.framework.http

import java.io.Serializable

/**
 * @author zhiqiang
 * @date 2019-06-05
 * @desc 请求得到的原始数据，data 为 object 类型
 */

/**
 * statusCode : 200
 * message : ""
 * pagination : {"count":9,"offset":0,"length":20,"sortingConditions":[],"total":9,"pageNo":1,"pageSize":20}
 * data : {}
 */
open class ApiResponse<T> : Serializable {
    constructor()
    constructor(data: T?, statusCode: Int, message: String?) {
        this.data = data
        this.statusCode = statusCode
        this.message = message
    }

    var data: T? = null
    var statusCode: Int = 0
    var message: String? = null

    /**
     * 用来拓展属性，比如pagination
     */
    var ext: Any? = null
    val isSuccess: Boolean
        get() = statusCode == 200

}