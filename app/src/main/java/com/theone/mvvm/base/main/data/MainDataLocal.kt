package com.theone.mvvm.base.main.data

import com.theone.mvvm.home.entity.HomeEn

/**
 * @Author ZhiQiang
 * @Date 2022/7/28
 * @Description
 */
class MainDataLocal {
    private var localData: HomeEn? = null
    fun getData(): HomeEn? {
        return localData
    }

    fun setData(data: HomeEn) {
        localData = data
    }
}