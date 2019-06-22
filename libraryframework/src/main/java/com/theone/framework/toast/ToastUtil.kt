package com.theone.framework.toast

import android.app.AppOpsManager
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.theone.framework.toast.style.IToastStyle
import com.theone.framework.toast.style.ToastBlackStyle
import com.theone.framework.toast.BaseToast
import java.lang.reflect.InvocationTargetException

/**
 * @Author zhiqiang
 * @Email liuzhiqiang@theone.com
 * @Date 2019-06-22
 * @Description Toast工具类
 * @From https://github.com/getActivity/ToastUtils
 */
object ToastUtil {

    private var sToastHandler: ToastHandler? = null

    private var sDefaultStyle: IToastStyle? = null

    /**
     * 初始化 ToastUtils 及样式
     */
    fun init(application: Application, style: IToastStyle) {
        initStyle(style)
        init(application)
    }

    /**
     * 初始化 ToastUtils，在Application中初始化
     *
     * @param application       应用的上下文
     */
    fun init(application: Application) {
        // 初始化样式
        if (sDefaultStyle == null) {
            // 如果样式没有指定就初始化一个默认的样式
            initStyle(ToastBlackStyle())
        }

        // 初始化吐司
        if (isNotificationEnabled(application)) {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
                // 解决 Android 7.1 上主线程被阻塞后吐司会报错的问题
                setToast(SafeToast(application))
            } else {
                setToast(BaseToast(application))
            }
        } else {
            // 解决关闭通知栏权限后 Toast 不显示的问题
            setToast(SupportToast(application))
        }

        // 初始化布局
        setView(createTextView(application.applicationContext))

