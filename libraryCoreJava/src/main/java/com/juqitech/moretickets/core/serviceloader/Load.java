package com.juqitech.moretickets.core.serviceloader;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

/**
 * @Author zhiqiang
 * @Email liuzhiqiang@moretickets.com
 * @Date 2019-06-06
 * @Description
 */
class Load {

    private ClassLoader loader;
    private Enumeration<URL> configs = null;

    Load() {
        //初始化加载器
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (null != cl) {
            loader = cl;
        } else {
            loader = ClassLoader.getSystemClassLoader();
        }
    }

    //获取URL
    URL initLoad(String location) throws Exception {
        if (configs == null) {
            try {
                if (loader == null) {
                    configs = ClassLoader.getSystemResources(location);
                } else {
                    configs = loader.getResources(location);
                }
            } catch (IOException x) {
                x.printStackTrace();
            }
        }
        return configs.nextElement();
    }

    ClassLoader getLoader() {
        return loader;
    }
}
