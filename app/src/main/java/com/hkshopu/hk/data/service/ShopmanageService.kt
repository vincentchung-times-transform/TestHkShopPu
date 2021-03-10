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
    @POST("${ApiConstants.API_PATH}/user/socialLoginProcess")
    fun sociallogin(@Field("facebook_account") facebook_account : String,@Field("google_account") google_account : String,@Field("apple_account") apple_account: String) : Observable<BaseResponse<Any>>


}