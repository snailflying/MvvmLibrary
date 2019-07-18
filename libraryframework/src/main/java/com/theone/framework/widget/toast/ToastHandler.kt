package com.theone.framework.widget.toast

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.Toast
import java.util.*
import java.util.concurrent.ArrayBlockingQueue

/**
 * @Author zhiqiang
 * @Email liuzhiqiang@theone.com
 * @Date 2019-06-22
 * @Description Toast 显示处理类
 */
internal class ToastHandler(// 吐司对象
    val mToast: Toast
) : Handler(Looper.getMainLooper()) {

    // 吐司队列
    private val mQueue: Queue<CharSequence> = ArrayBlockingQueue(MAX_TOAST_CAPACITY)


    // 当前是否正在执行显示操作
    @Volatile
    private var isShow: Boolean = false


    fun add(s: CharSequence) {
        if (mQueue.isEmpty() || !mQueue.contains(s)) {
            // 添加一个元素并返回true，如果队列已满，则返回false
            if (!mQueue.offer(s)) {
                // 移除队列头部元素并添加一个新的元素
                mQueue.poll()
                mQueue.offer(s)
            }
        }
    }

    fun show() {
        if (!isShow) {
            isShow = true
            // 延迟一段时间之后再执行，因为在没有通知栏权限的情况下，Toast 只能显示当前 Activity
            // 如果当前 Activity 在 ToastUtils.show 之后进行 finish 了，那么这个时候 Toast 可能会显示不出来
            // 因为 Toast 会显示在销毁 Activity 界面上，而不会显示在新跳转的 Activity 上面
            sendEmptyMessageDelayed(TYPE_SHOW, DELAY_TIMEOUT.toLong())
        }
    }

    fun cancel() {
        if (isShow) {
            isShow = false
            sendEmptyMessage(TYPE_CANCEL)
        }
    }

    override fun handleMessage(msg: Message) {
        when (msg.what) {
            TYPE_SHOW -> {
                // 返回队列头部的元素，如果队列为空，则返回null
                val text = mQueue.peek()
                if (text != null) {
                    mToast.setText(text)
                    mToast.show()
                    // 等这个 Toast 显示完后再继续显示，要加上一些延迟
                    // 不然在某些手机上 Toast 可能会来不及消失就要进行显示，这样是显示不出来的
                    sendEmptyMessageDelayed(TYPE_CONTINUE, (getToastDuration(text) + DELAY_TIMEOUT).toLong())
                } else {
                    isShow = false
                }
            }
            TYPE_CONTINUE -> {
                // 移除并返问队列头部的元素，如果队列为空，则返回null
                mQueue.poll()
                if (!mQueue.isEmpty()) {
                    sendEmptyMessage(TYPE_SHOW)
                } else {
                    isShow = false
                }
            }
            TYPE_CANCEL -> {
                isShow = false
                mQueue.clear()
                mToast.cancel()
            }
            else -> {
            }
        }
    }

    companion object {

        val SHORT_DURATION_TIMEOUT = 2000 // 短吐司显示的时长
        val LONG_DURATION_TIMEOUT = 3500 // 长吐司显示的时长

        private val DELAY_TIMEOUT = 300 // 延迟时间

        private val TYPE_SHOW = 1 // 显示吐司
        private val TYPE_CONTINUE = 2 // 继续显示
        private val TYPE_CANCEL = 3 // 取消显示

        // 队列最大容量
        private val MAX_TOAST_CAPACITY = 3

        /**
         * 根据文本来获取吐司的显示时间
         */
        private fun getToastDuration(text: CharSequence): Int {
            // 如果显示的文字超过了10个就显示长吐司，否则显示短吐司
            return if (text.length > 20) LONG_DURATION_TIMEOUT else SHORT_DURATION_TIMEOUT
        }
    }
}