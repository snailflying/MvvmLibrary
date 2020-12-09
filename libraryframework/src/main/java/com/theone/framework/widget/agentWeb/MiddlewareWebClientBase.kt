/*
 * Copyright (C)  Justson(https://github.com/Justson/AgentWeb)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.theone.framework.widget.agentWeb

import android.webkit.WebViewClient

/**
 * @Author ZhiQiang
 * @Date 2020/11/15
 * @Description
 */
open class MiddlewareWebClientBase : WebViewClientDelegate {
    private var mMiddleWrareWebClientBase: MiddlewareWebClientBase? = null
    final override fun setDelegate(delegate: WebViewClient) {
        super.setDelegate(delegate)
    }

    internal constructor(client: MiddlewareWebClientBase?) : super() {
        mMiddleWrareWebClientBase = client
    }

    protected constructor(client: WebViewClient?) : super() {}
    protected constructor() : super() {}

    operator fun next(): MiddlewareWebClientBase? {
        return mMiddleWrareWebClientBase
    }

    fun enq(middleWrareWebClientBase: MiddlewareWebClientBase): MiddlewareWebClientBase? {
        setDelegate(middleWrareWebClientBase)
        mMiddleWrareWebClientBase = middleWrareWebClientBase
        return middleWrareWebClientBase
    }

    companion object {
        private val TAG = MiddlewareWebClientBase::class.java.simpleName
    }
}