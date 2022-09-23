package com.theone.mvvm.base.main.data

/**
 * @Author ZhiQiang
 * @Date 2022/7/28
 * @Description
 */
class MainDataLocal {
    private var localData: String? = null
    fun getData(): String? {
        return localData
    }

    fun setData(data: String) {
        localData = data
    }
}