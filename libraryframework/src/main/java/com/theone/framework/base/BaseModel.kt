package com.theone.framework.base

import com.themone.core.base.impl.CoreModel

/**
 * @Author zhiqiang
 * @Date 2019-08-02
 * @Email liuzhiqiang@moretickets.com
 * @Description
 */
class BaseModel<T>(clazz: Class<T>) : CoreModel<T>(clazz), IFrameworkModel {
}