package com.themone.core.base.impl

import androidx.appcompat.app.AppCompatActivity

/**
 * @author zhiqiang
 * @date 2019-06-04
 * @desc 简单 Activity，无业务逻辑可继承此类
 */
open class CoreActivity : AppCompatActivity() {

    /**
     * Fragment返回键处理，见[CoreFragment.onBackPressed]
     */
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 0) {
            super.onBackPressed()
        } else {
            try {
                supportFragmentManager.popBackStackImmediate()
            } catch (e: Exception) {
            }
        }
    }
}