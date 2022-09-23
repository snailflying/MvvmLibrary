package com.theone.mvvm.base.main.domain

import com.theone.framework.base.IBaseRepository


/**
 * @Author ZhiQiang
 * @Date 2022/7/28
 * @Description
 */
interface IMainRepository : IBaseRepository {
    fun getData(): String
}
