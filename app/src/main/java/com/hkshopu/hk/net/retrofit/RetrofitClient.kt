package com.hkshopu.hk.net.retrofit

import android.content.Context
import com.hkshopu.hk.BuildConfig
import com.hkshopu.hk.net.ApiConstants
import com.hkshopu.hk.net.GsonProvider
import com.hkshopu.hk.utils.FileUtils
import okhttp3.Cache
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.net.CookieHandler
import java.net.CookieManager
import java.util.concurrent.TimeUnit

/**
 * @Author: YangYang
 * @Date: 2017/12/26
 * @Version: 1.0.0
 * @Description:
 */
object RetrofitClient {

    //http连接最大时长
    private val HTTP_CONNECT_TIMEOUT = 20000L
    //http读取最大时长
    private val HTTP_READ_TIMEOUT = 20000L
    //http写出最大时长
    private val HTTP_WRITE_TIMEOUT = 20000L

    /**
     * 获取读取手机信息权限后重新初始化请求头信息
     */
    fun initBaseParams(context: Context) {
//        HeaderInterceptor.initBaseParam(context)
    }

    //OKHttp的相关配置,未配置log等
    private fun getClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        if (builder.interceptors() != null) {
            builder.interceptors().clear()
        }
        builder.addInterceptor(BaseParamsInterceptor())
//        builder.addInterceptor(CacheInterceptor())
        builder.addInterceptor(HeaderInterceptor())
//        builder.addInterceptor(ErrorReportInterceptor())
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(HttpLoggingInterceptor())
        }
        val cacheDir = File(FileUtils.getCachePath(), "response")
        val cache = Cache(cacheDir, 1024 * 1024 * 100)
        builder.cache(cache)
        val cookieHandler: CookieHandler = CookieManager()
        builder.connectTimeout(HTTP_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(HTTP_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(HTTP_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .cookieJar(JavaNetCookieJar(cookieHandler))
        return builder.build()
    }

    //获取Retrofit的实力
    private fun retrofit(): Retrofit {
//        val gson = GsonBuilder()
//            .registerTypeAdapter(List<T>,ListDefaultAdapter<T>)
        var domain = ApiConstants.API_HOST
//        if (domain.isNullOrEmpty()) {
//            domain = SharedPreUtils.getInstance().getString("apiDomain")
//        }
        if (domain.isNullOrEmpty()) {
            throw Exception("无法获取到api域名")
        }
        return Retrofit.Builder()
                .baseUrl(domain)
                .client(getClient())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(GsonProvider.gson))
                .build()
    }

    //获取接口的Service
    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit().create(serviceClass)
    }

}