        // 初始化位置
        setGravity(sDefaultStyle!!.gravity, sDefaultStyle!!.xOffset, sDefaultStyle!!.yOffset)
    }


    /**
     * 显示一个吐司
     *
     * @param text      需要显示的文本
     */
    @Synchronized
    fun show(text: CharSequence?) {
        checkToastState()

        if (text == null || "" == text.toString()) return

        sToastHandler?.add(text)
        sToastHandler?.show()
    }

    /**
     * 设置当前Toast对象
     */
    fun setToast(mToast: Toast) {
        // 创建一个吐司处理类
        sToastHandler = ToastHandler(mToast)
    }

    /**
     * 取消吐司的显示
     */
    @Synchronized
    fun cancel() {
        checkToastState()

        sToastHandler?.cancel()
    }

    /**
     * 设置吐司的位置
     *
     * @param gravity           重心
     * @param xOffset           x轴偏移
     * @param yOffset           y轴偏移
     */
    fun setGravity(gravity: Int, xOffset: Int, yOffset: Int) {
        if (sToastHandler == null) return
        var gravityInner = gravity
        checkToastState()

        // 适配 Android 4.2 新特性，布局反方向（开发者选项 - 强制使用从右到左的布局方向）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            gravityInner = Gravity.getAbsoluteGravity(
                gravityInner,
                sToastHandler!!.mToast.view.resources.configuration.layoutDirection
            )
        }

        sToastHandler!!.mToast.setGravity(gravityInner, xOffset, yOffset)
    }

    /**
     * 给当前Toast设置新的布局，具体实现可看[BaseToast.setView]
     */
    fun setView(layoutId: Int) {
        if (sToastHandler == null) return
        checkToastState()

        setView(View.inflate(sToastHandler!!.mToast.view.context.applicationContext, layoutId, null))
    }

    fun setView(view: View?) {
        checkToastState()

        // 这个 View 不能为空
        if (view == null) {
            throw IllegalArgumentException("Views cannot be empty")
        }

        // 当前必须用 Application 的上下文创建的 View，否则可能会导致内存泄露
        if (view.context !== view.context.applicationContext) {
            throw IllegalArgumentException("The view must be initialized using the context of the application")
        }

        // 如果吐司已经创建，就重新初始化吐司
        if (sToastHandler?.mToast != null) {
            // 取消原有吐司的显示
            sToastHandler!!.mToast.cancel()
            sToastHandler!!.mToast.view = view
        }
    }

    /**
     * 获取当前 Toast 的视图
     */
    fun <V : View> getView(): V? {
        checkToastState()

        return sToastHandler?.mToast?.view as V?
    }

    /**
     * 统一全局的Toast样式，建议在[android.app.Application.onCreate]中初始化
     *
     * @param style         样式实现类，框架已经实现三种不同的样式
     * 黑色样式：[ToastBlackStyle]
     */
    fun initStyle(style: IToastStyle) {
        sDefaultStyle = style
        // 如果吐司已经创建，就重新初始化吐司
        if (sToastHandler?.mToast != null) {
            // 取消原有吐司的显示
            sToastHandler!!.mToast.cancel()
            sToastHandler!!.mToast.view = createTextView(sToastHandler!!.mToast.view.context.applicationContext)
            sToastHandler!!.mToast.setGravity(
                sDefaultStyle!!.gravity,
                sDefaultStyle!!.xOffset,
                sDefaultStyle!!.yOffset
            )
        }
    }

    /**
     * 检查吐司状态，如果未初始化请先调用[ToastUtil.init]
     */
    private fun checkToastState() {
        // 吐司工具类还没有被初始化，必须要先调用init方法进行初始化
        if (sToastHandler?.mToast == null) {
            throw IllegalStateException("ToastUtils has not been initialized")
        }
    }

    /**
     * 生成默认的 TextView 对象
     */
    private fun createTextView(context: Context): TextView {

        val drawable = GradientDrawable()
        // 设置背景色
        drawable.setColor(sDefaultStyle!!.backgroundColor)
        // 设置圆角大小
        drawable.cornerRadius = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            sDefaultStyle!!.cornerRadius.toFloat(),
            context.resources.displayMetrics
        )

        val textView = TextView(context)
        textView.id = android.R.id.message
        textView.setTextColor(sDefaultStyle!!.textColor)
        textView.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                sDefaultStyle!!.textSize,
                context.resources.displayMetrics
            )
        )

        textView.setPadding(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                sDefaultStyle!!.paddingLeft.toFloat(),
                context.resources.displayMetrics
            ).toInt(),
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                sDefaultStyle!!.paddingTop.toFloat(),
                context.resources.displayMetrics
            ).toInt(),
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                sDefaultStyle!!.paddingRight.toFloat(),
                context.resources.displayMetrics
            ).toInt(),
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                sDefaultStyle!!.paddingBottom.toFloat(),
                context.resources.displayMetrics
            ).toInt()
        )

        textView.layoutParams =
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        // setBackground API 版本兼容
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            textView.background = drawable
        } else {
            textView.setBackgroundDrawable(drawable)
        }

        // 设置 Z 轴阴影
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textView.z = sDefaultStyle!!.z.toFloat()
        }

        // 设置最大显示行数
        if (sDefaultStyle!!.maxLines > 0) {
            textView.maxLines = sDefaultStyle!!.maxLines
        }

        return textView
    }

    /**
     * 检查通知栏权限有没有开启
     * 参考 SupportCompat 包中的方法： NotificationManagerCompat.from(context).areNotificationsEnabled();
     */
    private fun isNotificationEnabled(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).areNotificationsEnabled()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val appInfo = context.applicationInfo
            val pkg = context.applicationContext.packageName
            val uid = appInfo.uid

            try {
                val appOpsClass = Class.forName(AppOpsManager::class.java.name)
                val checkOpNoThrowMethod =
                    appOpsClass.getMethod("checkOpNoThrow", Integer.TYPE, Integer.TYPE, String::class.java)
                val opPostNotificationValue = appOpsClass.getDeclaredField("OP_POST_NOTIFICATION")
                val value = opPostNotificationValue.get(Int::class.java) as Int
                return checkOpNoThrowMethod.invoke(appOps, value, uid, pkg) as Int == 0
            } catch (ignored: NoSuchMethodException) {
                return true
            } catch (ignored: NoSuchFieldException) {
                return true
            } catch (ignored: InvocationTargetException) {
                return true
            } catch (ignored: IllegalAccessException) {
                return true
            } catch (ignored: RuntimeException) {
                return true
            } catch (ignored: ClassNotFoundException) {
                return true
            }

        } else {
            return true
        }
    }
}