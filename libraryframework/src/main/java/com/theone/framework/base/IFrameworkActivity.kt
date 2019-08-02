package com.theone.framework.base

import android.content.Context

/**
 * @Author zhiqiang
 * @Date 2019-08-02
 * @Email liuzhiqiang@moretickets.com
 * @Description 指示Framework层的Activity需要实现的接口
 */
interface IFrameworkActivity {
    fun attachBaseContext(newBase: Context)
}