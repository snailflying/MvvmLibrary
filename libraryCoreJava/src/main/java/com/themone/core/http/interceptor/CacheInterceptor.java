package com.themone.core.http.interceptor;

import android.text.TextUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class CacheInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        if (!TextUtils.isEmpty(response.header("Cache-Control"))) {
            return response.newBuilder()
                    .removeHeader("Pragma")
                    .build();
        } else {
            return response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    //cache for 300 秒
                    .header("Cache-Control", "max-age=" + 300)
                    .build();
        }
    }
}