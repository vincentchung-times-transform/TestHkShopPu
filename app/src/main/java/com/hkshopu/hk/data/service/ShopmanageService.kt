package com.HKSHOPU.hk.data.service

import com.HKSHOPU.hk.data.bean.BaseResponse

import com.HKSHOPU.hk.net.ApiConstants
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Path
import java.io.File

interface ShopmanageService{

    @FormUrlEncoded
    @POST("${ApiConstants.API_PATH}shop/checkShopNameIsExistsProcess/")
    fun shopnamecheck(@Field("shop_title") shop_title : String,) : Observable<BaseResponse<Any>>

    @FormUrlEncoded
    @POST("${ApiConstants.API_PATH}shop/save/")
    fun adddnewshop(@Field("shop_title") shop_title : String,) : Observable<BaseResponse<Any>>

    @FormUrlEncoded
    @POST("${ApiConstants.API_PATH}product/save/")
    fun add_product(@Field("shop_id") shop_id : String, @Field("product_category_id") product_category_id : String, @Field("product_sub_category_id") product_sub_category_id : String, @Field("product_title") product_title : String, @Field("quantity") quantity : Int, @Field("product_description") product_description : String, @Field("product_price") product_price : Int, @Field("shipping_fee") shipping_fee : Int, @Field("weight") weight : Int, @Field("new_secondhand") new_secondhand : String, @Field("product_pic_list") product_pic_list : MutableList<File>, @Field("product_spec_list") product_spec_list : String,  @Field("user_id") user_id: String, @Field("length") length : Int,  @Field("width") width : Int,  @Field("height") height : Int, @Field("shipment_method") shipment_method : String ) : Observable<BaseResponse<Any>>

    @FormUrlEncoded
    @POST("${ApiConstants.API_PATH}shop/{id}/shipmentSettings/")
    fun syncShippingfare(@Path("id") id : String, @Field("shipment_settings") shipment_settings : String) : Observable<BaseResponse<Any>>


    @FormUrlEncoded
    @POST("${ApiConstants.API_PATH}product/update_product_status_forAndroid/")
    fun updateProductStatus(@Field("id") id : String, @Field("status") status : String) : Observable<BaseResponse<Any>>


}