package com.theone.mvvm.base.main.data

import com.theone.mvvm.base.net.api.ApiService
import com.theone.mvvm.home.entity.HomeEn
import io.reactivex.rxjava3.core.Observable

/**
 * @Author ZhiQiang
 * @Date 2022/7/28
 * @Description
 */
class MainDataNet(private val apiService: ApiService) {
    fun getData(): Observable<HomeEn> {
        return apiService.getMock()
    }
}