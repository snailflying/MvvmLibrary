package com.themone.core.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import androidx.palette.graphics.Palette
import com.themone.core.base.impl.BaseApp


/**
 * @author zhiqiang
 * @date 2019-06-04
 * @desc
 */
object StatusBarUtil {


    /**
     * 修改状态栏字体颜色
     * @param context Context
     * @param darkModeFlag Boolean 状态栏字体颜色
     */
    @JvmStatic
    fun compat(context: Context, darkModeFlag: Boolean) {
        if (context is Activity) {
            if (MIUISetStatusBarLightMode(context, darkModeFlag) || setStatusBarLightMode(context, darkModeFlag)) {
            } else {
                flymeSetStatusBarLightMode(context.window, darkModeFlag)
            }
        } else {
            throw IllegalStateException("context is not instance activity")
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
     * 设置透明状态栏
     */
    fun setTransparentForWindow(context: Context) {
        if (context is Activity) {
            val window = context.window
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                window.statusBarColor = Color.TRANSPARENT

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                val localLayoutParams = window.attributes
                localLayoutParams.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or localLayoutParams.flags
            }
        } else {
            throw IllegalStateException("context is not instance activity")
        }

    }

    /**
     * 设置填充状态栏高度
     * rootView.fitsSystemWindows = true时，系统自动给rootView一个状态栏高度的paddingTop
     */
    fun setFitsSystemWindows(context: Context) {
        if (context is Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                val parent = context.findViewById<ViewGroup>(android.R.id.content)
                val rootView = parent.getChildAt(0)
                if (null != rootView) {
                    //进入页面时，状态栏文字颜色自动改变
                    autoSetStatusBarTextColor(context,rootView)
                    rootView.fitsSystemWindows = true
                }
            }
        } else {
            throw IllegalStateException("context is not instance activity")
        }

    }

    /**
     * 设置填充状态栏高度
     */
    fun setFitsSystemWindows(context: Context, offsetView: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val statusBarHeight = getStatusBarHeight(context)
            offsetView.setPadding(
                offsetView.paddingLeft, offsetView.paddingTop + statusBarHeight,
                offsetView.paddingRight, offsetView.paddingBottom
            )
        }
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
    private fun flymeSetStatusBarLightMode(window: Window, dark: Boolean): Boolean {
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
    private fun MIUISetStatusBarLightMode(activity: Activity, dark: Boolean): Boolean {
        var result = false
        val window = activity.window
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
                        activity.window.decorView.systemUiVisibility =
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    } else {
                        activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    }
                }
                result = true
            } catch (e: Exception) {

            }

        }
        return result
    }

    @JvmStatic
    private fun setStatusBarLightMode(activity: Activity, isFontColorDark: Boolean): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isFontColorDark) {
                //非沉浸式
                activity.window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                //非沉浸式
                activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
            return true
        }
        return false
    }


    /****************状态栏颜色自动改变 Start***********************/
    /**
     * 状态栏颜色自动改变
     * @param activity Activity
     * @param view View?
     */
    @SuppressLint("StaticFieldLeak")
    fun autoSetStatusBarTextColor(activity: Activity, view: View?) {

        object : AsyncTask<View?, Void, Int>() {
            override fun doInBackground(vararg params: View?): Int {
                return try {
                    val statusBarBg =
                        getBitmapFromStatusBar(params[0], getAppScreenWidth(), getStatusBarHeight(BaseApp.application))
                    getColorFromBitmapSync(statusBarBg)
                } catch (e: Exception) {
                    Log.e("", "Exception thrown during async generate", e)
                    Color.WHITE
                }

            }

            override fun onPostExecute(bgColor: Int) {
                compat(activity, !isDarkColor(bgColor))
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, view)
    }

    /**
     * HSV跟HSL有点类似，HSV的优点是饱和度计算更符合人的感官，而HSL的优点是它对称于亮与暗，
     * HSL[0] 是色调(Hue)，取值范围是0到360；
     * HSL[1] 是饱和度( Saturation)，取值范围是0到1，值越高，颜色越接近光谱色；
     * HSL[2] 是明度( Value )，取值范围是0到1;
     * <p>
     * Lab颜色空间中的L分量用于表示像素的亮度，取值范围是[0,100],表示从纯黑到纯白；个人感觉70是分界线
     * a表示从红色到绿色的范围，取值范围是[127,-128]；
     * b表示从黄色到蓝色的范围，取值范围是[127,-128]
     * <p>
     * XYZ颜色空间，Y表示亮度，取值范围是[0,100],表示从纯黑到纯白
     * X component value [0...95.047)
     * Y component value [0...100)，个人感觉40是分界线
     * Z component value [0...108.883)
     *
     * @param color RGB颜色
     * @return 是否是暗色
     */
    private fun isDarkColor(@ColorInt color: Int): Boolean {
        val luminance = ColorUtils.calculateLuminance(color)
        return luminance < 0.4
    }

    /**
     * 获取状态栏背景Bitmap
     * @param v View?
     * @param height Int
     * @return Bitmap?
     */
    private fun getBitmapFromStatusBar(view: View?, width: Int, height: Int): Bitmap? {
        if (view == null) {
            return null
        }
        //StatusBar获取的bitmap是透明的，所以取StatusBar下方相同高度区域代替
        val bitmap: Bitmap = Bitmap.createBitmap(width, height*2, Bitmap.Config.ARGB_4444)
        val canvas = Canvas(bitmap)
        view.draw(canvas)

        val result = Bitmap.createBitmap(bitmap, 0, height, width, height)
        bitmap.recycle()
        return result
    }

    /**
     * 获取App宽度.
     *
     * @return the application's width of screen, in pixel
     */
    private fun getAppScreenWidth(): Int {
        val wm = BaseApp.application.getSystemService(Context.WINDOW_SERVICE) as WindowManager? ?: return -1
        val point = Point()
        wm.defaultDisplay.getSize(point)
        return point.x
    }

    /**
     * 注意：同步方法
     * @param bitmap Bitmap 图片
     * @return Int? 图片颜色
     */
    private fun getColorFromBitmapSync(bitmap: Bitmap?): Int {
        if (bitmap == null) return Color.WHITE

        val swatch = when {
            Palette.from(bitmap).generate().vibrantSwatch != null -> Palette.from(bitmap).generate().vibrantSwatch
            Palette.from(bitmap).generate().darkVibrantSwatch != null -> Palette.from(bitmap).generate().darkVibrantSwatch
            else -> Palette.from(bitmap).generate().dominantSwatch
        }
        return swatch?.rgb ?: Color.WHITE
    }
    /****************状态栏颜色自动改变 End***********************/

}
