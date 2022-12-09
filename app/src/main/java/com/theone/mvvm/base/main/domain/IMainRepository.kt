package com.theone.mvvm.base.main.domain

import com.theone.framework.base.IBaseRepository
import com.theone.mvvm.home.entity.HomeEn
import io.reactivex.rxjava3.core.Observable


/**
 * @Author ZhiQiang
 * @Date 2022/7/28
 * @Description
 */
interface IMainRepository : IBaseRepository {
    fun getData(): Observable<HomeEn>
}
