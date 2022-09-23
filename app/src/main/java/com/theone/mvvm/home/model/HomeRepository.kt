package com.theone.mvvm.home.model

import com.theone.framework.base.BaseRepository
import com.theone.mvvm.base.net.api.ApiService

/**
 * @Author zhiqiang
 * @Date 2019-08-13
 * @Email liuzhiqiang@theone.com
 * @Description
 */
class HomeRepository:BaseRepository<ApiService>(ApiService::class.java) {
}