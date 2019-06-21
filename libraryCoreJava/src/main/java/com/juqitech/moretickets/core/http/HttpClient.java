package com.juqitech.moretickets.core.http;

import androidx.annotation.NonNull;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * @author zhiqiang
 * @date 2019-06-04
 * @desc
 */
public class HttpClient {

    public static <T> T create(@NonNull Class<T> clazz) {
        return getRetrofit().create(clazz);
    }

    public static Retrofit getRetrofit() {
        return RetrofitFactory.getInstance().getRetrofit();
    }

    public static OkHttpClient getOkHttpClient() {
        return RetrofitFactory.getInstance().getOkHttpClient();
    }

}
