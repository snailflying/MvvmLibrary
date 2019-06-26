package com.themone.core.entity

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
data class ApiResponse<T>(
    var data: T?,
    var statusCode: Int,
    var message: String
) : Serializable {

    val isSuccess: Boolean
        get() = statusCode == 200 && null != data

}
