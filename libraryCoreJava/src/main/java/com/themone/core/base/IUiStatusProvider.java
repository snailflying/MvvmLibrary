package com.themone.core.base;

/**
 * @author zhiqiang
 * @date 2019-06-06
 * @desc loading、error 等通过弹窗
 */
public interface IUiStatusProvider {

    /**
     * loading 页面
     */
    void onStatusLoading();

    /**
     * 网络异常，请求失败
     */
    void onStatusHttpError();

    /**
     * 空数据
     */
    void onStatusEmpty();

    /**
     * 正常数据页面
     */
    void onStatusMain();

    /**
     * 请求成功，后台服务异常
     */
    void onStatusServiceEx();
}
