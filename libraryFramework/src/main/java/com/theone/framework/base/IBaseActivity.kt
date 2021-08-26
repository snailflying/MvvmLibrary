package com.theone.framework.base

import android.content.Context
import io.reactivex.rxjava3.disposables.CompositeDisposable

/**
 * @Author zhiqiang
 * @Date 2019-08-02
 * @Description 指示Framework层的Activity需要实现的接口
 */
interface IBaseActivity {
    val compositeDisposable: CompositeDisposable
    fun attachBaseContext(newBase: Context)
}