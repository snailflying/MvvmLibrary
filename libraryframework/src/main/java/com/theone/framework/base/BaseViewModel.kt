package com.theone.framework.base

import com.themone.core.base.IModel
import com.themone.core.base.impl.CoreViewModel

/**
 * @Author zhiqiang
 * @Date 2019-08-02
 * @Email liuzhiqiang@moretickets.com
 * @Description
 */
abstract class BaseViewModel<M : IModel>: CoreViewModel<M>(),IFrameworkViewModel {
}