package com.juqitech.moretickets.core.base.impl;

import android.app.Application;

/**
 * @author zhanfeng
 * @date 2019-06-05
 * @desc
 */
public class BaseApp extends Application {

    private static BaseApp instance;


    public static synchronized BaseApp getApplication() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
