package com.theone.mvvm.base.main.presentation

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.juqitech.moretickets.main.presentation.MainViewModel
import com.theone.framework.base.BaseMultiViewActivity
import com.theone.mvvm.R

/**
 * @Author ZhiQiang
 * @Date 2022/7/28
 * @Description
 */
class MainActivity : BaseMultiViewActivity<MainViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

    }

    override fun onCreateViewModel(): MainViewModel {
        return ViewModelProvider(this).get(MainViewModel::class.java)
    }

}
