package com.theone.framework.base

import android.content.Context
import com.themone.core.base.IViewModel
import com.themone.core.base.impl.CoreMultiViewActivity
import com.theone.framework.util.I18NUtil

/**
 * @Author zhiqiang
 * @Date 2019-06-19
 * @Email liuzhiqiang@moretickets.com
 * @Description
 */
abstract class BaseMultiViewActivity<VM : IViewModel> : CoreMultiViewActivity<VM>() {
    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(I18NUtil.updateResource(newBase))
    }
}