package com.themone.core.base

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

/**
 * @author zhiqiang
 * @date 2019-06-03
 * @desc
 */
interface ILifecycleProvider : LifecycleObserver {

    /**
     * onCreate
     */
//    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
//    fun onCreate(owner: LifecycleOwner)

    /**
     * onStart
     */
//    @OnLifecycleEvent(Lifecycle.Event.ON_START)
//    fun onStart(owner: LifecycleOwner)

    /**
     * onResume
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume(owner: LifecycleOwner)

    /**
     * onPause
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause(owner: LifecycleOwner)

    /**
     * onStop
     */
//    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
//    fun onStop(owner: LifecycleOwner)

    /**
     * onDestroy
     */
//    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
//    fun onDestroy(owner: LifecycleOwner)

}
