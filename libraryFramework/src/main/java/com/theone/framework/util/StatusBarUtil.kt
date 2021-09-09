package com.theone.framework.util

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import com.theone.framework.util.StatusBarUtil.compat
import com.theone.framework.util.StatusBarUtil.setFitsSystemWindows


/**
 * @author zhiqiang
 * @date 2019-06-04
 * @desc
 * [com.themone.framework.base.BaseActivity]和[com.themone.framework.base.BaseMvvmActivity]内如下调用：
 * 1.[setTransparentForWindow]设置状态栏透明
 * 2.[setFitsSystemWindows]将activity的rootView设置一个“系统padding”
 * 额外需要处理的情况：
 * 1.状态栏底色非rootView提供时，需要手动调用[addStatusBarOffsetForView]关闭“系统padding”，且给view提供一个statusBar高度padding
 * 2.遇到scrollView等状态栏底色动态改变的，需要手动调用[compat]改变状态栏内容颜色。
 *
 */
object StatusBarUtil {


    /**
     * 设置透明状态栏,[com.themone.framework.base.BaseActivity]和[com.themone.framework.base.BaseMvvmActivity]内调用
     */
    fun transparentStatusBar(activity: Activity) {
        val window = activity.window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //兼容5.0及以上支持全透明
            /*window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT*/
            transparentStatusBarForWindow(window)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val localLayoutParams = window.attributes
            localLayoutParams.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or localLayoutParams.flags
        }
    }

    /**
     * 透明状态栏
     *
     * @param window
     */
    fun transparentStatusBarForWindow(window: Window?) {

        if (window == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR)//确保窗口内容不会被装饰条（状态栏）盖住。
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)//弹出窗口占满整个屏幕，忽略周围的装饰边框（例如状态栏）

            val option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            val vis = window.decorView.systemUiVisibility
            window.decorView.systemUiVisibility = option or vis
            window.statusBarColor = Color.TRANSPARENT
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }

    /**
     * 设置填充状态栏高度,[com.juqitech.moretickets.core.base.impl.CoreActivity]内调用
     * rootView.fitsSystemWindows = true时，系统自动给rootView一个状态栏高度的paddingTop
     * 跟[addStatusBarHeightForView]互斥
     */
    internal fun setFitsSystemWindows(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val parent = activity.findViewById<ViewGroup>(android.R.id.content)
            val rootView = parent.getChildAt(0)
            if (null != rootView) {
                rootView.fitsSystemWindows = true
                if (rootView is ViewGroup) {
                    rootView.clipToPadding = true
                }
            }
        }
    }

    /**
     * 手动修改状态栏字体颜色
     * @param window Window
     * @param darkStatusBarText Boolean 状态栏字体颜色
     */

    @JvmStatic
    fun compat(context: Context, darkStatusBarText: Boolean) {
        if (context is Activity) {
            compat(context.window, darkStatusBarText)
        } else {
            throw IllegalStateException("context is not instance activity")
        }
    }

    /**
     * 手动修改状态栏字体颜色
     * @param context Context
     * @param darkStatusBarText Boolean 状态栏字体颜色
     */
    @JvmStatic
    fun compat(window: Window, darkStatusBarText: Boolean) {
        if (!setStatusBarLightMode(window, darkStatusBarText) && !setMiuiStatusBarLightMode(
                window,
                darkStatusBarText
            )
        ) {
            setFlymeStatusBarLightMode(window, darkStatusBarText)
        }
    }

    /**
     * 给具体view额外设置一个状态栏高度的padding
     * 跟[setFitsSystemWindows]互斥
     */
    fun addStatusBarHeightForView(offsetView: View?) {
        offsetView ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val layoutParams = offsetView.layoutParams as ViewGroup.MarginLayoutParams
            val statusBarHeight = getStatusBarHeight(offsetView.context)
            offsetView.setPadding(
                offsetView.paddingLeft, offsetView.paddingTop + statusBarHeight,
                offsetView.paddingRight, offsetView.paddingBottom
            )
            val height = layoutParams.height
            if (height >= 0) {
                layoutParams.height = height + statusBarHeight
            }
        }
    }

    /**
     * 设置底部导航栏是否可见
     */
    fun setStatusBarVisibility(context: Context, window: Window, isVisible: Boolean) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return
        }
        val decorView = window.decorView as ViewGroup
        var i = 0
        val count = decorView.childCount
        while (i < count) {
            val child = decorView.getChildAt(i)
            val id = child.id
            if (id != View.NO_ID) {
                val resourceEntryName = context.resources.getResourceEntryName(id)
                if ("navigationBarBackground" == resourceEntryName) {
                    child.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
                }
            }
            i++
        }
        val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        if (isVisible) {
            decorView.systemUiVisibility = decorView.systemUiVisibility and uiOptions.inv()
        } else {
            decorView.systemUiVisibility = decorView.systemUiVisibility or uiOptions
        }
    }

    /**
     * 获取 状态栏 高度
     */
    fun getStatusBarHeight(context: Context): Int {
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        return context.resources.getDimensionPixelSize(resourceId)
    }

    /**
     * 设置状态栏图标为深色和魅族特定的文字风格，Flyme4.0以上
     * 可以用来判断是否为Flyme用户
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    @JvmStatic
    private fun setFlymeStatusBarLightMode(window: Window, dark: Boolean): Boolean {
        var result = false
        try {
            val lp = window.attributes
            val darkFlag = WindowManager.LayoutParams::class.java
                .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
            val meizuFlags = WindowManager.LayoutParams::class.java
                .getDeclaredField("meizuFlags")
            darkFlag.isAccessible = true
            meizuFlags.isAccessible = true
            val bit = darkFlag.getInt(null)
            var value = meizuFlags.getInt(lp)
            if (dark) {
                value = value or bit
            } else {
                value = value and bit.inv()
            }
            meizuFlags.setInt(lp, value)
            window.attributes = lp
            result = true
        } catch (e: Exception) {

        }
        return result
    }

    /**
     * 需要MIUIV6以上
     *
     * @param activity
     * @param dark     是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    @JvmStatic
    private fun setMiuiStatusBarLightMode(window: Window?, dark: Boolean): Boolean {
        var result = false
        if (window != null) {
            val clazz = window.javaClass
            try {
                var darkModeFlag = 0
                val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
                val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
                darkModeFlag = field.getInt(layoutParams)
                val extraFlagField =
                    clazz.getMethod("setExtraFlags", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
                if (dark) {
                    //状态栏透明且黑色字体
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag)
                } else {
                    //清除黑色字体
                    extraFlagField.invoke(window, 0, darkModeFlag)
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //开发版 7.7.13 及以后版本采用了系统API，旧方法无效但不会报错，所以两个方式都要加上
                    if (dark) {
                        window.decorView.systemUiVisibility =
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    } else {
                        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    }
                }
                result = true
            } catch (e: Exception) {

            }

        }
        return result
    }

    @JvmStatic
    private fun setStatusBarLightMode(window: Window, isFontColorDark: Boolean): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isFontColorDark) {
                //非沉浸式
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                //非沉浸式
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
            return true
        }
        return false
    }
}
