package com.juqitech.moretickets.core.base;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

/**
 * @author zhanfeng
 * @date 2019-06-03
 * @desc
 */
public interface ILifecycleProvider extends LifecycleObserver {

    /**
     * onCreate
     */
//    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
//    void onCreate(LifecycleOwner owner);

    /**
     * onStart
     */
//    @OnLifecycleEvent(Lifecycle.Event.ON_START)
//    void onStart(LifecycleOwner owner);

    /**
     * onResume
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void onResume(LifecycleOwner owner);

    /**
     * onPause
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    void onPause(LifecycleOwner owner);

    /**
     * onStop
     */
//    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
//    void onStop(LifecycleOwner owner);

    /**
     * onDestroy
     */
//    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
//    void onDestroy(LifecycleOwner owner);

}
