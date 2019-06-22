package com.theone.framework.base

import android.content.Context
import com.themone.core.base.IViewModel
import com.themone.core.base.impl.CoreMvvmActivity
import com.theone.framework.util.I18NUtil

/**
 * @Author zhiqiang
 * @Date 2019-06-19
 * @Email liuzhiqiang@theone.com
 * @Description
 */
abstract class BaseMvvmActivity<VM : IViewModel> : CoreMvvmActivity<VM>() {

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(I18NUtil.updateResource(newBase))
    }
}