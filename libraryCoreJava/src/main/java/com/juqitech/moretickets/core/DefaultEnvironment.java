package com.juqitech.moretickets.core;

import androidx.annotation.NonNull;
import com.juqitech.moretickets.core.base.impl.BaseApp;
import com.juqitech.moretickets.core.http.interceptor.CacheInterceptor;
import com.juqitech.moretickets.core.util.LogUtil;
import com.juqitech.moretickets.library.BuildConfig;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author zhiqiang
 * @date 2019-06-05
 * @desc
 */
public class DefaultEnvironment implements IAppEnvironment {
    /**
     * 连接超时时间
     */
    private static final int DEFAULT_TIME_OUT = 10;
    /**
     * 读操作超时时间
     */
    private static final int DEFAULT_READ_TIME_OUT = 10;

    @Override
    @NonNull
    public Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(getApiBaseServiceUrl())
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    @Override
    @NonNull
    public OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS)
                .cache(getCache());

        for (Interceptor interceptor : getInterceptors()) {
            builder.addInterceptor(interceptor);
        }
        for (Interceptor interceptor : getNetworkInterceptors()) {
            builder.addNetworkInterceptor(interceptor);
        }

        if (BuildConfig.DEBUG) {
            SSLSocketFactoryImp ssl = null;
            try {
                ssl = new SSLSocketFactoryImp(KeyStore.getInstance(KeyStore.getDefaultType()));
            } catch (Exception e) {
                LogUtil.d("SSLSocketFactory", "ssl:" + e.getMessage());
            }
            if (null != ssl) {
                builder.sslSocketFactory(ssl.getSSLContext().getSocketFactory(), ssl.getTrustManager());
            }
        }

        return builder.build();
    }


    @Override
    @NonNull
    public String getApiBaseServiceUrl() {
        return "http://www.moretickets.com";
    }


    @Override
    @NonNull
    public List<Interceptor> getInterceptors() {
        List<Interceptor> interceptors = new ArrayList<>();
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            interceptors.add(logging);
        }
        return interceptors;
    }

    @Override
    @NonNull
    public List<Interceptor> getNetworkInterceptors() {
        List<Interceptor> interceptors = new ArrayList<>();
        CacheInterceptor cacheInterceptor = new CacheInterceptor();
        interceptors.add(cacheInterceptor);
        return interceptors;
    }

    @Override
    @NonNull
    public Cache getCache() {
        return new Cache(BaseApp.getApplication().getCacheDir(), 10240 * 1024);
    }


}
