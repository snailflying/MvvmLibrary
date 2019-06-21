package com.juqitech.moretickets.core.base;

/**
 * @author zhiqiang
 * @date 2019-06-04
 * @desc
 */
public interface IBaseModel {
    /**
     * onDestroy 可用来 回收、取消网络请求等操作
     */
    void onDestroy();
}
