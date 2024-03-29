package com.theone.framework.widget.toast;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;

import com.theone.framework.widget.toast.config.IToastInterceptor;
import com.theone.framework.widget.toast.config.IToastStrategy;
import com.theone.framework.widget.toast.config.IToastStyle;
import com.theone.framework.widget.toast.style.BlackToastStyle;
import com.theone.framework.widget.toast.style.LocationToastStyle;
import com.theone.framework.widget.toast.style.ViewToastStyle;
import com.theone.framework.widget.toast.style.WhiteToastStyle;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/ToastUtils
 * time   : 2018/09/01
 * desc   : Toast 框架
 */
@SuppressWarnings("unused")
public final class ToastUtils {

    /**
     * Application 对象
     */
    private static Application sApplication;

    /**
     * Toast 处理策略
     */
    private static IToastStrategy sToastStrategy;

    /**
     * Toast 样式
     */
    private static IToastStyle<?> sToastStyle;

    /**
     * Toast 拦截器（可空）
     */
    private static IToastInterceptor sToastInterceptor;

    /**
     * 调试模式
     */
    private static Boolean sDebugMode;

    /**
     * 不允许被外部实例化
     */
    private ToastUtils() {
    }

    /**
     * 初始化 Toast，需要在 Application.create 中初始化
     *
     * @param application 应用的上下文
     */
    public static void init(Application application) {
        init(application, sToastStyle);
    }

    /**
     * 初始化 Toast 及样式
     */
    public static void init(Application application, IToastStyle<?> style) {
        sApplication = application;

        // 初始化 Toast 显示处理器
        if (sToastStrategy == null) {
            setStrategy(new ToastStrategy());
        }

        if (style == null) {
            style = new BlackToastStyle();
        }

        // 设置 Toast 样式
        setStyle(style);
    }

    /**
     * 判断当前框架是否已经初始化
     */
    public static boolean isInit() {
        return sApplication != null && sToastStrategy != null && sToastStyle != null;
    }

    public static void show(Context context, Object object) {
        show(object);
    }

    /**
     * 显示一个对象的吐司
     *
     * @param object 对象
     */
    public static void show(Object object) {
        show(object != null ? object.toString() : "null");
    }

    public static void debugShow(Object object) {
        if (!isDebugMode()) {
            return;
        }
        show(object);
    }

    public static void show(Context context, int id) {
        show(id);
    }

    /**
     * 显示一个吐司
     *
     * @param id 如果传入的是正确的 string id 就显示对应字符串
     *           如果不是则显示一个整数的string
     */
    public static void show(int id) {
        try {
            // 如果这是一个资源 id
            show(sApplication.getResources().getText(id));
        } catch (Resources.NotFoundException ignored) {
            // 如果这是一个 int 整数
            show(String.valueOf(id));
        }
    }

    public static void debugShow(int id) {
        if (!isDebugMode()) {
            return;
        }
        show(id);
    }

    public static void show(Context context, CharSequence text) {
        show(text);
    }

    /**
     * 显示一个吐司
     *
     * @param text 需要显示的文本
     */
    public static void show(CharSequence text) {
        // 如果是空对象或者空文本就不显示
        if (text == null || text.length() == 0) {
            return;
        }

        if (sToastInterceptor != null && sToastInterceptor.intercept(text)) {
            return;
        }

        sToastStrategy.showToast(text);
    }

    public static void debugShow(CharSequence text) {
        if (!isDebugMode()) {
            return;
        }
        show(text);
    }

    /**
     * 取消吐司的显示
     */
    public static void cancel() {
        sToastStrategy.cancelToast();
    }

    /**
     * 设置吐司的位置
     *
     * @param gravity 重心
     */
    public static void setGravity(int gravity) {
        setGravity(gravity, 0, 0);
    }

    public static void setGravity(int gravity, int xOffset, int yOffset) {
        setGravity(gravity, xOffset, yOffset, 0, 0);
    }

    public static void setGravity(int gravity, int xOffset, int yOffset, float horizontalMargin, float verticalMargin) {
        sToastStrategy.bindStyle(new LocationToastStyle(sToastStyle, gravity, xOffset, yOffset, horizontalMargin, verticalMargin));
    }

    /**
     * 给当前 Toast 设置新的布局
     */
    public static void setView(int id) {
        if (id <= 0) {
            return;
        }
        setStyle(new ViewToastStyle(id, sToastStyle));
    }

    /**
     * 初始化全局的 Toast 样式
     *
     * @param style 样式实现类，框架已经实现两种不同的样式
     *              黑色样式：{@link BlackToastStyle}
     *              白色样式：{@link WhiteToastStyle}
     */
    public static void setStyle(IToastStyle<?> style) {
        sToastStyle = style;
        sToastStrategy.bindStyle(style);
    }

    public static IToastStyle<?> getStyle() {
        return sToastStyle;
    }

    /**
     * 设置 Toast 显示策略
     */
    public static void setStrategy(IToastStrategy strategy) {
        sToastStrategy = strategy;
        sToastStrategy.registerStrategy(sApplication);
    }

    public static IToastStrategy getStrategy() {
        return sToastStrategy;
    }

    /**
     * 设置 Toast 拦截器（可以根据显示的内容决定是否拦截这个Toast）
     * 场景：打印 Toast 内容日志、根据 Toast 内容是否包含敏感字来动态切换其他方式显示（这里可以使用我的另外一套框架 XToast）
     */
    public static void setInterceptor(IToastInterceptor interceptor) {
        sToastInterceptor = interceptor;
    }

    public static IToastInterceptor getInterceptor() {
        return sToastInterceptor;
    }

    /**
     * 是否为调试模式
     */
    public static void setDebugMode(boolean debug) {
        sDebugMode = debug;
    }

    private static boolean isDebugMode() {
        if (sDebugMode == null) {
            sDebugMode = (sApplication.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        }
        return sDebugMode;
    }
}