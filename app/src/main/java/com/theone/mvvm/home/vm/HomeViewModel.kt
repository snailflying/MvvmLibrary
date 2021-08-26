package com.theone.mvvm.home.vm

import com.theone.framework.base.BaseViewModel
import com.theone.mvvm.home.model.HomeModel

/**
 * @Author zhiqiang
 * @Date 2019-08-13
 * @Email liuzhiqiang@moretickets.com
 * @Description
 */
class HomeViewModel() : BaseViewModel<HomeModel>() {
    override fun onCreateModel(): HomeModel {
        return HomeModel()
    }
}