package com.theone.mvvm.base.main.domain

import com.theone.mvvm.base.main.data.MainDataLocal
import com.theone.framework.base.BaseRepository
import com.theone.mvvm.base.main.data.MainDataNet
import com.theone.mvvm.base.main.http.ApiService

/**
 * @Author ZhiQiang
 * @Date 2022/7/28
 * @Description
 */
class MainRepository(clazz: Class<ApiService>) : BaseRepository<ApiService>(clazz), IMainRepository {
    private val localData = MainDataLocal()
    private val netData = MainDataNet(apiService)
    override fun getData(): String {
        if (localData.getData() != null) {
            return localData.getData()!!
        }
        val data = netData.getData()
        localData.setData(data)
        return data
    }
}
