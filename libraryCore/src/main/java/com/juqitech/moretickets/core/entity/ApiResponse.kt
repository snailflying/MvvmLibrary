package com.juqitech.moretickets.core.entity

import java.io.Serializable

/**
 * @author zhanfeng
 * @date 2019-06-05
 * @desc 请求得到的原始数据，data 为 object 类型
 */

/**
 * errorCode : 200
 * message : ""
 * pagination : {"count":9,"offset":0,"length":20,"sortingConditions":[],"total":9,"pageNo":1,"pageSize":20}
 * data : {}
 */
data class ApiResponse<T>(
    var data: T?,
    var errorCode: Int,
    var message: String,
    var pagination: PaginationEn? = null
) : Serializable {

    val isSuccess: Boolean
        get() = errorCode == 200 && null != data

}
