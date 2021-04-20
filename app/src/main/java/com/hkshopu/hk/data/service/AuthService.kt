package com.hkshopu.hk.data.service

import com.hkshopu.hk.data.bean.BaseResponse
import com.hkshopu.hk.net.ApiConstants
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthService{
    @FormUrlEncoded
    @POST("${ApiConstants.API_PATH}/user/socialLoginProcess/")
    fun sociallogin(@Field("email") email : String,@Field("facebook_account") facebook_account : String,@Field("google_account") google_account : String,@Field("apple_account") apple_account: String) : Observable<BaseResponse<Any>>

    @FormUrlEncoded
    @POST("${ApiConstants.API_PATH}/user/registerProcess/")
    fun register(@Field("account_name") account_name : String,@Field("email") email : String,@Field("password") password : String,@Field("confirm_password") confirm_password : String,@Field("first_name") first_name : String,@Field("last_name") last_name : String,@Field("gender") gender : String,@Field("birthday") birthday : String,@Field("phone") phone : String,@Field("address") address : String,@Field("region") region : String,@Field("district") district : String,@Field("street_name") street_name : String,@Field("street_no") street_no : String,@Field("floor") floor : String,@Field("room") room : String) : Observable<BaseResponse<Any>>

    @FormUrlEncoded
    @POST("${ApiConstants.API_PATH}user/loginProcess/")
    fun login(@Field("email") email : String,@Field("password") password : String) : Observable<BaseResponse<Any>>

    @FormUrlEncoded
    @POST("${ApiConstants.API_PATH}user/generateAndSendValidationCodeProcess/")
    fun verifycode(@Field("email") email : String) : Observable<BaseResponse<Any>>

    @FormUrlEncoded
    @POST("${ApiConstants.API_PATH}user/validateEmailProcess/")
    fun emailverify(@Field("email") email : String,@Field("validation_code") validation_code: String) : Observable<BaseResponse<Any>>


    @FormUrlEncoded
    @POST("${ApiConstants.API_PATH}user/checkEmailExistsProcess/")
    fun emailcheck(@Field("email") email : String) : Observable<BaseResponse<Any>>


    @FormUrlEncoded
    @POST("${ApiConstants.API_PATH}user/resetPasswordProcess/")
    fun reset_password(@Field("email") email : String,@Field("password") password : String, @Field("confirm_password") confirm_password:String) : Observable<BaseResponse<Any>>

}