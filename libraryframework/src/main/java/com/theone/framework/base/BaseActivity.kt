package com.theone.framework.base

import android.content.Context
import com.themone.core.base.impl.CoreActivity

import com.theone.framework.util.I18NUtil

/**
 * @Author zhiqiang
 * @Date 2019-06-19
 * @Email liuzhiqiang@theone.com
 * @Description
 */
open class BaseActivity : CoreActivity(), IFrameworkActivity {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(I18NUtil.updateResource(newBase))
    }

}