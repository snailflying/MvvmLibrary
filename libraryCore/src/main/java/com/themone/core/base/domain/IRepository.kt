package com.themone.core.base.domain

/**
 * @Author ZhiQiang
 * @Date 2022/7/28
 * @Description
 */
interface IRepository {
    /**
     * onDestroy 可用来 回收、取消网络请求等操作
     */
    fun onDestroy()
}
