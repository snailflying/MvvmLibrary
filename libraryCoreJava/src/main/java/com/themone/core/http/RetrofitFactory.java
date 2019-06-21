package com.themone.core.http;

import com.themone.core.AppConfigManager;
import com.themone.core.IAppEnvironment;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * @author zhiqiang
 * @date 2019-06-04
 * @desc RetrofitFactory
 * 非 Public 类型，不对外公开，必须通过 HttpClient 获取
 */
class RetrofitFactory {

    /**
     * 连接超时时间
     */
    private static final int DEFAULT_TIME_OUT = 10;
    /**
     * 读操作超时时间
     */
    private static final int DEFAULT_READ_TIME_OUT = 5;

    private OkHttpClient mOkHttpClient;
    private Retrofit mRetrofit;
    private IAppEnvironment appEnvironment;

    private RetrofitFactory() {
        appEnvironment = AppConfigManager.getAppEnvironment();
    }

    static RetrofitFactory getInstance() {
        return RetrofitManagerHelper.INSTANCE;
    }

    private static class RetrofitManagerHelper {
        private static final RetrofitFactory INSTANCE = new RetrofitFactory();
    }

    Retrofit getRetrofit() {
        if (null == mRetrofit) {
            mRetrofit = appEnvironment.getRetrofit();
        }
        return mRetrofit;
    }

    OkHttpClient getOkHttpClient() {
        return provideOkHttpClient();
    }

    private OkHttpClient provideOkHttpClient() {
        if (null == mOkHttpClient) {
            mOkHttpClient = appEnvironment.getOkHttpClient();
        }
        return mOkHttpClient;
    }


}
