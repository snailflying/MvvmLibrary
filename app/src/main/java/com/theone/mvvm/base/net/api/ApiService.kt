package com.theone.mvvm.base.net.api

import com.theone.mvvm.home.entity.HomeEn
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * @Author zhiqiang
 * @Email liuzhiqiang@theone.com
 * @Date 2019-06-19
 * @Description
 */
interface ApiService {

    companion object {

        /**
         * 最后一个字符必须带"/"
         */
        const val BASE_URL_ONLINE = "https://appapi.shownow.live/shownowapi/"
        const val BASE_URL_QA = "https://appapiqa666.shownow.live/shownowapi/"
        const val BASE_URL_DEV = "https://appapidev999.shownow.live/shownowapi/"

        const val BASE_H5_URL_ONLINE = "https://m.shownow.live/"
        const val BASE_H5_URL_QA = "https://mqa666.shownow.live/"
        const val BASE_H5_URL_DEV = "https://mdev999.shownow.live/"


        const val HEADER_NO_CACHE = "no-cache, no-store, "
        const val VERSION_UPDATE = "http://7xk0r4.dl1.z0.glb.clouddn.com/version.json"
    }

    /**
     * 全路径
     * @return Observable<HomeEn>
     */
    @GET("https://api.mock.live/mock_android.json")
    fun getMock(): Observable<HomeEn>

    /**
     * Normal
     */
    @POST("pub/mock")
    fun postMock(@Body homeEn: HomeEn): Observable<Any>

}
