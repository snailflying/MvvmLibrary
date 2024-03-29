package com.theone.mvvm.utils

import com.theone.framework.http.ApiResponse
import com.theone.framework.http.exception.NetworkException
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.MaybeTransformer
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * @Author zhiqiang
 * @Date 2019-06-27
 * @Description Rx相关工具类
 */
object RxUtil {

    /**
     * 1.预处理response数据
     * 处理后的数据： T
     */
    @JvmStatic
    fun <T> responseData(): ObservableTransformer<ApiResponse<T>, T> {
        return ObservableTransformer { upstream ->
            upstream.map {
                if (it.isSuccess) {
                    it.data
                } else {
                    throw NetworkException(it.statusCode, it.message?:"")
                }
            }
        }
    }

    /**
     * 1.预处理response数据
     * 2.线程切换
     * 处理后的数据： T
     */
    @JvmStatic
    fun <T> responseDataToMain(): ObservableTransformer<ApiResponse<T>, T> {
        return ObservableTransformer { upstream ->
            upstream.map {
                if (it.isSuccess) {
                    it.data
                } else {
                    throw NetworkException(it.statusCode, it.message?:"")
                }
            }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }
    }

    /**
     * 1.预处理response数据
     * 处理后的数据： ApiResponse<T>
     */
    @JvmStatic
    fun <T> response(): ObservableTransformer<ApiResponse<T>, ApiResponse<T>> {
        return ObservableTransformer { upstream ->
            upstream.map {
                if (it.isSuccess) {
                    it
                } else {
                    throw NetworkException(it.statusCode, it.message?:"")
                }
            }
        }
    }

    /**
     * 1.预处理response数据
     * 2.线程切换
     * 处理后的数据： ApiResponse<T>
     */
    @JvmStatic
    fun <T> responseToMain(): ObservableTransformer<ApiResponse<T>, ApiResponse<T>> {
        return ObservableTransformer { upstream ->
            upstream.map {
                if (it.isSuccess) {
                    it
                } else {
                    throw NetworkException(it.statusCode, it.message?:"")
                }
            }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }
    }

    @JvmStatic
    fun <T> observableToMain(): ObservableTransformer<T, T> {

        return ObservableTransformer { upstream ->
            upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }
    }

    @JvmStatic
    fun <T> maybeToMain(): MaybeTransformer<T, T> {

        return MaybeTransformer { upstream ->
            upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }
    }

}