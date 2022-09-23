package com.themone.core.base.presentation

import androidx.appcompat.app.AppCompatActivity

/**
 * @Author ZhiQiang
 * @Date 2021/9/8
 * @Description 简单 Activity，无业务逻辑可继承此类
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