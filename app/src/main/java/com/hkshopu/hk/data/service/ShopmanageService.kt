package com.hkshopu.hk.data.service

import android.util.Log
import com.google.gson.reflect.TypeToken
import com.hkshopu.hk.data.bean.BaseResponse
import com.hkshopu.hk.data.bean.ShopInfoBean
import com.hkshopu.hk.net.ApiConstants
import com.hkshopu.hk.net.GsonProvider
import io.reactivex.Observable
import okhttp3.*
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.io.IOException

interface ShopmanageService{

    @FormUrlEncoded
    @POST("${ApiConstants.API_PATH}/shop/checkShopNameIsExistsProcess/")
    fun shopnamecheck(@Field("shop_title") shop_title : String,) : Observable<BaseResponse<Any>>

    @FormUrlEncoded
    @POST("${ApiConstants.API_PATH}/shop/save/")
    fun adddnewshop(@Field("shop_title") shop_title : String,) : Observable<BaseResponse<Any>>

}