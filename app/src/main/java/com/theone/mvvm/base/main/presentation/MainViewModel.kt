package com.juqitech.moretickets.main.presentation

import com.theone.mvvm.base.main.domain.IMainRepository
import com.theone.mvvm.base.main.domain.MainRepository
import com.theone.framework.base.BaseViewModel
import com.theone.mvvm.base.net.api.ApiService

/**
 * @author zhanfeng
 * @date 2019-06-05
 * @desc
 */
class MainViewModel : BaseViewModel<IMainRepository>() {

    fun triggerGetData() {
        repository.getData()
    }

    override fun onCreateRepository(): IMainRepository {
        return MainRepository(ApiService::class.java)
    }


}
