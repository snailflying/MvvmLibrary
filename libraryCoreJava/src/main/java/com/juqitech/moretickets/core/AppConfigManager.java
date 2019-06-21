package com.juqitech.moretickets.core;

import com.juqitech.moretickets.core.serviceloader.ServicesLoader;

/**
 * @author zhanfeng
 * @date 2019-06-05
 * @desc 获取 app 环境配置信息
 * @see ServicesLoader
 */
public class AppConfigManager {

    private static volatile IAppEnvironment instance;

    private AppConfigManager() {
    }

    public static IAppEnvironment getAppEnvironment() {
        if (null == instance) {
            synchronized (AppConfigManager.class) {
                if (null == instance) {
                    instance = ServicesLoader.getService(IAppEnvironment.class, DefaultEnvironment.class);
                }
            }
        }
        return instance;
    }
}
