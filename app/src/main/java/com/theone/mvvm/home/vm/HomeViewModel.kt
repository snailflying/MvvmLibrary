package com.theone.mvvm.home.vm

import com.theone.framework.base.BaseViewModel
import com.theone.mvvm.home.model.HomeRepository

/**
 * @Author zhiqiang
 * @Date 2019-08-13
 * @Description
 */
class HomeViewModel() : BaseViewModel<HomeRepository>() {

    override fun onCreateRepository(): HomeRepository {
        return HomeRepository()
    }
}