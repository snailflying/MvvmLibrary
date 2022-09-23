package com.theone.mvvm.base.main.data

import com.theone.mvvm.base.main.http.ApiService

/**
 * @Author ZhiQiang
 * @Date 2022/7/28
 * @Description
 */
class MainDataNet(private val apiService: ApiService) {
    fun getData(): String {
        return apiService.getData()
    }
}