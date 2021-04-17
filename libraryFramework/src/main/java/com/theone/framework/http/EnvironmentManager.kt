package com.theone.framework.http

import com.themone.core.serviceloader.ServicesLoader

/**
 * @author zhiqiang
 * @date 2019-06-05
 * @desc 获取 app 环境配置信息
 * @see ServicesLoader
 */
object EnvironmentManager {

    val environment: IAppEnvironment by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        ServicesLoader.getService(IAppEnvironment::class.java, DefaultEnvironment::class.java)!!
    }

/*    @Volatile
    private var environment: IAppEnvironment? = null

    val appEnvironment: IAppEnvironment?
        get() {
            if (null == environment) {
                synchronized(AppConfigManager::class.java) {
                    if (null == environment) {
                        environment = ServicesLoader.getService(IAppEnvironment::class.java, DefaultEnvironment::class.java)
                    }
                }
            }
            return environment
        }*/
}
