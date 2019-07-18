package com.theone.framework.widget.toast

import android.app.Application
import android.os.Handler
import android.os.Message
import android.view.WindowManager
import android.widget.Toast

/**
 * @Author zhiqiang
 * @Email liuzhiqiang@theone.com
 * @Date 2019-06-22
 * @Description Toast 显示安全处理
 */
internal class SafeToast(application: Application) : BaseToast(application) {

    init {

        // Hook mToast field
        try {
            val field_tn = Toast::class.java.getDeclaredField("mTN")
            field_tn.isAccessible = true

            val mTN = field_tn.get(this)
            val field_handler = field_tn.type.getDeclaredField("mHandler")
            field_handler.isAccessible = true

            val handler = field_handler.get(mTN) as Handler
            field_handler.set(mTN, SafeHandler(handler)) // 偷梁换柱

        } catch (ignored: Exception) {
        }

    }

    internal class SafeHandler(private val mHandler: Handler) : Handler() {

        override fun handleMessage(msg: Message) {
            // 捕获这个异常，避免程序崩溃
            try {
                /*
                 目前发现 Android 7.1 主线程被阻塞之后弹吐司会导致崩溃
                 查看源码得知 Google 已经在 8.0 已经修复了此问题
                 因为主线程阻塞之后 Toast 也会被阻塞
                 Toast 超时 Window token 会失效
                 可使用 Thread.sleep(5000) 进行复现
                 */
                mHandler.handleMessage(msg)
            } catch (ignored: WindowManager.BadTokenException) {
                // android.view.WindowManager$BadTokenException:
                // Unable to add window -- token android.os.BinderProxy@94ae84f is not valid; is your activity running?
            }

        }

        override fun dispatchMessage(msg: Message) {
            mHandler.dispatchMessage(msg)
        }
    }
}