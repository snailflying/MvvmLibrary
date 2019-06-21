package com.themone.core.base

/**
 * @author zhiqiang
 * @date 2019-06-04
 * @desc
 */
interface IModel {
    /**
     * onDestroy 可用来 回收、取消网络请求等操作
     */
    fun onDestroy()
}
