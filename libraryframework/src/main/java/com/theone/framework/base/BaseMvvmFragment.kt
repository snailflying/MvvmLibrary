package com.theone.framework.base

import com.themone.core.base.IViewModel
import com.themone.core.base.impl.CoreMvvmFragment


/**
 * @Author zhiqiang
 * @Date 2019-06-19
 * @Email liuzhiqiang@theone.com
 * @Description
 */
abstract class BaseMvvmFragment<VM : IViewModel> : CoreMvvmFragment<VM>(),IFrameworkFragment